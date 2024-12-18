package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.wizzdi.flexicore.pricing.model.price.PriceListItem;
import com.wizzdi.flexicore.pricing.model.price.PriceListItem_;
import com.wizzdi.flexicore.pricing.request.PriceListItemCreate;
import com.wizzdi.flexicore.pricing.request.PriceListItemFiltering;
import com.wizzdi.flexicore.pricing.request.PriceListItemUpdate;
import com.wizzdi.flexicore.pricing.service.PriceListItemService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;


@OperationsInside

@RequestMapping("/plugins/PriceListItem")

@Tag(name = "PriceListItem")
@Extension
@RestController
public class PriceListItemController implements Plugin {

    @Autowired
    private PriceListItemService service;


    @Operation(summary = "getAllPriceListItems", description = "Lists all PriceListItems")
    @IOperation(Name = "getAllPriceListItems", Description = "Lists all PriceListItems")
    @PostMapping("/getAllPriceListItems")
    public PaginationResponse<PriceListItem> getAllPriceListItems(
            
            @RequestBody PriceListItemFiltering filtering, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllPriceListItems(securityContext, filtering);
    }


    @PostMapping("/createPriceListItem")
    @Operation(summary = "createPriceListItem", description = "Creates PriceListItem")
    @IOperation(Name = "createPriceListItem", Description = "Creates PriceListItem")
    public PriceListItem createPriceListItem(

            
            @RequestBody PriceListItemCreate creationContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createPriceListItem(creationContainer, securityContext);
    }


    @PutMapping("/updatePriceListItem")
    @Operation(summary = "updatePriceListItem", description = "Updates PriceListItem")
    @IOperation(Name = "updatePriceListItem", Description = "Updates PriceListItem")
    public PriceListItem updatePriceListItem(

            
            @RequestBody PriceListItemUpdate updateContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(updateContainer, securityContext);
        PriceListItem priceListItem = service.getByIdOrNull(updateContainer.getId(),
                PriceListItem.class,  securityContext);
        if (priceListItem == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no PriceListItem with id "
                    + updateContainer.getId());
        }
        updateContainer.setPriceListItem(priceListItem);

        return service.updatePriceListItem(updateContainer, securityContext);
    }
}
