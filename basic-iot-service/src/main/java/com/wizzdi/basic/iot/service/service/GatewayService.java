package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.*;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.PendingGateway;
import com.wizzdi.basic.iot.service.data.GatewayRepository;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.response.ImportGatewaysResponse;
import com.wizzdi.basic.iot.service.response.RemoteUpdateResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.SecurityUserCreate;
import com.wizzdi.flexicore.security.request.TenantToUserCreate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.SecurityUserService;
import com.wizzdi.flexicore.security.service.TenantToUserService;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.request.MappedPOICreate;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.request.MappedPOIUpdate;
import com.wizzdi.maps.service.service.MappedPOIService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Extension
@Component

public class GatewayService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger("basic-iot");
    @Autowired
    private GatewayRepository repository;

    @Autowired
    private RemoteService remoteService;
    @Autowired
    private PendingGatewayService pendingGatewayService;
    @Autowired
    private SecurityUserService securityUserService;
    @Autowired
    private TenantToUserService tenantToUserService;
    @Autowired
    private MappedPOIService mappedPOIService;
    @Autowired
    @Qualifier("gatewayMapIcon")
    private MapIcon gatewayMapIcon;

    @Autowired
    @Lazy
    private PublicKeyService publicKeyService;

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
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


    public void validateFiltering(GatewayFilter gatewayFilter,
                                  SecurityContext securityContext) {
        remoteService.validateFiltering(gatewayFilter, securityContext);
    }
    public void validateFiltering(ApproveGatewaysRequest approveGatewaysRequest,
                                  SecurityContext securityContext) {
        Set<String> pendingGatewayIds = approveGatewaysRequest.getPendingGatewayIds();
        if (pendingGatewayIds == null || pendingGatewayIds.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "pendingGatewayIds must be provided");
        }
        Map<String, PendingGateway> pendingGatewayMap = listByIds(PendingGateway.class, pendingGatewayIds, securityContext)
                .stream()
                .collect(Collectors.toMap(PendingGateway::getId, f -> f, (a, b) -> a));
        Set<String> missing = new HashSet<>(pendingGatewayIds);
        missing.removeAll(pendingGatewayMap.keySet());
        if (!missing.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "No PendingGateway with ids " + missing);
        }
        List<PendingGateway> alreadyRegistered = pendingGatewayMap.values().stream()
                .filter(f -> f.getRegisteredGateway() != null)
                .toList();
        if (!alreadyRegistered.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "PendingGateway already registered ids " + alreadyRegistered.stream().map(PendingGateway::getId).toList());
        }
        approveGatewaysRequest.setPendingGateways(new ArrayList<>(pendingGatewayMap.values()));
        String tenantId = approveGatewaysRequest.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            Set<SecurityTenant> pendingTenants = pendingGatewayMap.values().stream()
                    .map(PendingGateway::getTenant)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SecurityTenant::getId))));
            if (pendingTenants.isEmpty()) {
                throw new ResponseStatusException(BAD_REQUEST, "PendingGateway records do not have a tenant");
            }
            if (pendingTenants.size() > 1) {
                throw new ResponseStatusException(BAD_REQUEST,
                        "PendingGateway records belong to multiple tenants; provide tenantId explicitly");
            }
            approveGatewaysRequest.setTargetTenant(pendingTenants.iterator().next());
            return;
        }
        SecurityTenant targetTenant = getByIdOrNull(tenantId, SecurityTenant.class, securityContext);
        if (targetTenant == null) {
            throw new ResponseStatusException(BAD_REQUEST, "No SecurityTenant with id " + tenantId);
        }
        approveGatewaysRequest.setTargetTenant(targetTenant);
    }

    public PaginationResponse<Gateway> getAllGateways(
            SecurityContext securityContext, GatewayFilter filtering) {
        List<Gateway> list = listAllGateways(securityContext, filtering);
        long count = repository.countAllGateways(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Gateway> listAllGateways(SecurityContext securityContext, GatewayFilter gatewayFilter) {
        return repository.getAllGateways(securityContext, gatewayFilter);
    }

    public Gateway createGateway(GatewayCreate creationContainer,
                                 SecurityContext securityContext) {
        Gateway gateway = createGatewayNoMerge(creationContainer, securityContext);
        repository.merge(gateway,null);
        return gateway;
    }

    public Gateway createGatewayNoMerge(GatewayCreate creationContainer,
                                        SecurityContext securityContext) {
        Gateway gateway = new Gateway();
        gateway.setId(UUID.randomUUID().toString());

        updateGatewayNoMerge(gateway, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(gateway, securityContext);
        return gateway;
    }

    public RemoteUpdateResponse updateGatewayNoMerge(Gateway gateway,
                                        GatewayCreate gatewayCreate) {
        RemoteUpdateResponse remoteUpdateResponse = remoteService.updateRemoteNoMerge(gateway, gatewayCreate);
        boolean update =remoteUpdateResponse.updated();
        if (gatewayCreate.getPublicKey() != null && !gatewayCreate.getPublicKey().equals(gateway.getPublicKey())) {
            gateway.setPublicKey(gatewayCreate.getPublicKey());
            update = true;
        }
        if(gatewayCreate.getApprovingUser()!=null&&(gateway.getApprovingUser()==null||!gatewayCreate.getApprovingUser().getId().equals(gateway.getApprovingUser().getId()))){
            gateway.setApprovingUser(gatewayCreate.getApprovingUser());
            update=true;
        }
        if(gatewayCreate.getGatewayUser()!=null&&(gateway.getGatewayUser()==null||!gatewayCreate.getGatewayUser().getId().equals(gateway.getGatewayUser().getId()))){
            gateway.setGatewayUser(gatewayCreate.getGatewayUser());
            update=true;
        }
        if(gatewayCreate.getNoSignatureCapabilities()!=null&&gatewayCreate.getNoSignatureCapabilities()!=gateway.isNoSignatureCapabilities()){
            gateway.setNoSignatureCapabilities(gatewayCreate.getNoSignatureCapabilities());
            update=true;
        }
        return new RemoteUpdateResponse(update,remoteUpdateResponse.remoteUpdatedEvent());
    }

    public Gateway updateGateway(GatewayUpdate gatewayUpdate,
                                 SecurityContext securityContext) {
        Gateway gateway = gatewayUpdate.getGateway();
        RemoteUpdateResponse remoteUpdateResponse = updateGatewayNoMerge(gateway, gatewayUpdate);
        if (remoteUpdateResponse.updated()) {
            repository.merge(gateway,remoteUpdateResponse.remoteUpdatedEvent());
        }
        invalidatePublicKey(gateway);
        return gateway;
    }

    private void invalidatePublicKey(Gateway gateway) {
        if(gateway==null||gateway.getRemoteId()==null){
            return;
        }
        publicKeyService.invalidateCache(gateway.getRemoteId());
    }

    public void validate(GatewayCreate gatewayCreate,
                         SecurityContext securityContext) {
        remoteService.validate(gatewayCreate, securityContext);

    }

    public void validateCreate(GatewayCreate gatewayCreate,
                         SecurityContext securityContext) {
        validate(gatewayCreate, securityContext);
        if(gatewayCreate.getGatewayUser()==null){
            gatewayCreate.setGatewayUser(securityContext.getUser());
        }
        if(gatewayCreate.getApprovingUser()==null){
            gatewayCreate.setApprovingUser(securityContext.getUser());
        }
    }

    public PaginationResponse<Gateway> approveGateways(SecurityContext securityContext, ApproveGatewaysRequest approveGatewaysRequest) {
        SecurityTenant targetTenant = approveGatewaysRequest.getTargetTenant();
        if (targetTenant != null) {
            securityContext.setTenantToCreateIn(targetTenant);
        }
        return approveGateways(securityContext, approveGatewaysRequest.getPendingGateways());
    }

    private PaginationResponse<Gateway> approveGateways(SecurityContext securityContext, List<PendingGateway> pendingGateways) {
        List<Gateway> response=new ArrayList<>();
        for (PendingGateway pendingGateway : pendingGateways) {
            SecurityUser gatewaySecurityUser=createGatewaySecurityUser(securityContext,pendingGateway.getGatewayId());
            GatewayCreate gatewayCreate=getGatwayCreate(pendingGateway)
                    .setApprovingUser(securityContext.getUser())
                    .setGatewayUser(gatewaySecurityUser);
            Gateway gateway = createGatewayNoMerge(gatewayCreate, securityContext);
            MappedPOI mappedPOI = getOrCreateGatewayMappedPOI(pendingGateway, gateway, securityContext);
            gateway.setMappedPOI(mappedPOI);
            repository.massMergePlain(List.of(mappedPOI,gateway));
            response.add(gateway);
            pendingGatewayService.updatePendingGateway(new PendingGatewayUpdate().setPendingGateway(pendingGateway).setRegisteredGateway(gateway), securityContext);
            invalidatePublicKey(gateway);
        }
        return new PaginationResponse<>(response, response.size(), response.size());
    }

    private MappedPOI getOrCreateGatewayMappedPOI(PendingGateway pendingGateway, Gateway gateway, SecurityContext securityContext) {
        MappedPOICreate create = new MappedPOICreate()
                .setExternalId(gateway.getRemoteId())
                .setMapIcon(gatewayMapIcon)
                .setRelatedType(Gateway.class.getCanonicalName())
                .setRelatedId(gateway.getId())
                .setLat(pendingGateway.getLat())
                .setLon(pendingGateway.getLon())
                .setName(gateway.getName());
        List<MappedPOI> existingPendingPoints = mappedPOIService.listAllMappedPOIs(new MappedPOIFilter()
                .setRelatedType(Collections.singleton(PendingGateway.class.getCanonicalName()))
                .setRelatedId(Collections.singleton(pendingGateway.getId())), securityContext);
        if (!existingPendingPoints.isEmpty()) {
            MappedPOI mappedPOI = existingPendingPoints.get(0);
            MappedPOIUpdate update = new MappedPOIUpdate().setMappedPOI(mappedPOI);
            update.setExternalId(gateway.getRemoteId());
            update.setMapIcon(gatewayMapIcon);
            update.setRelatedType(Gateway.class.getCanonicalName());
            update.setRelatedId(gateway.getId());
            update.setName(gateway.getName());
            update.setLat(pendingGateway.getLat());
            update.setLon(pendingGateway.getLon());
            mappedPOIService.updateMappedPOINoMerge(update, mappedPOI);
            return mappedPOI;
        }
        return mappedPOIService.createMappedPOINoMerge(create, securityContext);
    }

    public SecurityUser createGatewaySecurityUser(SecurityContext securityContext, String gatewayId) {
        List<Object> toMerge = new ArrayList<>();
        SecurityUser securityUser = createGatewaySecurityUserNoMerge(securityContext, gatewayId, toMerge);
        tenantToUserService.massMerge(toMerge);
        return securityUser;
    }

    public SecurityUser createGatewaySecurityUserNoMerge(SecurityContext securityContext, String gatewayId, List<Object> toMerge) {
        SecurityUserCreate securityUserCreate=new SecurityUserCreate()
                .setDescription("Automatically Created User for Gateway "+gatewayId)
                .setName(gatewayId+"-User");
        SecurityUser securityUser = securityUserService.createSecurityUserNoMerge(securityUserCreate, securityContext);
        TenantToUser tenantToUser = tenantToUserService.createTenantToUserNoMerge(new TenantToUserCreate().setUser(securityUser).setDefaultTenant(true).setTenant(securityContext.getTenantToCreateIn()), securityContext);
        toMerge.add(securityUser);
        toMerge.add(tenantToUser);
        return securityUser;
    }

    private GatewayCreate getGatwayCreate(PendingGateway pendingGateway) {
        return new GatewayCreate()
                .setNoSignatureCapabilities(pendingGateway.isNoSignatureCapabilities())
                .setPublicKey(pendingGateway.getPublicKey())
                .setRemoteId(pendingGateway.getGatewayId())
                .setReportedLat(pendingGateway.getLat())
                .setReportedLon(pendingGateway.getLon())
                .setName(pendingGateway.getName())
                .setDescription(pendingGateway.getDescription());
    }

    public void validate(ImportGatewaysRequest importGatewaysRequest, SecurityContext securityContext) {

        String csvId= importGatewaysRequest.getCsvId();;
        FileResource csv=csvId!=null?getByIdOrNull(csvId,FileResource.class, securityContext):null;
        if(csv==null){
            throw new ResponseStatusException(BAD_REQUEST,"csvId must be provided");
        }
        importGatewaysRequest.setCsv(csv);
    }


    public ImportGatewaysResponse importGateways(SecurityContext securityContext, ImportGatewaysRequest importGatewaysRequest) {
        String fullPath = importGatewaysRequest.getCsv()
                .getFullPath();
        try (Reader in = new InputStreamReader(new FileInputStream(fullPath),
                StandardCharsets.UTF_8)) {
            Map<String,PendingGatewayCreate> pendingGatewayCreates=new HashMap<>();

            CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                    .parse(in);
            boolean noSignatureCapabilitiesHeader = records.getHeaderNames().contains("noSignatureCapabilities");
            for (CSVRecord record : records) {
                String gatewayId = record.get("gatewayId");
                String publicKey = record.get("publicKey");
                boolean noSignatureCapabilities = noSignatureCapabilitiesHeader && Boolean.parseBoolean(record.get("noSignatureCapabilities"));
                pendingGatewayCreates.put(gatewayId,new PendingGatewayCreate().setPublicKey(publicKey).setGatewayId(gatewayId).setNoSignatureCapabilities(noSignatureCapabilities));

            }
            Map<String,Gateway> existing=pendingGatewayCreates.isEmpty()?new HashMap<>():listAllGateways(securityContext,new GatewayFilter().setRemoteIds(pendingGatewayCreates.keySet())).stream().collect(Collectors.toMap(f->f.getRemoteId(),f->f,(a,b)->a));
            List<PendingGateway> pendingGateways = pendingGatewayCreates.values().stream().filter(f->!existing.containsKey(f.getGatewayId())).map(f -> pendingGatewayService.createPendingGateway(f, securityContext)).collect(Collectors.toList());
            PaginationResponse<Gateway> gatewayPaginationResponse = approveGateways(securityContext, pendingGateways);

            return new ImportGatewaysResponse(gatewayPaginationResponse);
        } catch (IOException e) {
            logger.error("unable to open file " + fullPath,e);
        }
        return new ImportGatewaysResponse();
    }

}
