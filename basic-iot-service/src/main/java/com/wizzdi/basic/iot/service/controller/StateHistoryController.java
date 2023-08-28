package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.StateHistory;
import com.wizzdi.basic.iot.service.request.StateHistoryFilter;
import com.wizzdi.basic.iot.service.service.StateHistoryService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@OperationsInside

@RequestMapping("/plugins/StateHistory")

@Tag(name = "StateHistory")
@Extension
@RestController
public class StateHistoryController implements Plugin , Invoker {

    @Autowired
    private StateHistoryService service;


    @Operation(summary = "getAllStateHistories", description = "Lists all StateHistory")
    
    @PostMapping("/getAllStateHistories")
    public PaginationResponse<StateHistory> getAllStateHistories(

            
            @RequestBody StateHistoryFilter stateHistoryFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(stateHistoryFilter, securityContext);
        return service.getAllStateHistories(securityContext, stateHistoryFilter);
    }

    @Override
    public Class<?> getHandlingClass() {
        return StateHistory.class;
    }
}