package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.RemoteHealthHistory;
import com.wizzdi.basic.iot.service.request.RemoteHealthHistoryFilter;
import com.wizzdi.basic.iot.service.service.HealthHistoryService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
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
@RequestMapping("/plugins/RemoteHealthHistory")
@Tag(name = "RemoteHealthHistory")
@Extension
@RestController
public class RemoteHealthHistoryController implements Plugin, Invoker {

    @Autowired
    private HealthHistoryService service;

    @PostMapping("/getAllRemoteHealthHistories")
    @Operation(summary = "getAllRemoteHealthHistories")
    public PaginationResponse<RemoteHealthHistory> getAll(
            @RequestBody RemoteHealthHistoryFilter filter,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(filter, securityContext);
        return service.getAllRemoteHealthHistory(securityContext, filter);
    }

    @Override
    public Class<?> getHandlingClass() {
        return RemoteHealthHistory.class;
    }
}
