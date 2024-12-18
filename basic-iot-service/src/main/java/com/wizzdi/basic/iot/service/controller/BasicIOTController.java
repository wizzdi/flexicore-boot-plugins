package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.Device_;
import com.wizzdi.basic.iot.service.request.DeviceCreate;
import com.wizzdi.basic.iot.service.request.DeviceFilter;
import com.wizzdi.basic.iot.service.request.DeviceUpdate;
import com.wizzdi.basic.iot.service.service.BasicIOTLogic;
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

@RequestMapping("/plugins/BasicIOT")

@Tag(name = "BasicIOT")
@Extension
@RestController
public class BasicIOTController implements Plugin {

    @Autowired
    private BasicIOTLogic basicIOTLogic;


    @Operation(summary = "Run fixing connectivity off with default icon fix")
    @GetMapping("/fixIcons")
    public long fixIcons(
           @RequestAttribute SecurityContext securityContext) {

        return basicIOTLogic.fixIcons(securityContext);
    }


}
