package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.RemoteRoleDefinition;
import com.wizzdi.basic.iot.service.request.RemoteRoleDefinitionCreate;
import com.wizzdi.basic.iot.service.request.RemoteRoleDefinitionFilter;
import com.wizzdi.basic.iot.service.request.RemoteRoleDefinitionUpdate;
import com.wizzdi.basic.iot.service.service.RemoteRoleDefinitionService;
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
@RequestMapping("/plugins/RemoteRoleDefinition")
@Tag(name = "RemoteRoleDefinition")
@Extension
@RestController
public class RemoteRoleDefinitionController implements Plugin, Invoker {
    @Autowired
    private RemoteRoleDefinitionService service;

    @PostMapping("/getAllRemoteRoleDefinitions")
    @Operation(summary = "getAllRemoteRoleDefinitions")
    public PaginationResponse<RemoteRoleDefinition> getAll(@RequestBody RemoteRoleDefinitionFilter filter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAll(securityContext, filter);
    }

    @PostMapping("/createRemoteRoleDefinition")
    @Operation(summary = "createRemoteRoleDefinition")
    public RemoteRoleDefinition create(@RequestBody RemoteRoleDefinitionCreate create, @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.create(create, securityContext);
    }

    @PutMapping("/updateRemoteRoleDefinition")
    @Operation(summary = "updateRemoteRoleDefinition")
    public RemoteRoleDefinition update(@RequestBody RemoteRoleDefinitionUpdate update, @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.update(update, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() {
        return RemoteRoleDefinition.class;
    }
}
