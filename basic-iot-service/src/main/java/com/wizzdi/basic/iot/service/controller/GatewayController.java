package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.Gateway_;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.basic.iot.service.response.ImportGatewaysResponse;
import com.wizzdi.basic.iot.service.response.MoveGatewaysResponse;
import com.wizzdi.basic.iot.service.service.GatewayMoveService;
import com.wizzdi.basic.iot.service.service.GatewayService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;



@OperationsInside

@RequestMapping("/plugins/Gateway")

@Tag(name = "Gateway")
@Extension
@RestController
public class GatewayController implements Plugin, Invoker {

    @Autowired
    private GatewayService service;
    @Autowired
    private GatewayMoveService gatewayMoveService;


    @Operation(summary = "getAllGateways", description = "Lists all Gateway")
    
    @PostMapping("/getAllGateways")
    public PaginationResponse<Gateway> getAllGateways(

            
            @RequestBody GatewayFilter gatewayFilter, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(gatewayFilter, securityContext);
        return service.getAllGateways(securityContext, gatewayFilter);
    }

    @Operation(summary = "approveGateway", description = "approve gateways")
    @PostMapping("/approveGateways")
    public PaginationResponse<Gateway> approveGateways(

            
            @RequestBody ApproveGatewaysRequest approveGatewaysRequest, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(approveGatewaysRequest, securityContext);
        return service.approveGateways(securityContext, approveGatewaysRequest);
    }


    @PostMapping("/createGateway")
    @Operation(summary = "createGateway", description = "Creates Gateway")
    
    public Gateway createGateway(
            
            @RequestBody GatewayCreate gatewayCreate,
            @RequestAttribute SecurityContext securityContext) {
        service.validateCreate(gatewayCreate, securityContext);

        return service.createGateway(gatewayCreate, securityContext);
    }


    @PutMapping("/updateGateway")
    @Operation(summary = "updateGateway", description = "Updates Gateway")
    
    public Gateway updateGateway(

            
            @RequestBody GatewayUpdate gatewayUpdate,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(gatewayUpdate, securityContext);
        Gateway gateway = service.getByIdOrNull(gatewayUpdate.getId(),
                Gateway.class,  securityContext);
        if (gateway == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Gateway with id "
                    + gatewayUpdate.getId());
        }
        gatewayUpdate.setGateway(gateway);

        return service.updateGateway(gatewayUpdate, securityContext);
    }

    @Operation(summary = "importGateways", description = "imports Gateways from CSV")
    @PostMapping("/importGateways")
    public ImportGatewaysResponse importGateways(

            
            @RequestBody ImportGatewaysRequest importGatewaysRequest, @RequestAttribute SecurityContext securityContext) {
        service.validate(importGatewaysRequest, securityContext);
        return service.importGateways(securityContext, importGatewaysRequest);
    }

    @Operation(summary = "moveGatewaysToTenant", description = "moves gateway and all of its devices and related objects to a different tenant")
    @PostMapping("/move")
    public MoveGatewaysResponse moveGatewaysToTenant(

            @RequestBody MoveGatewaysRequest moveGatewaysRequest, @RequestAttribute SecurityContext securityContext) {
        gatewayMoveService.validate(moveGatewaysRequest, securityContext);
        return gatewayMoveService.moveGatewaysToTenant(securityContext, moveGatewaysRequest);
    }

    @Override
    public Class<?> getHandlingClass() {
        return Gateway.class;
    }
}
