package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice_;
import com.wizzdi.flexicore.pricing.request.RecurringPriceCreate;
import com.wizzdi.flexicore.pricing.request.RecurringPriceFiltering;
import com.wizzdi.flexicore.pricing.request.RecurringPriceUpdate;
import com.wizzdi.flexicore.pricing.service.RecurringPriceService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/RecurringPrice")

@Tag(name = "RecurringPrice")
@Extension
@RestController
public class RecurringPriceController implements Plugin {

    @Autowired
    private RecurringPriceService service;


    @Operation(summary = "getAllRecurringPrice", description = "Lists all RecurringPrice")
    @IOperation(Name = "getAllRecurringPrice", Description = "Lists all RecurringPrice")
    @PostMapping("/getAllRecurringPrice")
    public PaginationResponse<RecurringPrice> getAllRecurringPrice(

            
            @RequestBody RecurringPriceFiltering filtering, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllRecurringPrice(securityContext, filtering);
    }


    @PostMapping("/createRecurringPrice")
    @Operation(summary = "createRecurringPrice", description = "Creates RecurringPrice")
    @IOperation(Name = "createRecurringPrice", Description = "Creates RecurringPrice")
    public RecurringPrice createRecurringPrice(
            
            @RequestBody RecurringPriceCreate creationContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createRecurringPrice(creationContainer, securityContext);
    }


    @PutMapping("/updateRecurringPrice")
    @Operation(summary = "updateRecurringPrice", description = "Updates RecurringPrice")
    @IOperation(Name = "updateRecurringPrice", Description = "Updates RecurringPrice")
    public RecurringPrice updateRecurringPrice(

            
            @RequestBody RecurringPriceUpdate updateContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(updateContainer, securityContext);
        RecurringPrice recurringPrice = service.getByIdOrNull(updateContainer.getId(),
                RecurringPrice.class,  securityContext);
        if (recurringPrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no RecurringPrice with id "
                    + updateContainer.getId());
        }
        updateContainer.setRecurringPrice(recurringPrice);

        return service.updateRecurringPrice(updateContainer, securityContext);
    }
}
