package com.wizzdi.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.billing.request.ChargeReferenceCreate;
import com.wizzdi.flexicore.billing.request.ChargeReferenceFiltering;
import com.wizzdi.flexicore.billing.request.ChargeReferenceUpdate;
import com.wizzdi.flexicore.billing.service.ChargeReferenceService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.billing.model.billing.ChargeReference;
import com.wizzdi.flexicore.billing.model.billing.ChargeReference_;
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

@RequestMapping("/plugins/ChargeReference")

@Tag(name = "ChargeReference")
@Extension
@RestController
public class ChargeReferenceController implements Plugin {

    @Autowired
    private ChargeReferenceService service;


    @Operation(summary = "getAllChargeReferences", description = "Lists all ChargeReferences")
    @IOperation(Name = "getAllChargeReferences", Description = "Lists all ChargeReferences")
    @PostMapping("/getAllChargeReferences")
    public PaginationResponse<ChargeReference> getAllChargeReferences(

            
            @RequestBody ChargeReferenceFiltering filtering, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllChargeReferences(securityContext, filtering);
    }


    @PostMapping("/createChargeReference")
    @Operation(summary = "createChargeReference", description = "Creates ChargeReference")
    @IOperation(Name = "createChargeReference", Description = "Creates ChargeReference")
    public ChargeReference createChargeReference(

            
            @RequestBody ChargeReferenceCreate creationContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createChargeReference(creationContainer, securityContext);
    }


    @PutMapping("/updateChargeReference")
    @Operation(summary = "updateChargeReference", description = "Updates ChargeReference")
    @IOperation(Name = "updateChargeReference", Description = "Updates ChargeReference")
    public ChargeReference updateChargeReference(

            
            @RequestBody ChargeReferenceUpdate updateContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(updateContainer, securityContext);
        ChargeReference chargeReference = service.getByIdOrNull(updateContainer.getId(),
                ChargeReference.class,  securityContext);
        if (chargeReference == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no ChargeReference with id "
                    + updateContainer.getId());
        }
        updateContainer.setChargeReferenceObject(chargeReference);

        return service.updateChargeReference(updateContainer, securityContext);
    }
}
