package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.data.RemoteRepository;
import com.wizzdi.basic.iot.service.request.RemoteCreate;
import com.wizzdi.basic.iot.service.request.RemoteFilter;
import com.wizzdi.basic.iot.service.request.RemoteUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class RemoteService implements Plugin {

    @Autowired
    private RemoteRepository repository;

    @Autowired
    private BasicService basicService;

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

    public void validateFiltering(RemoteFilter remoteFilter,
                                  SecurityContextBase securityContext) {
        basicService.validate(remoteFilter, securityContext);
        Set<String> productTypesIds = remoteFilter.getRemoteIds();
    }

    public PaginationResponse<Remote> getAllRemotes(
            SecurityContextBase securityContext, RemoteFilter filtering) {
        List<Remote> list = listAllRemotes(securityContext, filtering);
        long count = repository.countAllRemotes(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<Remote> listAllRemotes(SecurityContextBase securityContext, RemoteFilter remoteFilter) {
        return repository.getAllRemotes(securityContext, remoteFilter);
    }

    public Remote createRemote(RemoteCreate creationContainer,
                                 SecurityContextBase securityContext) {
        Remote remote = createRemoteNoMerge(creationContainer, securityContext);
        repository.merge(remote);
        return remote;
    }

    public Remote createRemoteNoMerge(RemoteCreate creationContainer,
                                        SecurityContextBase securityContext) {
        Remote remote = new Remote();
        remote.setId(Baseclass.getBase64ID());

        updateRemoteNoMerge(remote, creationContainer);
        BaseclassService.createSecurityObjectNoMerge(remote, securityContext);
        return remote;
    }

    public boolean updateRemoteNoMerge(Remote remote,
                                        RemoteCreate remoteCreate) {
        boolean update = basicService.updateBasicNoMerge(remoteCreate, remote);
        if (remoteCreate.getRemoteId() != null && !remoteCreate.getRemoteId().equals(remote.getRemoteId())) {
            remote.setRemoteId(remoteCreate.getRemoteId());
            update = true;
        }
        return update;
    }

    public Remote updateRemote(RemoteUpdate remoteUpdate,
                                 SecurityContextBase securityContext) {
        Remote remote = remoteUpdate.getRemote();
        if (updateRemoteNoMerge(remote, remoteUpdate)) {
            repository.merge(remote);
        }
        return remote;
    }

    public void validate(RemoteCreate remoteCreate,
                         SecurityContextBase securityContext) {
        basicService.validate(remoteCreate, securityContext);
    }
}