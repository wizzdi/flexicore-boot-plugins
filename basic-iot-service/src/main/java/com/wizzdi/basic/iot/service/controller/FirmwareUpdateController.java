package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.FirmwareUpdate;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateCreate;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateFilter;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateUpdate;
import com.wizzdi.basic.iot.service.service.FirmwareUpdateService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@OperationsInside

@RequestMapping("/plugins/FirmwareUpdate")

@Tag(name = "FirmwareUpdate")
@Extension
@RestController
public class FirmwareUpdateController implements Plugin {

    @Autowired
    private FirmwareUpdateService service;


    @Operation(summary = "getAllFirmwareUpdates", description = "Lists all FirmwareUpdate")
    @PostMapping("/getAllFirmwareUpdates")
    public PaginationResponse<FirmwareUpdate> getAllFirmwareUpdates(

            
            @RequestBody @Valid FirmwareUpdateFilter firmwareUpdateFilter, @RequestAttribute SecurityContext securityContext) {
        return service.getAllFirmwareUpdates(securityContext, firmwareUpdateFilter);
    }



    @PostMapping("/createFirmwareUpdate")
    @Operation(summary = "createFirmwareUpdate", description = "Creates FirmwareUpdate")
    
    public FirmwareUpdate createFirmwareUpdate(
            
            @RequestBody @Validated(Create.class) FirmwareUpdateCreate firmwareUpdateCreate,
            @RequestAttribute SecurityContext securityContext) {

        return service.createFirmwareUpdate(firmwareUpdateCreate, securityContext);
    }


    @PutMapping("/updateFirmwareUpdate")
    @Operation(summary = "updateFirmwareUpdate", description = "Updates FirmwareUpdate")
    
    public FirmwareUpdate updateFirmwareUpdate(

            
            @RequestBody @Validated(Update.class) FirmwareUpdateUpdate firmwareUpdateUpdate,
            @RequestAttribute SecurityContext securityContext) {

        return service.updateFirmwareUpdate(firmwareUpdateUpdate, securityContext);
    }
}
