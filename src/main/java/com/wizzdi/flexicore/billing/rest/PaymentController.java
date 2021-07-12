package com.wizzdi.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.billing.model.payment.Payment;
import com.wizzdi.flexicore.billing.model.payment.Payment_;
import com.wizzdi.flexicore.billing.request.PaymentCreate;
import com.wizzdi.flexicore.billing.request.PaymentFiltering;
import com.wizzdi.flexicore.billing.request.PaymentUpdate;
import com.wizzdi.flexicore.billing.service.PaymentService;
import com.flexicore.security.SecurityContextBase;
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

@RequestMapping("/plugins/Payment")

@Tag(name = "Payment")
@Extension
@RestController
public class PaymentController implements Plugin {

    @Autowired
    private PaymentService service;


    @Operation(summary = "getAllPayments", description = "Lists all Payments")
    @IOperation(Name = "getAllPayments", Description = "Lists all Payments")
    @PostMapping("/getAllPayments")
    public PaginationResponse<Payment> getAllPayments(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PaymentFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllPayments(securityContext, filtering);
    }


    @PostMapping("/createPayment")
    @Operation(summary = "createPayment", description = "Creates Payment")
    @IOperation(Name = "createPayment", Description = "Creates Payment")
    public Payment createPayment(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PaymentCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createPayment(creationContainer, securityContext);
    }


    @PutMapping("/updatePayment")
    @Operation(summary = "updatePayment", description = "Updates Payment")
    @IOperation(Name = "updatePayment", Description = "Updates Payment")
    public Payment updatePayment(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody PaymentUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        Payment payment = service.getByIdOrNull(updateContainer.getId(),
                Payment.class, Payment_.security, securityContext);
        if (payment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Payment with id "
                    + updateContainer.getId());
        }
        updateContainer.setPayment(payment);

        return service.updatePayment(updateContainer, securityContext);
    }
}