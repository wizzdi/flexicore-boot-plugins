package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.model.SecurityUser;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.PendingGateway;
import com.wizzdi.basic.iot.service.data.GatewayRepository;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.response.ImportGatewaysResponse;
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
import com.wizzdi.maps.service.service.MappedPOIService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class GatewayService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(GatewayService.class);
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

    public void validateFiltering(GatewayFilter gatewayFilter,
                                  SecurityContextBase securityContext) {
        remoteService.validateFiltering(gatewayFilter, securityContext);
    }
    public void validateFiltering(ApproveGatewaysRequest gatewayFilter,
                                  SecurityContextBase securityContext) {
        if(gatewayFilter.getPendingGatewayFilter()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"pendingGatewayFilter must be provided");
        }
        gatewayFilter.getPendingGatewayFilter().setRegistered(false);
        pendingGatewayService.validateFiltering(gatewayFilter.getPendingGatewayFilter(), securityContext);
    }

    public PaginationResponse<Gateway> getAllGateways(
            SecurityContextBase securityContext, GatewayFilter filtering) {
        List<Gateway> list = listAllGateways(securityContext, filtering);
        long count = repository.countAllGateways(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Gateway> listAllGateways(SecurityContextBase securityContext, GatewayFilter gatewayFilter) {
        return repository.getAllGateways(securityContext, gatewayFilter);
    }

    public Gateway createGateway(GatewayCreate creationContainer,
                                 SecurityContextBase securityContext) {
        Gateway gateway = createGatewayNoMerge(creationContainer, securityContext);
        repository.merge(gateway);
        return gateway;
    }

    public Gateway createGatewayNoMerge(GatewayCreate creationContainer,
                                        SecurityContextBase securityContext) {
        Gateway gateway = new Gateway();
        gateway.setId(UUID.randomUUID().toString());

        updateGatewayNoMerge(gateway, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(gateway, securityContext);
        return gateway;
    }

    public boolean updateGatewayNoMerge(Gateway gateway,
                                        GatewayCreate gatewayCreate) {
        boolean update = remoteService.updateRemoteNoMerge(gateway, gatewayCreate);
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
        return update;
    }

    public Gateway updateGateway(GatewayUpdate gatewayUpdate,
                                 SecurityContextBase securityContext) {
        Gateway gateway = gatewayUpdate.getGateway();
        if (updateGatewayNoMerge(gateway, gatewayUpdate)) {
            repository.merge(gateway);
        }
        return gateway;
    }

    public void validate(GatewayCreate gatewayCreate,
                         SecurityContextBase securityContext) {
        remoteService.validate(gatewayCreate, securityContext);
        if(gatewayCreate.getGatewayUser()==null){
            gatewayCreate.setGatewayUser(securityContext.getUser());
        }
        if(gatewayCreate.getApprovingUser()==null){
            gatewayCreate.setApprovingUser(securityContext.getUser());
        }
    }

    public PaginationResponse<Gateway> approveGateways(SecurityContextBase securityContext, ApproveGatewaysRequest approveGatewaysRequest) {
        PendingGatewayFilter pendingGatewayFilter = approveGatewaysRequest.getPendingGatewayFilter();
        pendingGatewayFilter.setRegistered(false);
        List<PendingGateway> pendingGateways = pendingGatewayService.listAllPendingGateways(securityContext, pendingGatewayFilter);
        return approveGateways(securityContext, pendingGateways);
    }

    private PaginationResponse<Gateway> approveGateways(SecurityContextBase securityContext, List<PendingGateway> pendingGateways) {
        List<Gateway> response=new ArrayList<>();
        for (PendingGateway pendingGateway : pendingGateways) {
            SecurityUser gatewaySecurityUser=createGatewaySecurityUser(securityContext,pendingGateway.getGatewayId());
            GatewayCreate gatewayCreate=getGatwayCreate(pendingGateway)
                    .setApprovingUser(securityContext.getUser())
                    .setGatewayUser(gatewaySecurityUser);
            Gateway gateway = createGatewayNoMerge(gatewayCreate, securityContext);
            MappedPOI mappedPOI = mappedPOIService.createMappedPOINoMerge(new MappedPOICreate().setExternalId(gateway.getRemoteId()).setMapIcon(gatewayMapIcon).setRelatedType(Gateway.class.getCanonicalName()).setRelatedId(gateway.getId()).setName(gateway.getName()), securityContext);
            gateway.setMappedPOI(mappedPOI);
            massMerge(List.of(mappedPOI,gateway));
            response.add(gateway);
            pendingGatewayService.updatePendingGateway(new PendingGatewayUpdate().setPendingGateway(pendingGateway).setRegisteredGateway(gateway), securityContext);
        }
        return new PaginationResponse<>(response, response.size(), response.size());
    }

    private SecurityUser createGatewaySecurityUser(SecurityContextBase securityContext, String gatewayId) {
        SecurityUserCreate securityUserCreate=new SecurityUserCreate()
                .setDescription("Automatically Created User for Gateway "+gatewayId)
                .setName(gatewayId+"-User");
        SecurityUser securityUser = securityUserService.createSecurityUser(securityUserCreate, securityContext);
        tenantToUserService.createTenantToUser(new TenantToUserCreate().setSecurityUser(securityUser).setDefaultTenant(true).setTenant(securityContext.getTenantToCreateIn()),securityContext);
        return securityUser;
    }

    private GatewayCreate getGatwayCreate(PendingGateway pendingGateway) {
        return new GatewayCreate()
                .setNoSignatureCapabilities(pendingGateway.isNoSignatureCapabilities())
                .setPublicKey(pendingGateway.getPublicKey())
                .setRemoteId(pendingGateway.getGatewayId())
                .setName(pendingGateway.getName())
                .setDescription(pendingGateway.getDescription());
    }

    public void validate(ImportGatewaysRequest importGatewaysRequest, SecurityContextBase securityContext) {

        String csvId= importGatewaysRequest.getCsvId();;
        FileResource csv=csvId!=null?getByIdOrNull(csvId,FileResource.class, SecuredBasic_.security,securityContext):null;
        if(csv==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"csvId must be provided");
        }
        importGatewaysRequest.setCsv(csv);
    }

    public ImportGatewaysResponse importGateways(SecurityContextBase securityContext, ImportGatewaysRequest importGatewaysRequest) {
        String fullPath = importGatewaysRequest.getCsv()
                .getFullPath();
        try (Reader in = new InputStreamReader(new FileInputStream(fullPath),
                StandardCharsets.UTF_8)) {
            Map<String,PendingGatewayCreate> pendingGatewayCreates=new HashMap<>();

            CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                    .parse(in);
            for (CSVRecord record : records) {
                String gatewayId = record.get("gatewayId");
                String publicKey = record.get("publicKey");
                pendingGatewayCreates.put(gatewayId,new PendingGatewayCreate().setPublicKey(publicKey).setGatewayId(gatewayId));

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