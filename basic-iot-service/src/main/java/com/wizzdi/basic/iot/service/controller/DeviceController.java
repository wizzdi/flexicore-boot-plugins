package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.Device_;
import com.wizzdi.basic.iot.service.request.ChangeStateRequest;
import com.wizzdi.basic.iot.service.request.DeviceCreate;
import com.wizzdi.basic.iot.service.request.DeviceFilter;
import com.wizzdi.basic.iot.service.request.DeviceUpdate;
import com.wizzdi.basic.iot.service.response.ChangeStateResponse;
import com.wizzdi.basic.iot.service.service.DeviceService;
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

@RequestMapping("/plugins/Device")

@Tag(name = "Device")
@Extension
@RestController
public class DeviceController implements Plugin {

    @Autowired
    private DeviceService service;


    @Operation(summary = "getAllDevices", description = "Lists all Device")
    @PostMapping("/getAllDevices")
    public PaginationResponse<Device> getAllDevices(

            
            @RequestBody DeviceFilter deviceFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(deviceFilter, securityContext);
        return service.getAllDevices(securityContext, deviceFilter);
    }



    @PostMapping("/createDevice")
    @Operation(summary = "createDevice", description = "Creates Device")
    
    public Device createDevice(
            
            @RequestBody DeviceCreate deviceCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(deviceCreate, securityContext);

        return service.createDevice(deviceCreate, securityContext);
    }


    @PutMapping("/updateDevice")
    @Operation(summary = "updateDevice", description = "Updates Device")
    
    public Device updateDevice(

            
            @RequestBody DeviceUpdate deviceUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(deviceUpdate, securityContext);
        Device device = service.getByIdOrNull(deviceUpdate.getId(),
                Device.class, Device_.security, securityContext);
        if (device == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Device with id "
                    + deviceUpdate.getId());
        }
        deviceUpdate.setDevice(device);

        return service.updateDevice(deviceUpdate, securityContext);
    }
}