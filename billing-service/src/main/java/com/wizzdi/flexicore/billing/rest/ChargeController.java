package com.wizzdi.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.billing.request.ChargeCreate;
import com.wizzdi.flexicore.billing.request.ChargeFiltering;
import com.wizzdi.flexicore.billing.request.ChargeUpdate;
import com.wizzdi.flexicore.billing.service.ChargeService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.billing.model.billing.Charge;
import com.wizzdi.flexicore.billing.model.billing.Charge_;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/Charge")

@Tag(name = "Charge")
@Extension
@RestController
public class ChargeController implements Plugin {

    @Autowired
    private ChargeService service;


    @Operation(summary = "getAllCharges", description = "Lists all Charges")
    @IOperation(Name = "getAllCharges", Description = "Lists all Charges")
    @PostMapping("/getAllCharges")
    public PaginationResponse<Charge> getAllCharges(

            
            @RequestBody ChargeFiltering filtering, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllCharges(securityContext, filtering);
    }


    @PostMapping("/createCharge")
    @Operation(summary = "createCharge", description = "Creates Charge")
    @IOperation(Name = "createCharge", Description = "Creates Charge")
    public Charge createCharge(

            
            @RequestBody ChargeCreate creationContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createCharge(creationContainer, securityContext);
    }


    @PutMapping("/updateCharge")
    @Operation(summary = "updateCharge", description = "Updates Charge")
    @IOperation(Name = "updateCharge", Description = "Updates Charge")
    public Charge updateCharge(

            
            @RequestBody ChargeUpdate updateContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(updateContainer, securityContext);
        Charge charge = service.getByIdOrNull(updateContainer.getId(),
                Charge.class,  securityContext);
        if (charge == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Charge with id "
                    + updateContainer.getId());
        }
        updateContainer.setCharge(charge);

        return service.updateCharge(updateContainer, securityContext);
    }
}
