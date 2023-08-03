package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.FirmwareUpdateInstallation;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateInstallationCreate;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateInstallationFilter;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateInstallationMassCreate;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateInstallationUpdate;
import com.wizzdi.basic.iot.service.service.FirmwareUpdateInstallationService;
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
import java.util.List;


@OperationsInside

@RequestMapping("/plugins/FirmwareUpdateInstallation")

@Tag(name = "FirmwareUpdateInstallation")
@Extension
@RestController
public class FirmwareUpdateInstallationController implements Plugin {

    @Autowired
    private FirmwareUpdateInstallationService service;


    @Operation(summary = "getAllFirmwareUpdateInstallations", description = "Lists all FirmwareUpdateInstallation")
    @PostMapping("/getAllFirmwareUpdateInstallations")
    public PaginationResponse<FirmwareUpdateInstallation> getAllFirmwareUpdateInstallations(

            
            @RequestBody @Valid FirmwareUpdateInstallationFilter firmwareUpdateInstallationFilter, @RequestAttribute SecurityContextBase securityContext) {
        return service.getAllFirmwareUpdateInstallations(securityContext, firmwareUpdateInstallationFilter);
    }



    @PostMapping("/createFirmwareUpdateInstallation")
    @Operation(summary = "createFirmwareUpdateInstallation", description = "Creates FirmwareUpdateInstallation")
    
    public FirmwareUpdateInstallation createFirmwareUpdateInstallation(
            
            @RequestBody @Validated(Create.class) FirmwareUpdateInstallationCreate firmwareUpdateInstallationCreate,
            @RequestAttribute SecurityContextBase securityContext) {

        return service.createFirmwareUpdateInstallation(firmwareUpdateInstallationCreate, securityContext);
    }

    @PostMapping("/massCreateFirmwareUpdateInstallation")
    @Operation(summary = "massCreateFirmwareUpdateInstallation", description = "Mass Creates FirmwareUpdateInstallation")

    public List<FirmwareUpdateInstallation> massCreateFirmwareUpdateInstallation(
            
            @RequestBody @Valid FirmwareUpdateInstallationMassCreate firmwareUpdateInstallationMassCreate,
            @RequestAttribute SecurityContextBase securityContext) {

        return service.massCreateFirmwareUpdateInstallation(firmwareUpdateInstallationMassCreate, securityContext);
    }


    @PutMapping("/updateFirmwareUpdateInstallation")
    @Operation(summary = "updateFirmwareUpdateInstallation", description = "Updates FirmwareUpdateInstallation")
    
    public FirmwareUpdateInstallation updateFirmwareUpdateInstallation(

            
            @RequestBody @Validated(Update.class) FirmwareUpdateInstallationUpdate firmwareUpdateInstallationUpdate,
            @RequestAttribute SecurityContextBase securityContext) {

        return service.updateFirmwareUpdateInstallation(firmwareUpdateInstallationUpdate, securityContext);
    }
}