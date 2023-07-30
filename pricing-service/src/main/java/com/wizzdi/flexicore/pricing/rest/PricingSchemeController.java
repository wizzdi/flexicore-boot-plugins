package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;

import com.wizzdi.flexicore.pricing.model.price.PricingScheme;
import com.wizzdi.flexicore.pricing.model.price.PricingScheme_;
import com.wizzdi.flexicore.pricing.request.PricingSchemeCreate;
import com.wizzdi.flexicore.pricing.request.PricingSchemeFiltering;
import com.wizzdi.flexicore.pricing.request.PricingSchemeUpdate;
import com.wizzdi.flexicore.pricing.service.PricingSchemeService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/PricingScheme")

@Tag(name = "PricingScheme")
@Extension
@RestController
public class PricingSchemeController implements Plugin {

    @Autowired
    private PricingSchemeService service;


    @Operation(summary = "getAllPricingSchemes", description = "Lists all PricingSchemes")
    @IOperation(Name = "getAllPricingSchemes", Description = "Lists all PricingSchemes")
    @PostMapping("/getAllPricingSchemes")
    public PaginationResponse<PricingScheme> getAllPricingSchemes(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PricingSchemeFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllPricingSchemes(securityContext, filtering);
    }


    @PostMapping("/createPricingScheme")
    @Operation(summary = "createPricingScheme", description = "Creates PricingScheme")
    @IOperation(Name = "createPricingScheme", Description = "Creates PricingScheme")
    public PricingScheme createPricingScheme(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PricingSchemeCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createPricingScheme(creationContainer, securityContext);
    }


    @PutMapping("/updatePricingScheme")
    @Operation(summary = "updatePricingScheme", description = "Updates PricingScheme")
    @IOperation(Name = "updatePricingScheme", Description = "Updates PricingScheme")
    public PricingScheme updatePricingScheme(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PricingSchemeUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        PricingScheme pricingScheme = service.getByIdOrNull(updateContainer.getId(),
                PricingScheme.class, PricingScheme_.security, securityContext);
        if (pricingScheme == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no PricingScheme with id "
                    + updateContainer.getId());
        }
        updateContainer.setPricingScheme(pricingScheme);

        return service.updatePricingScheme(updateContainer, securityContext);
    }
}