package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.RemoteHealthProfile;
import com.wizzdi.basic.iot.service.request.RemoteHealthProfileCreate;
import com.wizzdi.basic.iot.service.request.RemoteHealthProfileFilter;
import com.wizzdi.basic.iot.service.request.RemoteHealthProfileUpdate;
import com.wizzdi.basic.iot.service.service.RemoteHealthProfileService;
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
@RequestMapping("/plugins/RemoteHealthProfile")
@Tag(name = "RemoteHealthProfile")
@Extension
@RestController
public class RemoteHealthProfileController implements Plugin, Invoker {
    @Autowired
    private RemoteHealthProfileService service;

    @PostMapping("/getAllRemoteHealthProfiles")
    @Operation(summary = "getAllRemoteHealthProfiles")
    public PaginationResponse<RemoteHealthProfile> getAll(@RequestBody RemoteHealthProfileFilter filter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAll(securityContext, filter);
    }

    @PostMapping("/createRemoteHealthProfile")
    @Operation(summary = "createRemoteHealthProfile")
    public RemoteHealthProfile create(@RequestBody RemoteHealthProfileCreate create, @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.create(create, securityContext);
    }

    @PutMapping("/updateRemoteHealthProfile")
    @Operation(summary = "updateRemoteHealthProfile")
    public RemoteHealthProfile update(@RequestBody RemoteHealthProfileUpdate update, @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.update(update, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() { return RemoteHealthProfile.class; }
}
