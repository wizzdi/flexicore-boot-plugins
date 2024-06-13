package com.wizzdi.basic.iot.service.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.data.StateHistoryRepository;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Extension
@Component

public class StateHistoryService implements Plugin {

    private static final Logger logger= LoggerFactory.getLogger(StateHistoryService.class);

    @Autowired
    private StateHistoryRepository repository;

    @Autowired
    private BasicService basicService;
    @Autowired
    private ConnectivityChangeService connectivityChangeService;
    @Autowired
    @Lazy
    private SecurityContextProvider securityContextProvider;
    @Autowired
    private RemoteService remoteService;

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

    public void validateFiltering(StateHistoryFilter stateHistoryFilter,
                                  SecurityContextBase securityContext) {
        basicService.validate(stateHistoryFilter, securityContext);
        if(stateHistoryFilter.getRemoteFilter()!=null){
            remoteService.validateFiltering(stateHistoryFilter.getRemoteFilter(),securityContext);
        }
    }

    public PaginationResponse<StateHistory> getAllStateHistories(
            SecurityContextBase securityContext, StateHistoryFilter filtering) {
        List<StateHistory> list = listAllStateHistories(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, 0)
                .setTotalRecords(null)
                .setEndPage(null)
                .setTotalPages(null);
    }

    public List<StateHistory> listAllStateHistories(SecurityContextBase securityContext, StateHistoryFilter stateHistoryFilter) {
        return repository.getAllStateHistories(securityContext, stateHistoryFilter);
    }

    public StateHistory createStateHistory(StateHistoryCreate creationContainer,
                                 SecurityContextBase securityContext) {
        StateHistory stateHistory = createStateHistoryNoMerge(creationContainer, securityContext);
        repository.merge(stateHistory);
        return stateHistory;
    }

    public StateHistory createStateHistoryNoMerge(StateHistoryCreate creationContainer,
                                        SecurityContextBase securityContext) {
        StateHistory stateHistory = new StateHistory();
        stateHistory.setId(UUID.randomUUID().toString());

        updateStateHistoryNoMerge(stateHistory, creationContainer);
        if(stateHistory.getRemote()!=null){
            stateHistory.setSecurity(stateHistory.getRemote().getSecurity());
        }
        else{
            throw new RuntimeException("cannot create state history without remote");
        }
        return stateHistory;
    }

    public boolean updateStateHistoryNoMerge(StateHistory stateHistory,
                                        StateHistoryCreate stateHistoryCreate) {
        boolean update = basicService.updateBasicNoMerge(stateHistoryCreate, stateHistory);

        if (stateHistoryCreate.getTimeAtState() != null && !stateHistoryCreate.getTimeAtState().equals(stateHistory.getTimeAtState())) {
            stateHistory.setTimeAtState(stateHistoryCreate.getTimeAtState());
            update = true;
        }

        if (stateHistoryCreate.getRemote() != null && (stateHistory.getRemote()==null||!stateHistoryCreate.getRemote().getId().equals(stateHistory.getRemote().getId()))) {
            stateHistory.setRemote(stateHistoryCreate.getRemote());
            update = true;
        }

        Map<String, Object> mergedDeviceValues = DynamicPropertiesUtils.updateDynamic(stateHistoryCreate.getDeviceProperties(), stateHistory.getDeviceProperties());

        if (mergedDeviceValues != null) {
            stateHistory.setDeviceProperties(mergedDeviceValues);
            update = true;
        }

        Map<String, Object> mergedUserValues = DynamicPropertiesUtils.updateDynamic(stateHistoryCreate.getUserAddedProperties(), stateHistory.getUserAddedProperties());

        if (mergedUserValues != null) {
            stateHistory.setUserAddedProperties(mergedUserValues);
            update = true;
        }

        return update;
    }

    public StateHistory updateStateHistory(StateHistoryUpdate stateHistoryUpdate,
                                 SecurityContextBase securityContext) {
        StateHistory stateHistory = stateHistoryUpdate.getStateHistory();
        if (updateStateHistoryNoMerge(stateHistory, stateHistoryUpdate)) {
            repository.merge(stateHistory);
        }
        return stateHistory;
    }


}
