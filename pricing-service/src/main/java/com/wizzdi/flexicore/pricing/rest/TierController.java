package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
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

            
            @RequestBody TierFiltering filtering, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllTier(securityContext, filtering);
    }


    @PostMapping("/createTier")
    @Operation(summary = "createTier", description = "Creates Tier")
    @IOperation(Name = "createTier", Description = "Creates Tier")
    public Tier createTier(
            
            @RequestBody TierCreate creationContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createTier(creationContainer, securityContext);
    }


    @PutMapping("/updateTier")
    @Operation(summary = "updateTier", description = "Updates Tier")
    @IOperation(Name = "updateTier", Description = "Updates Tier")
    public Tier updateTier(

            
            @RequestBody TierUpdate updateContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(updateContainer, securityContext);
        Tier tier = service.getByIdOrNull(updateContainer.getId(),
                Tier.class,  securityContext);
        if (tier == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Tier with id "
                    + updateContainer.getId());
        }
        updateContainer.setTier(tier);

        return service.updateTier(updateContainer, securityContext);
    }
}
