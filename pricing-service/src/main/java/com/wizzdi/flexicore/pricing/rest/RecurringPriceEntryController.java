package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.RecurringPriceEntry;
import com.wizzdi.flexicore.pricing.model.price.RecurringPriceEntry_;
import com.wizzdi.flexicore.pricing.request.RecurringPriceEntryCreate;
import com.wizzdi.flexicore.pricing.request.RecurringPriceEntryFiltering;
import com.wizzdi.flexicore.pricing.request.RecurringPriceEntryUpdate;
import com.wizzdi.flexicore.pricing.service.RecurringPriceEntryService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/RecurringPriceEntry")

@Tag(name = "RecurringPriceEntry")
@Extension
@RestController
public class RecurringPriceEntryController implements Plugin {

    @Autowired
    private RecurringPriceEntryService service;


    @Operation(summary = "getAllRecurringPriceEntry", description = "Lists all RecurringPriceEntry")
    @IOperation(Name = "getAllRecurringPriceEntry", Description = "Lists all RecurringPriceEntry")
    @PostMapping("/getAllRecurringPriceEntry")
    public PaginationResponse<RecurringPriceEntry> getAllRecurringPriceEntry(

            
            @RequestBody RecurringPriceEntryFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllRecurringPriceEntry(securityContext, filtering);
    }


    @PostMapping("/createRecurringPriceEntry")
    @Operation(summary = "createRecurringPriceEntry", description = "Creates RecurringPriceEntry")
    @IOperation(Name = "createRecurringPriceEntry", Description = "Creates RecurringPriceEntry")
    public RecurringPriceEntry createRecurringPriceEntry(
            
            @RequestBody RecurringPriceEntryCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createRecurringPriceEntry(creationContainer, securityContext);
    }


    @PutMapping("/updateRecurringPriceEntry")
    @Operation(summary = "updateRecurringPriceEntry", description = "Updates RecurringPriceEntry")
    @IOperation(Name = "updateRecurringPriceEntry", Description = "Updates RecurringPriceEntry")
    public RecurringPriceEntry updateRecurringPriceEntry(

            
            @RequestBody RecurringPriceEntryUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        RecurringPriceEntry recurringPriceEntry = service.getByIdOrNull(updateContainer.getId(),
                RecurringPriceEntry.class, RecurringPriceEntry_.security, securityContext);
        if (recurringPriceEntry == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no RecurringPriceEntry with id "
                    + updateContainer.getId());
        }
        updateContainer.setRecurringPriceEntry(recurringPriceEntry);

        return service.updateRecurringPriceEntry(updateContainer, securityContext);
    }
}