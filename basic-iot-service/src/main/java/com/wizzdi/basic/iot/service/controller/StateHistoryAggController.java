package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.StateHistory;
import com.wizzdi.basic.iot.service.request.StateHistoryAggRequest;
import com.wizzdi.basic.iot.service.request.StateHistoryFilter;
import com.wizzdi.basic.iot.service.response.StateHistoryAggEntry;
import com.wizzdi.basic.iot.service.service.StateHistoryAggService;
import com.wizzdi.basic.iot.service.service.StateHistoryService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@OperationsInside

@RequestMapping("/plugins/StateHistory")

@Tag(name = "StateHistory")
@Extension
@RestController
public class StateHistoryAggController implements Plugin , Invoker {

    @Autowired
    private StateHistoryAggService service;


    @Operation(summary = "getAllStateHistories", description = "Lists all StateHistory")
    
    @PostMapping("/getAllStateHistoriesAgg")
    public PaginationResponse<StateHistoryAggEntry> getAllStateHistoriesAgg(


            @RequestBody StateHistoryAggRequest stateHistoryFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validate(stateHistoryFilter, securityContext);
        return service.getAllStateHistoriesAgg(securityContext, stateHistoryFilter);
    }

    @Override
    public Class<?> getHandlingClass() {
        return StateHistory.class;
    }
}