package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.StatePropertyDefinition;
import com.wizzdi.basic.iot.service.request.StatePropertyDefinitionCreate;
import com.wizzdi.basic.iot.service.request.StatePropertyDefinitionFilter;
import com.wizzdi.basic.iot.service.request.StatePropertyDefinitionUpdate;
import com.wizzdi.basic.iot.service.service.StatePropertyDefinitionService;
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
@RequestMapping("/plugins/StatePropertyDefinition")
@Tag(name = "StatePropertyDefinition")
@Extension
@RestController
public class StatePropertyDefinitionController implements Plugin, Invoker {
    @Autowired
    private StatePropertyDefinitionService service;

    @PostMapping("/getAllStatePropertyDefinitions")
    @Operation(summary = "getAllStatePropertyDefinitions")
    public PaginationResponse<StatePropertyDefinition> getAll(@RequestBody StatePropertyDefinitionFilter filter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAll(securityContext, filter);
    }

    @PostMapping("/createStatePropertyDefinition")
    @Operation(summary = "createStatePropertyDefinition")
    public StatePropertyDefinition create(@RequestBody StatePropertyDefinitionCreate create, @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.create(create, securityContext);
    }

    @PutMapping("/updateStatePropertyDefinition")
    @Operation(summary = "updateStatePropertyDefinition")
    public StatePropertyDefinition update(@RequestBody StatePropertyDefinitionUpdate update, @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.update(update, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() { return StatePropertyDefinition.class; }
}
