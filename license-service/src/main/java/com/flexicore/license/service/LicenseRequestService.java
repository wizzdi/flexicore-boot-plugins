package com.flexicore.license.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicore.license.data.LicenseRequestRepository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wizzdi.flexicore.encryption.service.CommonEncryptionService;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.request.FileResourceCreate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.flexicore.license.holders.LicenseHolder;
import com.flexicore.license.holders.RequestToLicenseEntityHolder;
import com.flexicore.license.holders.UpdateLicensingCache;
import com.flexicore.license.request.LicenseRequestCreate;
import com.flexicore.license.request.LicenseRequestFiltering;
import com.flexicore.license.request.LicenseRequestToEntityFiltering;
import com.flexicore.license.request.LicenseRequestUpdate;
import com.flexicore.model.*;
import com.flexicore.license.model.*;

import com.flexicore.security.SecurityContextBase;


import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.TinkConfig;
import com.google.crypto.tink.signature.PublicKeyVerifyFactory;

import com.wizzdi.flexicore.security.service.BaseclassService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;

import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;



@Extension
@Component
public class LicenseRequestService implements Plugin {

    private static List<String> cachedMacAdresses = null;
    private static final Queue<String> deviceSerialNumbers = new LinkedBlockingQueue<>();

    private static final Cache<String, OffsetDateTime> cachedLicense = Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();


    @Autowired

    private LicenseRequestRepository repository;

    @Autowired
    private BasicService basicService;
    @Autowired

    private LicenseRequestToEntityService licenseRequestToEntityService;

    @Autowired
    private FileResourceService fileResourceService;

    @Autowired
    private CommonEncryptionService commonEncryptionService;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${flexicore.license.publicKey:/home/flexicore/license/public.key}")
    private String publicKeyPath;
    @Value("${flexicore.license.timeShiftLocation:/home/flexicore/timeshift}")
    private String timeShiftLocation;


   private static final Logger logger = LoggerFactory.getLogger(LicenseRequestService.class);

    @Autowired
    private ApplicationEventPublisher updateLicensingCacheEvent;


