package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.PendingGateway;
import com.wizzdi.basic.iot.model.PendingGateway_;
import com.wizzdi.basic.iot.service.request.DeviceCreate;
import com.wizzdi.basic.iot.service.request.PendingGatewayCreate;
import com.wizzdi.basic.iot.service.request.PendingGatewayFilter;
import com.wizzdi.basic.iot.service.request.PendingGatewayUpdate;
import com.wizzdi.basic.iot.service.service.PendingGatewayService;
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

@RequestMapping("/plugins/PendingGateway")

@Tag(name = "PendingGateway")
@Extension
@RestController
public class PendingGatewayController implements Plugin {

    @Autowired
    private PendingGatewayService service;


    @Operation(summary = "getAllPendingGateways", description = "Lists all PendingGateway")
    
    @PostMapping("/getAllPendingGateways")
    public PaginationResponse<PendingGateway> getAllPendingGateways(

            
            @RequestBody PendingGatewayFilter pendingGatewayFilter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(pendingGatewayFilter, securityContext);
        return service.getAllPendingGateways(securityContext, pendingGatewayFilter);
    }


    @PostMapping("/registerGateway")
    @Operation(summary = "registerGateway", description = "registers PendingGateway")

    public PendingGateway registerGateway(
            
            @RequestBody PendingGatewayCreate pendingGatewayCreate) {
        service.validate(pendingGatewayCreate);

        return service.registerGateway(pendingGatewayCreate);
    }

    @PostMapping("/createPendingGateway")
    @Operation(summary = "createPendingGateway", description = "Creates PendingGateway")
    
    public PendingGateway createPendingGateway(
            
            @RequestBody PendingGatewayCreate pendingGatewayCreate,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(pendingGatewayCreate, securityContext);

        return service.createPendingGateway(pendingGatewayCreate, securityContext);
    }


    @PutMapping("/updatePendingGateway")
    @Operation(summary = "updatePendingGateway", description = "Updates PendingGateway")
    
    public PendingGateway updatePendingGateway(

            
            @RequestBody PendingGatewayUpdate pendingGatewayUpdate,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(pendingGatewayUpdate, securityContext);
        PendingGateway pendingGateway = service.getByIdOrNull(pendingGatewayUpdate.getId(),
                PendingGateway.class,  securityContext);
        if (pendingGateway == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no PendingGateway with id "
                    + pendingGatewayUpdate.getId());
        }
        pendingGatewayUpdate.setPendingGateway(pendingGateway);

        return service.updatePendingGateway(pendingGatewayUpdate, securityContext);
    }
}
