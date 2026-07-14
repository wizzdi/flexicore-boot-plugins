package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.HealthNotificationDelivery;
import com.wizzdi.basic.iot.service.request.HealthNotificationDeliveryFilter;
import com.wizzdi.basic.iot.service.request.MarkHealthNotificationsRequest;
import com.wizzdi.basic.iot.service.service.HealthNotificationService;
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

import java.util.List;

@OperationsInside
@RequestMapping("/plugins/HealthNotifications")
@Tag(name = "HealthNotifications")
@Extension
@RestController
public class HealthNotificationController implements Plugin, Invoker {
    @Autowired
    private HealthNotificationService service;

    @PostMapping("/getAllHealthNotifications")
    @Operation(summary = "getAllHealthNotifications")
    public PaginationResponse<HealthNotificationDelivery> getAll(
            @RequestBody HealthNotificationDeliveryFilter filter,
            @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAll(securityContext, filter);
    }

    @PutMapping("/markHealthNotifications")
    @Operation(summary = "markHealthNotifications")
    public List<HealthNotificationDelivery> mark(
            @RequestBody MarkHealthNotificationsRequest request,
            @RequestAttribute SecurityContext securityContext) {
        return service.mark(securityContext, request);
    }

    @Override
    public Class<?> getHandlingClass() { return HealthNotificationDelivery.class; }
}
