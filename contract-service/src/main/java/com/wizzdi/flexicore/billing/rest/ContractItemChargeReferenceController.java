package com.wizzdi.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.billing.request.ContractItemChargeReferenceCreate;
import com.wizzdi.flexicore.billing.request.ContractItemChargeReferenceFiltering;
import com.wizzdi.flexicore.billing.request.ContractItemChargeReferenceUpdate;
import com.wizzdi.flexicore.billing.service.ContractItemChargeReferenceService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.contract.model.ContractItemChargeReference;
import com.wizzdi.flexicore.contract.model.ContractItemChargeReference_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/ContractItemChargeReference")

@Tag(name = "ContractItemChargeReference")
@Extension
@RestController
public class ContractItemChargeReferenceController implements Plugin {

    @Autowired
    private ContractItemChargeReferenceService service;


    @Operation(summary = "getAllContractItemChargeReferences", description = "Lists all ContractItemChargeReferences")
    @IOperation(Name = "getAllContractItemChargeReferences", Description = "Lists all ContractItemChargeReferences")
    @PostMapping("/getAllContractItemChargeReferences")
    public PaginationResponse<ContractItemChargeReference> getAllContractItemChargeReferences(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ContractItemChargeReferenceFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllContractItemChargeReferences(securityContext, filtering);
    }


    @PostMapping("/createContractItemChargeReference")
    @Operation(summary = "createContractItemChargeReference", description = "Creates ContractItemChargeReference")
    @IOperation(Name = "createContractItemChargeReference", Description = "Creates ContractItemChargeReference")
    public ContractItemChargeReference createContractItemChargeReference(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ContractItemChargeReferenceCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createContractItemChargeReference(creationContainer, securityContext);
    }


    @PutMapping("/updateContractItemChargeReference")
    @Operation(summary = "updateContractItemChargeReference", description = "Updates ContractItemChargeReference")
    @IOperation(Name = "updateContractItemChargeReference", Description = "Updates ContractItemChargeReference")
    public ContractItemChargeReference updateContractItemChargeReference(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ContractItemChargeReferenceUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        ContractItemChargeReference contractItemChargeReference = service.getByIdOrNull(updateContainer.getId(),
                ContractItemChargeReference.class, ContractItemChargeReference_.security, securityContext);
        if (contractItemChargeReference == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no ContractItemChargeReference with id "
                    + updateContainer.getId());
        }
        updateContainer.setContractItemChargeReference(contractItemChargeReference);

        return service.updateContractItemChargeReference(updateContainer, securityContext);
    }
}