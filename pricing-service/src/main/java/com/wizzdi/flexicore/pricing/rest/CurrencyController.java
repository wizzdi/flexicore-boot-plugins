package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;

import com.wizzdi.flexicore.pricing.model.price.Currency;
import com.wizzdi.flexicore.pricing.model.price.Currency_;
import com.wizzdi.flexicore.pricing.request.CurrencyCreate;
import com.wizzdi.flexicore.pricing.request.CurrencyFiltering;
import com.wizzdi.flexicore.pricing.request.CurrencyUpdate;
import com.wizzdi.flexicore.pricing.service.CurrencyService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;


import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/Currency")

@Tag(name = "Currency")
@Extension
@RestController
public class CurrencyController implements Plugin {

    @Autowired
    private CurrencyService service;


    @Operation(summary = "getAllCurrencies", description = "Lists all Currencies")
    @IOperation(Name = "getAllCurrencies", Description = "Lists all Currencies")
    @PostMapping("/getAllCurrencies")
    public PaginationResponse<Currency> getAllCurrencies(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody CurrencyFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllCurrencies(securityContext, filtering);
    }


    @PostMapping("/createCurrency")
    @Operation(summary = "createCurrency", description = "Creates Currency")
    @IOperation(Name = "createCurrency", Description = "Creates Currency")
    public Currency createCurrency(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody CurrencyCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createCurrency(creationContainer, securityContext);
    }


    @PutMapping("/updateCurrency")
    @Operation(summary = "updateCurrency", description = "Updates Currency")
    @IOperation(Name = "updateCurrency", Description = "Updates Currency")
    public Currency updateCurrency(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody CurrencyUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        Currency currency = service.getByIdOrNull(updateContainer.getId(),
                Currency.class, Currency_.security, securityContext);
        if (currency == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Currency with id "
                    + updateContainer.getId());
        }
        updateContainer.setCurrency(currency);

        return service.updateCurrency(updateContainer, securityContext);
    }
}