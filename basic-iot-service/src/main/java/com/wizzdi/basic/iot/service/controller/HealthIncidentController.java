package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.HealthIncident;
import com.wizzdi.basic.iot.model.HealthIncidentAction;
import com.wizzdi.basic.iot.service.request.HealthIncidentActionCreate;
import com.wizzdi.basic.iot.service.request.HealthIncidentActionFilter;
import com.wizzdi.basic.iot.service.request.HealthIncidentFilter;
import com.wizzdi.basic.iot.service.service.HealthIncidentService;
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
@RequestMapping("/plugins/HealthIncidents")
@Tag(name = "HealthIncidents")
@Extension
@RestController
public class HealthIncidentController implements Plugin, Invoker {
    @Autowired
    private HealthIncidentService service;

    @PostMapping("/getAllHealthIncidents")
    @Operation(summary = "getAllHealthIncidents")
    public PaginationResponse<HealthIncident> getAll(@RequestBody HealthIncidentFilter filter,
                                                      @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAll(securityContext, filter);
    }

    @PostMapping("/getAllHealthIncidentActions")
    @Operation(summary = "getAllHealthIncidentActions")
    public PaginationResponse<HealthIncidentAction> getAllActions(
            @RequestBody HealthIncidentActionFilter filter,
            @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAllActions(securityContext, filter);
    }

    @PostMapping("/addHealthIncidentAction")
    @Operation(summary = "addHealthIncidentAction")
    public HealthIncidentAction addAction(@RequestBody HealthIncidentActionCreate create,
                                           @RequestAttribute SecurityContext securityContext) {
        return service.addAction(create, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() { return HealthIncident.class; }
}
