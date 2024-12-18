package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.Price;
import com.wizzdi.flexicore.pricing.model.price.Price_;
import com.wizzdi.flexicore.pricing.request.PriceCreate;
import com.wizzdi.flexicore.pricing.request.PriceFiltering;
import com.wizzdi.flexicore.pricing.request.PriceUpdate;
import com.wizzdi.flexicore.pricing.service.PriceService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/Price")

@Tag(name = "Price")
@Extension
@RestController
public class PriceController implements Plugin {

    @Autowired
    private PriceService service;


    @Operation(summary = "getAllPrice", description = "Lists all Price")
    @IOperation(Name = "getAllPrice", Description = "Lists all Price")
    @PostMapping("/getAllPrice")
    public PaginationResponse<Price> getAllPrice(

            
            @RequestBody PriceFiltering filtering, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllPrice(securityContext, filtering);
    }


    @PostMapping("/createPrice")
    @Operation(summary = "createPrice", description = "Creates Price")
    @IOperation(Name = "createPrice", Description = "Creates Price")
    public Price createPrice(
            
            @RequestBody PriceCreate creationContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createPrice(creationContainer, securityContext);
    }


    @PutMapping("/updatePrice")
    @Operation(summary = "updatePrice", description = "Updates Price")
    @IOperation(Name = "updatePrice", Description = "Updates Price")
    public Price updatePrice(

            
            @RequestBody PriceUpdate updateContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(updateContainer, securityContext);
        Price price = service.getByIdOrNull(updateContainer.getId(),
                Price.class,  securityContext);
        if (price == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Price with id "
                    + updateContainer.getId());
        }
        updateContainer.setPrice(price);

        return service.updatePrice(updateContainer, securityContext);
    }
}
