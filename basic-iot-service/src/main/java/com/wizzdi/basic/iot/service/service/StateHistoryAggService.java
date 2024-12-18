package com.wizzdi.basic.iot.service.service;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.service.data.StateHistoryAggRepository;
import com.wizzdi.basic.iot.service.request.StateHistoryAggRequest;
import com.wizzdi.basic.iot.service.response.StateHistoryAggEntry;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@Extension
public class StateHistoryAggService implements Plugin {

    @Autowired
    private StateHistoryService stateHistoryService;
    @Autowired
    private StateHistoryAggRepository stateHistoryAggRepository;


    public void validate(StateHistoryAggRequest stateHistoryFilter, SecurityContext securityContext) {
        if(stateHistoryFilter.getStateHistoryFilter()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"stateHistoryFilter must be set");
        }
        stateHistoryService.validateFiltering(stateHistoryFilter.getStateHistoryFilter(),securityContext);

        if(stateHistoryFilter.getGroupByFieldName()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"groupBy must be set");
        }
    }

    public PaginationResponse<StateHistoryAggEntry> getAllStateHistoriesAgg(SecurityContext securityContext, StateHistoryAggRequest stateHistoryFilter) {
        List<StateHistoryAggEntry> list=stateHistoryAggRepository.listAllStateHistoriesAgg(stateHistoryFilter,securityContext);
        long count=list.size();
        return new PaginationResponse<>(list,stateHistoryFilter,count);
    }
}
