package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.DeviceType_;
import com.wizzdi.basic.iot.service.request.DeviceTypeCreate;
import com.wizzdi.basic.iot.service.request.DeviceTypeFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeUpdate;
import com.wizzdi.basic.iot.service.service.DeviceTypeService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/DeviceType")

@Tag(name = "DeviceType")
@Extension
@RestController
public class DeviceTypeController implements Plugin {

    @Autowired
    private DeviceTypeService service;


    @Operation(summary = "getAllDeviceTypes", description = "Lists all DeviceType")
    
    @PostMapping("/getAllDeviceTypes")
    public PaginationResponse<DeviceType> getAllDeviceTypes(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody DeviceTypeFilter deviceTypeFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(deviceTypeFilter, securityContext);
        return service.getAllDeviceTypes(securityContext, deviceTypeFilter);
    }


    @PostMapping("/createDeviceType")
    @Operation(summary = "createDeviceType", description = "Creates DeviceType")
    
    public DeviceType createDeviceType(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody DeviceTypeCreate deviceTypeCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(deviceTypeCreate, securityContext);

        return service.createDeviceType(deviceTypeCreate, securityContext);
    }


    @PutMapping("/updateDeviceType")
    @Operation(summary = "updateDeviceType", description = "Updates DeviceType")
    
    public DeviceType updateDeviceType(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody DeviceTypeUpdate deviceTypeUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(deviceTypeUpdate, securityContext);
        DeviceType deviceType = service.getByIdOrNull(deviceTypeUpdate.getId(),
                DeviceType.class, DeviceType_.security, securityContext);
        if (deviceType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no DeviceType with id "
                    + deviceTypeUpdate.getId());
        }
        deviceTypeUpdate.setDeviceType(deviceType);

        return service.updateDeviceType(deviceTypeUpdate, securityContext);
    }
}