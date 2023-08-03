package com.wizzdi.basic.iot.service.controller;

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
import com.wizzdi.basic.iot.service.service.DeviceStateService;
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

@RequestMapping("/plugins/DeviceState")

@Tag(name = "DeviceState")
@Extension
@RestController
public class DeviceStateController implements Plugin {

    @Autowired
    private DeviceStateService service;



    @Operation(summary = "changeState", description = "Change Device State")
    @PostMapping("/changeState")
    public ChangeStateResponse changeState(

            
            @RequestBody ChangeStateRequest changeStateRequest, @RequestAttribute SecurityContextBase securityContext) {
        service.validate(changeStateRequest, securityContext);
        return service.changeState(securityContext, changeStateRequest);
    }


}