   public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return repository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return repository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return repository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        repository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        repository.massMerge(toMerge);
    }
    public LicenseRequest createLicenseRequest(LicenseRequestCreate licenseRequestCreate, SecurityContextBase securityContext) {
        List<Object> toMerge=new ArrayList<>();
        String macAddress = null;
        try {
            List<String> hardwareAddress = getHardwareAddress();
            if (!hardwareAddress.isEmpty()) {
                macAddress = hardwareAddress.get(0);
            }
        } catch (SocketException e) {
            logger.error("failed getting mac addresses", e);
        }
        licenseRequestCreate.setMacAddress(macAddress);
        LicenseRequest licenseRequest = createLicenseRequestNoMerge(licenseRequestCreate, securityContext);
        List<LicenseRequest> otherRequestsForTenant=listAllLicenseRequests(new LicenseRequestFiltering().setRelatedTenant(Collections.singletonList(licenseRequestCreate.getLicensedTenant())),null);
        for (LicenseRequest request : otherRequestsForTenant) {
            request.setSoftDelete(true);
            toMerge.add(request);
        }
        toMerge.add(licenseRequest);
        repository.massMerge(toMerge);
        updateLicenseFile(licenseRequest, securityContext);
        return licenseRequest;
    }

    @EventListener
    public void licenseRequestUpdated(LicenseRequestUpdateEvent licenseRequest) {
        logger.info("License request updated event");
        updateLicenseFile(licenseRequest.getLicenseRequest(), licenseRequest.getSecurityContextBase());
        updateLicensingCacheEvent.publishEvent(new UpdateLicensingCache());
    }

    public LicenseRequest createLicenseRequestNoMerge(LicenseRequestCreate licenseRequestCreate, SecurityContextBase securityContext) {
        LicenseRequest licenseRequest = new LicenseRequest();
        licenseRequest.setId(Baseclass.getBase64ID());
        BaseclassService.createSecurityObjectNoMerge(licenseRequest,securityContext);
        updateLicenseRequestNoMerge(licenseRequest, licenseRequestCreate);
        return licenseRequest;
    }

    private boolean updateLicenseRequestNoMerge(LicenseRequest licenseRequest, LicenseRequestCreate licenseRequestCreate) {
        boolean update = basicService.updateBasicNoMerge(licenseRequestCreate, licenseRequest);
        if (licenseRequestCreate.getMacAddress() != null && !licenseRequestCreate.getMacAddress().equals(licenseRequest.getMacAddress())) {
            licenseRequest.setMacAddress(licenseRequestCreate.getMacAddress());
            update = true;
        }
        if (licenseRequestCreate.getDiskSerialNumber() != null && !licenseRequestCreate.getDiskSerialNumber().equals(licenseRequest.getDiskSerialNumber())) {
            licenseRequest.setDiskSerialNumber(licenseRequestCreate.getDiskSerialNumber());
            update = true;
        }

        if (licenseRequestCreate.getExternalHWSerialNumber() != null && !licenseRequestCreate.getExternalHWSerialNumber().equals(licenseRequest.getExternalHWSerialNumber())) {
            licenseRequest.setExternalHWSerialNumber(licenseRequestCreate.getExternalHWSerialNumber());
            update = true;
        }
        if (licenseRequestCreate.getLicense() != null && (licenseRequest.getLicense() == null || !licenseRequestCreate.getLicense().getId().equals(licenseRequest.getLicense().getId()))) {
            licenseRequest.setLicense(licenseRequestCreate.getLicense());
            update = true;
        }
        if (licenseRequestCreate.getLicensedTenant() != null && (licenseRequest.getLicensedTenant() == null || !licenseRequestCreate.getLicensedTenant().getId().equals(licenseRequest.getLicensedTenant().getId()))) {
            licenseRequest.setLicensedTenant(licenseRequestCreate.getLicensedTenant());
            update = true;
        }
        if (licenseRequestCreate.getRequestFile() != null && (licenseRequest.getRequestFile() == null || !licenseRequestCreate.getRequestFile().getId().equals(licenseRequest.getRequestFile().getId()))) {
            licenseRequest.setRequestFile(licenseRequestCreate.getRequestFile());
            update = true;

        }
        return update;
    }


    public LicenseRequest updateLicenseRequest(LicenseRequestUpdate licenseRequestUpdate, SecurityContextBase securityContext) {
        LicenseRequest licenseRequest = licenseRequestUpdate.getLicenseRequest();
        if (updateLicenseRequestNoMerge(licenseRequest, licenseRequestUpdate)) {
            repository.merge(licenseRequest);
            updateLicenseFile(licenseRequest, securityContext);

        }
        return licenseRequest;
    }

    public List<LicenseRequest> listAllLicenseRequests(LicenseRequestFiltering licenseRequestFiltering, SecurityContextBase securityContext) {
        return repository.listAllLicenseRequests(licenseRequestFiltering, securityContext);
    }

    public void validate(LicenseRequestCreate licenseRequestCreate, SecurityContextBase securityContext) {
        basicService.validate(licenseRequestCreate, securityContext);
        String licenseId = licenseRequestCreate.getLicenseId();
        FileResource license = licenseId != null ? getByIdOrNull(licenseId, FileResource.class, null, securityContext) : null;
        if (license == null && licenseId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No FileResource with id " + licenseId);
        }
        licenseRequestCreate.setLicense(license);
        String licensedTenantId = licenseRequestCreate.getLicensedTenantId();
        SecurityTenant tenant = licensedTenantId != null ? getByIdOrNull(licensedTenantId, SecurityTenant.class,SecuredBasic_.security,  securityContext) : null;
        if (licensedTenantId!=null&&tenant == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No SecurityTenant with id " + licensedTenantId);
        }
        licenseRequestCreate.setLicensedTenant(tenant);



    }

    private void addToCache(LicenseRequest licenseRequest, LicenseHolder holder) {
        for (RequestToLicenseEntityHolder entity : holder.getEntities()) {
            cachedLicense.put(entity.getCanonicalName(), entity.isPerpetual() ? OffsetDateTime.MAX: entity.getExpiration());

        }

    }

    private PublicKeyVerify getPublicKey() throws GeneralSecurityException, IOException {
        TinkConfig.register();
        File file = new File(publicKeyPath);
        KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(file));
        return PublicKeyVerifyFactory.getPrimitive(keysetHandle);

    }

    private boolean isLicenseValid(LicenseRequest licenseRequest, LicenseHolder holder) {
        try {
            if(licenseRequest.getLicense()!=null){
                File file = new File(licenseRequest.getLicense().getFullPath());
                PublicKeyVerify publicKeyVerify = getPublicKey();

                byte[] signed = Files.readAllBytes(file.toPath());
                byte[] plain = objectMapper.writeValueAsBytes(holder);
                publicKeyVerify.verify(signed, plain);
                return true;
            }

        } catch (IOException | GeneralSecurityException e) {
            logger.error("verification failed", e);
        }
        return false;
    }


    private boolean hasPublicKey() {
        return new File(publicKeyPath).exists();
    }

    private List<String> getHardwareAddress() throws SocketException {
        if (cachedMacAdresses == null) {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            List<String> macs = new ArrayList<>();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface n = networkInterfaces.nextElement();
                if (n.isVirtual() || n.isLoopback() || n.isPointToPoint() || !n.isUp()) {
                    continue;
                }
                byte[] hardwareAddress = n.getHardwareAddress();
                if (hardwareAddress != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < hardwareAddress.length; i++) {
                        sb.append(String.format("%02X%s", hardwareAddress[i], (i < hardwareAddress.length - 1) ? "-" : ""));
                    }
                    macs.add(sb.toString());

                }
            }
            cachedMacAdresses = macs;
        }
        return cachedMacAdresses;


    }

    public boolean isFeatureLicensed(SecurityUser user, List<SecurityTenant> tenants, LicensingFeature feature) {
        Instant instant = Instant.now();
        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime featureExpirationDate = cachedLicense.getIfPresent(feature.getCanonicalName());
        OffsetDateTime productExpirationDate = feature.getProduct() != null && feature.getProduct().getCanonicalName() != null ? cachedLicense.getIfPresent(feature.getProduct().getCanonicalName()) : null;
        if ((featureExpirationDate != null && featureExpirationDate.isAfter(now)) || (productExpirationDate != null && productExpirationDate.isAfter(now))) {
            return true;
        }
        if (isTimeWasManuallyShifted(System.currentTimeMillis())) {
            logger.warn("Time shift detected");
            return false;
        }
        if (!hasPublicKey()) {
            return false;
        }
        List<String> macs = new ArrayList<>();

        try {
            macs = getHardwareAddress();

        } catch (SocketException e) {
            logger.warn("could not get machine mac", e);
        }

        List<LicenseRequest> licenseRequests = getSuitableFeatureLicenseRequests(tenants, feature, macs, deviceSerialNumbers);
        if (feature.getProduct() != null) {

            licenseRequests.addAll(getSuitableProductLicenseRequests(tenants, feature.getProduct(), macs, deviceSerialNumbers));
        }
        for (LicenseRequest licenseRequest : licenseRequests) {
            if (isLicenseValid(licenseRequest)) {
                return true;
            }
        }

        return false;


    }

    public boolean isLicenseValid(LicenseRequest licenseRequest) {
        LicenseHolder holder = getLicenseHolder(licenseRequest);

        if (isLicenseValid(licenseRequest, holder)) {
            addToCache(licenseRequest, holder);
            return true;
        }
        return false;
    }

    private List<LicenseRequest> getSuitableProductLicenseRequests(List<SecurityTenant> tenants, LicensingProduct product, Collection<String> macs, Collection<String> deviceSerialNumbers) {
        LicenseRequestFiltering licenseRequestFiltering = new LicenseRequestFiltering()
                .setSigned(true)
                .setExpirationDateAfter(OffsetDateTime.now())
                .setLicensingProducts(Collections.singletonList(product))
                .setRelatedTenantsIds(tenants.parallelStream().map(f ->f.getId()).collect(Collectors.toSet()));
        List<LicenseRequest> signed = listAllLicenseRequests(licenseRequestFiltering, null);
        List<LicenseRequest> suitable = new ArrayList<>();
        for (LicenseRequest licenseRequest : signed) {
            if (macs.contains(licenseRequest.getMacAddress()) || (licenseRequest.getDiskSerialNumber() != null && deviceSerialNumbers.contains(licenseRequest.getDiskSerialNumber())) || (licenseRequest.getExternalHWSerialNumber() != null && deviceSerialNumbers.contains(licenseRequest.getExternalHWSerialNumber()))) {
                suitable.add(licenseRequest);
            }
        }
        return suitable;
    }


    private List<LicenseRequest> getSuitableFeatureLicenseRequests(List<SecurityTenant> tenants, LicensingFeature feature, Collection<String> macs, Collection<String> deviceSerialNumbers) {
        LicenseRequestFiltering licenseRequestFiltering = new LicenseRequestFiltering()
                .setSigned(true)
                .setExpirationDateAfter(OffsetDateTime.now())
                .setLicensingFeatures(Collections.singletonList(feature))
                .setRelatedTenantsIds(tenants.parallelStream().map(f ->f.getId()).collect(Collectors.toSet()));
        List<LicenseRequest> signed = listAllLicenseRequests(licenseRequestFiltering, null);
        List<LicenseRequest> suitable = new ArrayList<>();
        for (LicenseRequest licenseRequest : signed) {
            if ((licenseRequest.getMacAddress() != null && macs.contains(licenseRequest.getMacAddress())) || (licenseRequest.getDiskSerialNumber() != null && deviceSerialNumbers.contains(licenseRequest.getDiskSerialNumber())) || (licenseRequest.getExternalHWSerialNumber() != null && deviceSerialNumbers.contains(licenseRequest.getExternalHWSerialNumber()))) {
                suitable.add(licenseRequest);
            }
        }
        return suitable;
    }


    private static final String timeShiftKey = "YvnxVsqaPahye6jmHDjKqTuEruRBx9Mc6sFHjafttUm947CSBhnmfW5Jfa4VaMVEzQNvxrTRajwbY2tWebaxMhRGGqe3R8R7WazR";

    private boolean isTimeWasManuallyShifted(long currentTime) {
        File timeShift = new File(timeShiftLocation);
        if (timeShift.exists()) {
            try {
                String s = FileUtils.readFileToString(timeShift, StandardCharsets.UTF_8);
                String decrypted = new String(commonEncryptionService.decrypt(Base64.getDecoder().decode(s), timeShiftKey.getBytes()), StandardCharsets.UTF_8);
                long previouslyLoggedTime = Long.parseLong(decrypted);
                boolean shifted = previouslyLoggedTime > currentTime;
                if (!shifted) {
                    updateTimeShiftFile(currentTime, timeShift);
                }
                return shifted;
            } catch (Exception e) {
                logger.error("unable to read timeshift file", e);
            }
        }
        return true;
    }

    private void updateTimeShiftFile(long currentTime, File timeShift) throws GeneralSecurityException, IOException {
        File parentFile = timeShift.getParentFile();
        if(!parentFile.exists()){
            if(!parentFile.mkdirs()){
                logger.warn("Failed creating timeshift file");
            }
        }
        String timeString = currentTime + "";
        String encrypted = Base64.getEncoder().encodeToString(commonEncryptionService.encrypt(timeString.getBytes(StandardCharsets.UTF_8), timeShiftKey.getBytes()));
        FileUtils.write(timeShift, encrypted, StandardCharsets.UTF_8, false);
    }


    public void updateLicenseFile(LicenseRequest licenseRequest, SecurityContextBase securityContext) {
        LicenseHolder licenseHolder = getLicenseHolder(licenseRequest);
        FileResource fileResource = licenseRequest.getRequestFile();
        File file = fileResource != null ? new File(fileResource.getFullPath()) : new File(fileResourceService.generateNewPathForFileResource(licenseRequest.getId() + ".req", securityContext.getUser()));
        try {
            objectMapper.writeValue(file, licenseHolder);
            FileResourceCreate requestFile = new FileResourceCreate()
                    .setFullPath(file.getPath())
                    .setName("requestFile");
            if (fileResource == null) {
                fileResource = fileResourceService.createFileResourceNoMerge(requestFile, securityContext);
                licenseRequest.setRequestFile(fileResource);
            }
            repository.massMerge(Arrays.asList(fileResource, licenseRequest));
            if(isLicenseValid(licenseRequest,licenseHolder)){
                File timeShift = new File(timeShiftLocation);

                if (!timeShift.exists()) {
                    try {
                        updateTimeShiftFile(System.currentTimeMillis(), timeShift);
                    } catch (GeneralSecurityException | IOException e) {
                        logger.error("failed updating license file", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("unable to get license File");
        }
    }

    public LicenseHolder getLicenseHolder(LicenseRequest licenseRequest) {
        LicenseRequestToEntityFiltering licenseRequestToEntityFiltering = new LicenseRequestToEntityFiltering()
                .setLicenseRequests(Collections.singletonList(licenseRequest));
        List<LicenseRequestToEntity> links = licenseRequestToEntityService.listAllLicenseRequestToEntities(licenseRequestToEntityFiltering, null);
        return new LicenseHolder(licenseRequest,links);
    }

    public void validate(LicenseRequestFiltering licenseRequestFiltering, SecurityContextBase securityContext) {
        basicService.validate(licenseRequestFiltering, securityContext);
    }

    public PaginationResponse<LicenseRequest> getAllLicenseRequests(LicenseRequestFiltering licenseRequestFiltering, SecurityContextBase securityContext) {
        List<LicenseRequest> list = listAllLicenseRequests(licenseRequestFiltering, securityContext);
        long count = repository.countAllLicenseRequests(licenseRequestFiltering, securityContext);
        return new PaginationResponse<>(list, licenseRequestFiltering, count);
    }


    public void validateCreate(LicenseRequestCreate licenseRequestCreate,SecurityContextBase securityContext) {
        validate(licenseRequestCreate,securityContext);
        if(licenseRequestCreate.getLicensedTenant()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"SecurityTenant Must be provided");
        }

    }
}