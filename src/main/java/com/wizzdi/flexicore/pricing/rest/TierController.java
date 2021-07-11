package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.Tier;
import com.wizzdi.flexicore.pricing.model.price.Tier_;
import com.wizzdi.flexicore.pricing.request.TierCreate;
import com.wizzdi.flexicore.pricing.request.TierFiltering;
import com.wizzdi.flexicore.pricing.request.TierUpdate;
import com.wizzdi.flexicore.pricing.service.TierService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/Tier")

@Tag(name = "Tier")
@Extension
@RestController
public class TierController implements Plugin {

    @Autowired
    private TierService service;


    @Operation(summary = "getAllTier", description = "Lists all Tier")
    @IOperation(Name = "getAllTier", Description = "Lists all Tier")
    @PostMapping("/getAllTier")
    public PaginationResponse<Tier> getAllTier(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody TierFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllTier(securityContext, filtering);
    }


    @PostMapping("/createTier")
    @Operation(summary = "createTier", description = "Creates Tier")
    @IOperation(Name = "createTier", Description = "Creates Tier")
    public Tier createTier(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody TierCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createTier(creationContainer, securityContext);
    }


    @PutMapping("/updateTier")
    @Operation(summary = "updateTier", description = "Updates Tier")
    @IOperation(Name = "updateTier", Description = "Updates Tier")
    public Tier updateTier(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody TierUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        Tier tier = service.getByIdOrNull(updateContainer.getId(),
                Tier.class, Tier_.security, securityContext);
        if (tier == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Tier with id "
                    + updateContainer.getId());
        }
        updateContainer.setTier(tier);

        return service.updateTier(updateContainer, securityContext);
    }
}