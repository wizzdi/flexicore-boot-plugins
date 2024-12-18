package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.Money;
import com.wizzdi.flexicore.pricing.model.price.Money_;
import com.wizzdi.flexicore.pricing.request.MoneyCreate;
import com.wizzdi.flexicore.pricing.request.MoneyFiltering;
import com.wizzdi.flexicore.pricing.request.MoneyUpdate;
import com.wizzdi.flexicore.pricing.service.MoneyService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/Money")

@Tag(name = "Money")
@Extension
@RestController
public class MoneyController implements Plugin {

    @Autowired
    private MoneyService service;


    @Operation(summary = "getAllMoney", description = "Lists all Money")
    @IOperation(Name = "getAllMoney", Description = "Lists all Money")
    @PostMapping("/getAllMoney")
    public PaginationResponse<Money> getAllMoney(

            
            @RequestBody MoneyFiltering filtering, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllMoney(securityContext, filtering);
    }


    @PostMapping("/createMoney")
    @Operation(summary = "createMoney", description = "Creates Money")
    @IOperation(Name = "createMoney", Description = "Creates Money")
    public Money createMoney(
            
            @RequestBody MoneyCreate creationContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createMoney(creationContainer, securityContext);
    }


    @PutMapping("/updateMoney")
    @Operation(summary = "updateMoney", description = "Updates Money")
    @IOperation(Name = "updateMoney", Description = "Updates Money")
    public Money updateMoney(

            
            @RequestBody MoneyUpdate updateContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(updateContainer, securityContext);
        Money money = service.getByIdOrNull(updateContainer.getId(),
                Money.class,  securityContext);
        if (money == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Money with id "
                    + updateContainer.getId());
        }
        updateContainer.setMoney(money);

        return service.updateMoney(updateContainer, securityContext);
    }
}
