package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.billing.model.PriceListToService;
import com.flexicore.billing.model.PriceListToService_;
import com.flexicore.billing.request.PriceListToServiceCreate;
import com.flexicore.billing.request.PriceListToServiceFiltering;
import com.flexicore.billing.request.PriceListToServiceUpdate;
import com.flexicore.billing.service.PriceListToServiceService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@OperationsInside

@RequestMapping("/plugins/PriceListToService")

@Tag(name = "PriceListToService")
@Extension
@RestController
public class PriceListToServiceController implements Plugin {

    @Autowired
    private PriceListToServiceService service;


    @Operation(summary = "getAllPriceListToServices", description = "Lists all PriceListToServices")
    @IOperation(Name = "getAllPriceListToServices", Description = "Lists all PriceListToServices")
    @PostMapping("/getAllPriceListToServices")
    public PaginationResponse<PriceListToService> getAllPriceListToServices(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PriceListToServiceFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllPriceListToServices(securityContext, filtering);
    }


    @PostMapping("/createPriceListToService")
    @Operation(summary = "createPriceListToService", description = "Creates PriceListToService")
    @IOperation(Name = "createPriceListToService", Description = "Creates PriceListToService")
    public PriceListToService createPriceListToService(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PriceListToServiceCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createPriceListToService(creationContainer, securityContext);
    }


    @PutMapping("/updatePriceListToService")
    @Operation(summary = "updatePriceListToService", description = "Updates PriceListToService")
    @IOperation(Name = "updatePriceListToService", Description = "Updates PriceListToService")
    public PriceListToService updatePriceListToService(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PriceListToServiceUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        PriceListToService priceListToService = service.getByIdOrNull(updateContainer.getId(),
                PriceListToService.class, PriceListToService_.security, securityContext);
        if (priceListToService == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no PriceListToService with id "
                    + updateContainer.getId());
        }
        updateContainer.setPriceListToService(priceListToService);

        return service.updatePriceListToService(updateContainer, securityContext);
    }
}