package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.basic.iot.model.DeviceTypeToMapIcon;
import com.wizzdi.basic.iot.service.request.DeviceTypeToMapIconCreate;
import com.wizzdi.basic.iot.service.request.DeviceTypeToMapIconFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeToMapIconUpdate;
import com.wizzdi.basic.iot.service.service.DeviceTypeToMapIconService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@OperationsInside
@RequestMapping("/plugins/DeviceTypeToMapIcon")
@Tag(name = "DeviceTypeToMapIcon")
@Extension
@RestController
public class DeviceTypeToMapIconController implements Plugin, Invoker {

    @Autowired
    private DeviceTypeToMapIconService service;

    @Operation(summary = "getAllDeviceTypeToMapIcons", description = "Lists all DeviceTypeToMapIcon links")
    @PostMapping("/getAllDeviceTypeToMapIcons")
    public PaginationResponse<DeviceTypeToMapIcon> getAllDeviceTypeToMapIcons(
            @RequestBody DeviceTypeToMapIconFilter filter,
            @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filter, securityContext);
        return service.getAllDeviceTypeToMapIcons(securityContext, filter);
    }

    @PostMapping("/createDeviceTypeToMapIcon")
    @Operation(summary = "createDeviceTypeToMapIcon", description = "Creates DeviceTypeToMapIcon")
    public DeviceTypeToMapIcon createDeviceTypeToMapIcon(
            @Validated(Create.class) @RequestBody DeviceTypeToMapIconCreate create,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.createDeviceTypeToMapIcon(create, securityContext);
    }

    @PutMapping("/updateDeviceTypeToMapIcon")
    @Operation(summary = "updateDeviceTypeToMapIcon", description = "Updates DeviceTypeToMapIcon")
    public DeviceTypeToMapIcon updateDeviceTypeToMapIcon(
            @Validated(Update.class) @RequestBody DeviceTypeToMapIconUpdate update,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(update, securityContext);
        return service.updateDeviceTypeToMapIcon(update, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() {
        return DeviceTypeToMapIcon.class;
    }
}
