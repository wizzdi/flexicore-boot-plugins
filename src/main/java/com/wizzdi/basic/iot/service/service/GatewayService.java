package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecurityUser;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.PendingGateway;
import com.wizzdi.basic.iot.service.data.GatewayRepository;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.request.SecurityUserCreate;
import com.wizzdi.flexicore.security.request.TenantToUserCreate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.SecurityUserService;
import com.wizzdi.flexicore.security.service.TenantToUserService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class GatewayService implements Plugin {

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
        gateway.setId(Baseclass.getBase64ID());

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
    }

    public PaginationResponse<Gateway> approveGateways(SecurityContextBase securityContext, ApproveGatewaysRequest approveGatewaysRequest) {
        List<Gateway> response=new ArrayList<>();
        List<PendingGateway> pendingGateways = pendingGatewayService.listAllPendingGateways(securityContext, approveGatewaysRequest.getPendingGatewayFilter());
        for (PendingGateway pendingGateway : pendingGateways) {
            SecurityUser gatewaySecurityUser=createGatewaySecurityUser(securityContext,pendingGateway.getGatewayId());
            GatewayCreate gatewayCreate=getGatwayCreate(pendingGateway)
                    .setApprovingUser(securityContext.getUser())
                    .setGatewayUser(gatewaySecurityUser);
            Gateway gateway = createGateway(gatewayCreate, securityContext);
            response.add(gateway);
            pendingGatewayService.updatePendingGateway(new PendingGatewayUpdate().setPendingGateway(pendingGateway).setRegisteredGateway(gateway),securityContext);
        }
        return new PaginationResponse<>(response,response.size(),response.size());
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
                .setPublicKey(pendingGateway.getPublicKey())
                .setRemoteId(pendingGateway.getGatewayId())
                .setName(pendingGateway.getName())
                .setDescription(pendingGateway.getDescription());
    }

}