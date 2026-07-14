package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.HealthSignalDefinition;
import com.wizzdi.basic.iot.service.request.HealthSignalDefinitionCreate;
import com.wizzdi.basic.iot.service.request.HealthSignalDefinitionFilter;
import com.wizzdi.basic.iot.service.request.HealthSignalDefinitionUpdate;
import com.wizzdi.basic.iot.service.service.HealthSignalDefinitionService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@OperationsInside
@RequestMapping("/plugins/HealthSignalDefinition")
@Tag(name = "HealthSignalDefinition")
@Extension
@RestController
public class HealthSignalDefinitionController implements Plugin, Invoker {
    @Autowired
    private HealthSignalDefinitionService service;

    @PostMapping("/getAllHealthSignalDefinitions")
    @Operation(summary = "getAllHealthSignalDefinitions")
    public PaginationResponse<HealthSignalDefinition> getAll(@RequestBody HealthSignalDefinitionFilter filter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAll(securityContext, filter);
    }

    @PostMapping("/createHealthSignalDefinition")
    @Operation(summary = "createHealthSignalDefinition")
    public HealthSignalDefinition create(@RequestBody HealthSignalDefinitionCreate create, @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.create(create, securityContext);
    }

    @PutMapping("/updateHealthSignalDefinition")
    @Operation(summary = "updateHealthSignalDefinition")
    public HealthSignalDefinition update(@RequestBody HealthSignalDefinitionUpdate update, @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.update(update, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() { return HealthSignalDefinition.class; }
}
