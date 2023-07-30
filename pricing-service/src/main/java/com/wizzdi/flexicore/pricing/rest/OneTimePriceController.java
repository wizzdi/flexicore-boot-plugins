package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.OneTimePrice;
import com.wizzdi.flexicore.pricing.model.price.OneTimePrice_;
import com.wizzdi.flexicore.pricing.request.OneTimePriceCreate;
import com.wizzdi.flexicore.pricing.request.OneTimePriceFiltering;
import com.wizzdi.flexicore.pricing.request.OneTimePriceUpdate;
import com.wizzdi.flexicore.pricing.service.OneTimePriceService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/OneTimePrice")

@Tag(name = "OneTimePrice")
@Extension
@RestController
public class OneTimePriceController implements Plugin {

    @Autowired
    private OneTimePriceService service;


    @Operation(summary = "getAllOneTimePrice", description = "Lists all OneTimePrice")
    @IOperation(Name = "getAllOneTimePrice", Description = "Lists all OneTimePrice")
    @PostMapping("/getAllOneTimePrice")
    public PaginationResponse<OneTimePrice> getAllOneTimePrice(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody OneTimePriceFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllOneTimePrice(securityContext, filtering);
    }


    @PostMapping("/createOneTimePrice")
    @Operation(summary = "createOneTimePrice", description = "Creates OneTimePrice")
    @IOperation(Name = "createOneTimePrice", Description = "Creates OneTimePrice")
    public OneTimePrice createOneTimePrice(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody OneTimePriceCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createOneTimePrice(creationContainer, securityContext);
    }


    @PutMapping("/updateOneTimePrice")
    @Operation(summary = "updateOneTimePrice", description = "Updates OneTimePrice")
    @IOperation(Name = "updateOneTimePrice", Description = "Updates OneTimePrice")
    public OneTimePrice updateOneTimePrice(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody OneTimePriceUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        OneTimePrice oneTimePrice = service.getByIdOrNull(updateContainer.getId(),
                OneTimePrice.class, OneTimePrice_.security, securityContext);
        if (oneTimePrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no OneTimePrice with id "
                    + updateContainer.getId());
        }
        updateContainer.setOneTimePrice(oneTimePrice);

        return service.updateOneTimePrice(updateContainer, securityContext);
    }
}