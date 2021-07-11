package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.wizzdi.flexicore.pricing.model.price.PriceList;
import com.wizzdi.flexicore.pricing.model.price.PriceList_;
import com.wizzdi.flexicore.pricing.request.PriceListCreate;
import com.wizzdi.flexicore.pricing.request.PriceListFiltering;
import com.wizzdi.flexicore.pricing.request.PriceListUpdate;
import com.wizzdi.flexicore.pricing.service.PriceListService;
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


@OperationsInside

@RequestMapping("/plugins/PriceList")

@Tag(name = "PriceList")
@Extension
@RestController
public class PriceListController implements Plugin {

    @Autowired
    private PriceListService service;


    @Operation(summary = "getAllPriceLists", description = "Lists all PriceLists")
    @IOperation(Name = "getAllPriceLists", Description = "Lists all PriceLists")
    @PostMapping("/getAllPriceLists")
    public PaginationResponse<PriceList> getAllPriceLists(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PriceListFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllPriceLists(securityContext, filtering);
    }


    @PostMapping("/createPriceList")
    @Operation(summary = "createPriceList", description = "Creates PriceList")
    @IOperation(Name = "createPriceList", Description = "Creates PriceList")
    public PriceList createPriceList(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PriceListCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createPriceList(creationContainer, securityContext);
    }


    @PutMapping("/updatePriceList")
    @Operation(summary = "updatePriceList", description = "Updates PriceList")
    @IOperation(Name = "updatePriceList", Description = "Updates PriceList")
    public PriceList updatePriceList(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PriceListUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        PriceList priceList = service.getByIdOrNull(updateContainer.getId(),
                PriceList.class, PriceList_.security, securityContext);
        if (priceList == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no PriceList with id "
                    + updateContainer.getId());
        }
        updateContainer.setPriceList(priceList);

        return service.updatePriceList(updateContainer, securityContext);
    }
}