package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteCreate;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteFilter;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteUpdate;
import com.wizzdi.basic.iot.service.service.RemoteGroupToRemoteService;
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
@RequestMapping("/plugins/RemoteGroupToRemote")
@Tag(name = "RemoteGroupToRemote")
@Extension
@RestController
public class RemoteGroupToRemoteController implements Plugin, Invoker {
    @Autowired
    private RemoteGroupToRemoteService service;

    @PostMapping("/getAllRemoteGroupToRemotes")
    @Operation(summary = "getAllRemoteGroupToRemotes")
    public PaginationResponse<RemoteGroupToRemote> getAll(@RequestBody RemoteGroupToRemoteFilter filter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAll(securityContext, filter);
    }

    @PostMapping("/createRemoteGroupToRemote")
    @Operation(summary = "createRemoteGroupToRemote")
    public RemoteGroupToRemote create(@RequestBody RemoteGroupToRemoteCreate create, @RequestAttribute SecurityContext securityContext) {
        service.validateForCreate(create, securityContext);
        return service.create(create, securityContext);
    }

    @PutMapping("/updateRemoteGroupToRemote")
    @Operation(summary = "updateRemoteGroupToRemote")
    public RemoteGroupToRemote update(@RequestBody RemoteGroupToRemoteUpdate update, @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.update(update, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() {
        return RemoteGroupToRemote.class;
    }
}
