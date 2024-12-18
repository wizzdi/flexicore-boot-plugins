package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.ConnectivityChange;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.basic.iot.service.data.ConnectivityChangeRepository;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeCreate;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeFilter;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeUpdate;
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

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class ConnectivityChangeService implements Plugin {

    @Autowired
    private ConnectivityChangeRepository repository;

    @Autowired
    private BasicService basicService;

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

    @Transactional
    public void merge(Object base) {
        repository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        repository.massMerge(toMerge);
    }

    public void validateFiltering(ConnectivityChangeFilter connectivityChangeFilter,
                                  SecurityContext securityContext) {
        basicService.validate(connectivityChangeFilter, securityContext);
        Set<String> remoteIds=connectivityChangeFilter.getRemoteIds();
        Map<String,Remote> remoteMap=remoteIds.isEmpty()?new HashMap<>():listByIds(Remote.class,remoteIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
        remoteIds.removeAll(remoteMap.keySet());
        if(!remoteIds.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Remote with ids "+remoteIds);
        }
        connectivityChangeFilter.setRemotes(new ArrayList<>(remoteMap.values()));
    }

    public PaginationResponse<ConnectivityChange> getAllConnectivityChanges(
            SecurityContext securityContext, ConnectivityChangeFilter filtering) {
        List<ConnectivityChange> list = listAllConnectivityChanges(securityContext, filtering);
        long count = repository.countAllConnectivityChanges(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<ConnectivityChange> listAllConnectivityChanges(SecurityContext securityContext, ConnectivityChangeFilter connectivityChangeFilter) {
        return repository.getAllConnectivityChanges(securityContext, connectivityChangeFilter);
    }

    public ConnectivityChange createConnectivityChange(ConnectivityChangeCreate creationContainer,
                                 SecurityContext securityContext) {
        ConnectivityChange connectivityChange = createConnectivityChangeNoMerge(creationContainer, securityContext);
        repository.merge(connectivityChange);
        return connectivityChange;
    }

    public ConnectivityChange createConnectivityChangeNoMerge(ConnectivityChangeCreate creationContainer,
                                        SecurityContext securityContext) {
        ConnectivityChange connectivityChange = new ConnectivityChange();
        connectivityChange.setId(UUID.randomUUID().toString());

        updateConnectivityChangeNoMerge(connectivityChange, creationContainer);
        if(connectivityChange.getRemote()!=null){
            connectivityChange.setSecurityId(connectivityChange.getRemote().getSecurityId());
        }
        else{
            throw new RuntimeException("cannot create ConnectivityChange without remote");
        }
        return connectivityChange;
    }

    public boolean updateConnectivityChangeNoMerge(ConnectivityChange connectivityChange,
                                        ConnectivityChangeCreate connectivityChangeCreate) {
        if(connectivityChange.getName()==null&&connectivityChangeCreate.getName()==null&&connectivityChangeCreate.getRemote()!=null&&connectivityChangeCreate.getConnectivity()!=null&&connectivityChangeCreate.getDate()!=null){
            connectivityChangeCreate.setName(getConnectivityChangeName(connectivityChangeCreate));
        }
        boolean update = basicService.updateBasicNoMerge(connectivityChangeCreate, connectivityChange);

        if (connectivityChangeCreate.getConnectivity() != null && !connectivityChangeCreate.getConnectivity().equals(connectivityChange.getConnectivity())) {
            connectivityChange.setConnectivity(connectivityChangeCreate.getConnectivity());
            update = true;
        }
        if (connectivityChangeCreate.getDate() != null && !connectivityChangeCreate.getDate().equals(connectivityChange.getDate())) {
            connectivityChange.setDate(connectivityChangeCreate.getDate());
            update = true;
        }
        if (connectivityChangeCreate.getRemote() != null && (connectivityChange.getRemote()==null||!connectivityChangeCreate.getRemote().getId().equals(connectivityChange.getRemote().getId()))) {
            connectivityChange.setRemote(connectivityChangeCreate.getRemote());
            update = true;
        }
        return update;
    }

    public static String getConnectivityChangeName(ConnectivityChangeCreate connectivityChangeCreate) {
        return connectivityChangeCreate.getRemote().getRemoteId() + " changed to " + connectivityChangeCreate.getConnectivity() + " at " + connectivityChangeCreate.getDate();
    }

    public ConnectivityChange updateConnectivityChange(ConnectivityChangeUpdate connectivityChangeUpdate,
                                 SecurityContext securityContext) {
        ConnectivityChange connectivityChange = connectivityChangeUpdate.getConnectivityChange();
        if (updateConnectivityChangeNoMerge(connectivityChange, connectivityChangeUpdate)) {
            repository.merge(connectivityChange);
        }
        return connectivityChange;
    }

    public ConnectivityChange updateConnectivityChange(ConnectivityChangeCreate connectivityChangeUpdate,
                                                      ConnectivityChange connectivityChange) {
        if (updateConnectivityChangeNoMerge(connectivityChange, connectivityChangeUpdate)) {
            repository.merge(connectivityChange);
        }
        return connectivityChange;
    }

    public void validate(ConnectivityChangeCreate connectivityChangeCreate,
                         SecurityContext securityContext) {
        basicService.validate(connectivityChangeCreate, securityContext);
        String remoteId=connectivityChangeCreate.getRemoteId();
        Remote remote=remoteId==null?null:getByIdOrNull(remoteId,Remote.class, securityContext);
        if(remoteId!=null&&remote==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Remote with id "+remoteId);
        }
        connectivityChangeCreate.setRemote(remote);
    }


}
