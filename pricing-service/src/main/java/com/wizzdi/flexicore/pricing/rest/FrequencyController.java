package com.wizzdi.flexicore.pricing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.Frequency;
import com.wizzdi.flexicore.pricing.model.price.Frequency_;
import com.wizzdi.flexicore.pricing.request.FrequencyCreate;
import com.wizzdi.flexicore.pricing.request.FrequencyFiltering;
import com.wizzdi.flexicore.pricing.request.FrequencyUpdate;
import com.wizzdi.flexicore.pricing.service.FrequencyService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/Frequency")

@Tag(name = "Frequency")
@Extension
@RestController
public class FrequencyController implements Plugin {

    @Autowired
    private FrequencyService service;


    @Operation(summary = "getAllFrequencies", description = "Lists all Frequencies")
    @IOperation(Name = "getAllFrequencies", Description = "Lists all Frequencies")
    @PostMapping("/getAllFrequencies")
    public PaginationResponse<Frequency> getAllFrequencies(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody FrequencyFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllFrequencies(securityContext, filtering);
    }


    @PostMapping("/createFrequency")
    @Operation(summary = "createFrequency", description = "Creates Frequency")
    @IOperation(Name = "createFrequency", Description = "Creates Frequency")
    public Frequency createFrequency(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody FrequencyCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createFrequency(creationContainer, securityContext);
    }


    @PutMapping("/updateFrequency")
    @Operation(summary = "updateFrequency", description = "Updates Frequency")
    @IOperation(Name = "updateFrequency", Description = "Updates Frequency")
    public Frequency updateFrequency(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody FrequencyUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        Frequency frequency = service.getByIdOrNull(updateContainer.getId(),
                Frequency.class, Frequency_.security, securityContext);
        if (frequency == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Frequency with id "
                    + updateContainer.getId());
        }
        updateContainer.setFrequency(frequency);

        return service.updateFrequency(updateContainer, securityContext);
    }
}