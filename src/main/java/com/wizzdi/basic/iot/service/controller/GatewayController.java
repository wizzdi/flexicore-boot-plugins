package com.wizzdi.basic.iot.service.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.Gateway_;
import com.wizzdi.basic.iot.service.request.ApproveGatewaysRequest;
import com.wizzdi.basic.iot.service.request.GatewayCreate;
import com.wizzdi.basic.iot.service.request.GatewayFilter;
import com.wizzdi.basic.iot.service.request.GatewayUpdate;
import com.wizzdi.basic.iot.service.service.GatewayService;
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

@RequestMapping("/plugins/Gateway")

@Tag(name = "Gateway")
@Extension
@RestController
public class GatewayController implements Plugin {

    @Autowired
    private GatewayService service;


    @Operation(summary = "getAllGateways", description = "Lists all Gateway")
    
    @PostMapping("/getAllGateways")
    public PaginationResponse<Gateway> getAllGateways(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody GatewayFilter gatewayFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(gatewayFilter, securityContext);
        return service.getAllGateways(securityContext, gatewayFilter);
    }

    @Operation(summary = "approveGateway", description = "approve gateways")
    @PostMapping("/approveGateways")
    public PaginationResponse<Gateway> approveGateways(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ApproveGatewaysRequest approveGatewaysRequest, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(approveGatewaysRequest, securityContext);
        return service.approveGateways(securityContext, approveGatewaysRequest);
    }


    @PostMapping("/createGateway")
    @Operation(summary = "createGateway", description = "Creates Gateway")
    
    public Gateway createGateway(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody GatewayCreate gatewayCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(gatewayCreate, securityContext);

        return service.createGateway(gatewayCreate, securityContext);
    }


    @PutMapping("/updateGateway")
    @Operation(summary = "updateGateway", description = "Updates Gateway")
    
    public Gateway updateGateway(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody GatewayUpdate gatewayUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(gatewayUpdate, securityContext);
        Gateway gateway = service.getByIdOrNull(gatewayUpdate.getId(),
                Gateway.class, Gateway_.security, securityContext);
        if (gateway == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Gateway with id "
                    + gatewayUpdate.getId());
        }
        gatewayUpdate.setGateway(gateway);

        return service.updateGateway(gatewayUpdate, securityContext);
    }
}