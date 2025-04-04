package com.wizzdi.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.billing.request.PaymentMethodTypeCreate;
import com.wizzdi.flexicore.billing.request.PaymentMethodTypeFiltering;
import com.wizzdi.flexicore.billing.request.PaymentMethodTypeUpdate;
import com.wizzdi.flexicore.billing.service.PaymentMethodTypeService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType_;
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

@RequestMapping("/plugins/PaymentMethodType")

@Tag(name = "PaymentMethodType")
@Extension
@RestController
public class PaymentMethodTypeController implements Plugin {

    @Autowired
    private PaymentMethodTypeService service;


    @Operation(summary = "getAllPaymentMethodTypes", description = "Lists all PaymentMethodTypes")
    @IOperation(Name = "getAllPaymentMethodTypes", Description = "Lists all PaymentMethodTypes")
    @PostMapping("/getAllPaymentMethodTypes")
    public PaginationResponse<PaymentMethodType> getAllPaymentMethodTypes(

            
            @RequestBody PaymentMethodTypeFiltering filtering, @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllPaymentMethodTypes(securityContext, filtering);
    }


    @PostMapping("/createPaymentMethodType")
    @Operation(summary = "createPaymentMethodType", description = "Creates PaymentMethodType")
    @IOperation(Name = "createPaymentMethodType", Description = "Creates PaymentMethodType")
    public PaymentMethodType createPaymentMethodType(

            
            @RequestBody PaymentMethodTypeCreate creationContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createPaymentMethodType(creationContainer, securityContext);
    }


    @PutMapping("/updatePaymentMethodType")
    @Operation(summary = "updatePaymentMethodType", description = "Updates PaymentMethodType")
    @IOperation(Name = "updatePaymentMethodType", Description = "Updates PaymentMethodType")
    public PaymentMethodType updatePaymentMethodType(

            
            @RequestBody PaymentMethodTypeUpdate updateContainer,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(updateContainer, securityContext);
        PaymentMethodType paymentMethodType = service.getByIdOrNull(updateContainer.getId(),
                PaymentMethodType.class,  securityContext);
        if (paymentMethodType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no PaymentMethodType with id "
                    + updateContainer.getId());
        }
        updateContainer.setPaymentMethodType(paymentMethodType);

        return service.updatePaymentMethodType(updateContainer, securityContext);
    }
}
