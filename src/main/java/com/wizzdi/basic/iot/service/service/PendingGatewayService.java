package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.PendingGateway;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.basic.iot.service.data.PendingGatewayRepository;
import com.wizzdi.basic.iot.service.request.DeviceCreate;
import com.wizzdi.basic.iot.service.request.PendingGatewayCreate;
import com.wizzdi.basic.iot.service.request.PendingGatewayFilter;
import com.wizzdi.basic.iot.service.request.PendingGatewayUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Extension
@Component

public class PendingGatewayService implements Plugin {

    @Autowired
    private PendingGatewayRepository repository;

    @Autowired
    private BasicService basicService;
    @Autowired
    private SecurityContextBase adminSecurityContext;

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

    public void validateFiltering(PendingGatewayFilter pendingGatewayFilter,
                                  SecurityContextBase securityContext) {
        basicService.validate(pendingGatewayFilter, securityContext);
    }

    public PaginationResponse<PendingGateway> getAllPendingGateways(
            SecurityContextBase securityContext, PendingGatewayFilter filtering) {
        List<PendingGateway> list = listAllPendingGateways(securityContext, filtering);
        long count = repository.countAllPendingGateways(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<PendingGateway> listAllPendingGateways(SecurityContextBase securityContext, PendingGatewayFilter pendingGatewayFilter) {
        return repository.getAllPendingGateways(securityContext, pendingGatewayFilter);
    }

    public PendingGateway createPendingGateway(PendingGatewayCreate creationContainer,
                                 SecurityContextBase securityContext) {
        PendingGateway pendingGateway = createPendingGatewayNoMerge(creationContainer, securityContext);
        repository.merge(pendingGateway);
        return pendingGateway;
    }

    public PendingGateway createPendingGatewayNoMerge(PendingGatewayCreate creationContainer,
                                        SecurityContextBase securityContext) {
        PendingGateway pendingGateway = new PendingGateway();
        pendingGateway.setId(UUID.randomUUID().toString());

        updatePendingGatewayNoMerge(pendingGateway, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(pendingGateway, securityContext);
        return pendingGateway;
    }

    public boolean updatePendingGatewayNoMerge(PendingGateway pendingGateway,
                                        PendingGatewayCreate pendingGatewayCreate) {
        boolean update = basicService.updateBasicNoMerge(pendingGatewayCreate, pendingGateway);
        if (pendingGatewayCreate.getGatewayId() != null && !pendingGatewayCreate.getGatewayId().equals(pendingGateway.getGatewayId())) {
            pendingGateway.setGatewayId(pendingGatewayCreate.getGatewayId());
            update = true;
        }
        if (pendingGatewayCreate.getPublicKey() != null && !pendingGatewayCreate.getPublicKey().equals(pendingGateway.getPublicKey())) {
            pendingGateway.setPublicKey(pendingGatewayCreate.getPublicKey());
            update = true;
        }
        if (pendingGatewayCreate.getRegisteredGateway() != null &&(pendingGateway.getRegisteredGateway()==null|| !pendingGatewayCreate.getRegisteredGateway().getId().equals(pendingGateway.getRegisteredGateway().getId()))) {
            pendingGateway.setRegisteredGateway(pendingGatewayCreate.getRegisteredGateway());
            update = true;
        }
        return update;
    }

    public PendingGateway updatePendingGateway(PendingGatewayUpdate pendingGatewayUpdate,
                                 SecurityContextBase securityContext) {
        PendingGateway pendingGateway = pendingGatewayUpdate.getPendingGateway();
        if (updatePendingGatewayNoMerge(pendingGateway, pendingGatewayUpdate)) {
            repository.merge(pendingGateway);
        }
        return pendingGateway;
    }

    public void validate(PendingGatewayCreate pendingGatewayCreate,
                         SecurityContextBase securityContext) {
        basicService.validate(pendingGatewayCreate, securityContext);

        String registeredGatewayId=pendingGatewayCreate.getRegisteredGatewayId();
        Gateway gateway=registeredGatewayId==null?null:getByIdOrNull(registeredGatewayId,Gateway.class, Remote_.security,securityContext);
        if(registeredGatewayId!=null&&gateway==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Gateway with id "+registeredGatewayId);
        }
        pendingGatewayCreate.setRegisteredGateway(gateway);
    }

    public void validate(PendingGatewayCreate pendingGatewayCreate) {
        if(pendingGatewayCreate.getRegisteredGatewayId()!=null){
            pendingGatewayCreate.setRegisteredGatewayId(null);
        }
        if(pendingGatewayCreate.getGatewayId()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"gateway id must be provided");
        }
        if(pendingGatewayCreate.getPublicKey()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"public key must be provided");
        }
        validate(pendingGatewayCreate,adminSecurityContext);

    }

    public PendingGateway registerGateway(PendingGatewayCreate pendingGatewayCreate) {
        return createPendingGateway(pendingGatewayCreate,adminSecurityContext);
    }
}