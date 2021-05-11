package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.billing.model.Contract;
import com.flexicore.billing.model.ContractItem;
import com.flexicore.billing.model.ContractItem_;
import com.flexicore.billing.request.ContractCreate;
import com.flexicore.billing.request.ContractFiltering;
import com.flexicore.billing.request.ContractUpdate;
import com.flexicore.billing.service.ContractService;
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
import org.springframework.stereotype.Component;


@OperationsInside

@RequestMapping("/plugins/Contract")

@Tag(name = "Contract")
@Extension
@RestController
public class ContractController implements Plugin {

    @Autowired
    private ContractService service;


    @Operation(summary = "getAllContracts", description = "Lists all Contracts")
    @IOperation(Name = "getAllContracts", Description = "Lists all Contracts")
    @PostMapping("/getAllContracts")
    public PaginationResponse<Contract> getAllContracts(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ContractFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllContracts(securityContext, filtering);
    }


    @PostMapping("/createContract")
    @Operation(summary = "createContract", description = "Creates Contract")
    @IOperation(Name = "createContract", Description = "Creates Contract")
    public Contract createContract(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ContractCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createContract(creationContainer, securityContext);
    }


    @PutMapping("/updateContract")
    @Operation(summary = "updateContract", description = "Updates Contract")
    @IOperation(Name = "updateContract", Description = "Updates Contract")
    public Contract updateContract(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ContractUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        Contract contract = service.getByIdOrNull(updateContainer.getId(),
                Contract.class, ContractItem_.security, securityContext);
        if (contract == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Contract with id "
                    + updateContainer.getId());
        }
        updateContainer.setContract(contract);

        return service.updateContract(updateContainer, securityContext);
    }
}