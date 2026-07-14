package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.HealthNotificationPolicy;
import com.wizzdi.basic.iot.service.request.HealthNotificationPolicyCreate;
import com.wizzdi.basic.iot.service.request.HealthNotificationPolicyFilter;
import com.wizzdi.basic.iot.service.request.HealthNotificationPolicyUpdate;
import com.wizzdi.basic.iot.service.service.HealthNotificationPolicyService;
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
@RequestMapping("/plugins/HealthNotificationPolicies")
@Tag(name = "HealthNotificationPolicies")
@Extension
@RestController
public class HealthNotificationPolicyController implements Plugin, Invoker {
    @Autowired
    private HealthNotificationPolicyService service;

    @PostMapping("/getAllHealthNotificationPolicies")
    @Operation(summary = "getAllHealthNotificationPolicies")
    public PaginationResponse<HealthNotificationPolicy> getAll(
            @RequestBody HealthNotificationPolicyFilter filter,
            @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAll(securityContext, filter);
    }

    @PostMapping("/getOrCreateMyDefaultHealthNotificationPolicy")
    @Operation(summary = "getOrCreateMyDefaultHealthNotificationPolicy")
    public HealthNotificationPolicy getOrCreateMyDefault(
            @RequestAttribute SecurityContext securityContext) {
        return service.getOrCreateMyDefault(securityContext);
    }

    @PostMapping("/createHealthNotificationPolicy")
    @Operation(summary = "createHealthNotificationPolicy")
    public HealthNotificationPolicy create(@RequestBody HealthNotificationPolicyCreate create,
                                            @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.create(create, securityContext);
    }

    @PutMapping("/updateHealthNotificationPolicy")
    @Operation(summary = "updateHealthNotificationPolicy")
    public HealthNotificationPolicy update(@RequestBody HealthNotificationPolicyUpdate update,
                                            @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.update(update, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() { return HealthNotificationPolicy.class; }
}
