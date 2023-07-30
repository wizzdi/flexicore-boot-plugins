package com.wizzdi.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.wizzdi.flexicore.billing.model.payment.Invoice;
import com.wizzdi.flexicore.billing.model.payment.Invoice_;
import com.wizzdi.flexicore.billing.request.InvoiceCreate;
import com.wizzdi.flexicore.billing.request.InvoiceFiltering;
import com.wizzdi.flexicore.billing.request.InvoiceUpdate;
import com.wizzdi.flexicore.billing.service.InvoiceService;
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

@RequestMapping("/plugins/Invoice")

@Tag(name = "Invoice")
@Extension
@RestController
public class InvoiceController implements Plugin {

    @Autowired
    private InvoiceService service;


    @Operation(summary = "getAllInvoices", description = "Lists all Invoices")
    @IOperation(Name = "getAllInvoices", Description = "Lists all Invoices")
    @PostMapping("/getAllInvoices")
    public PaginationResponse<Invoice> getAllInvoices(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody InvoiceFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllInvoices(securityContext, filtering);
    }


    @PostMapping("/createInvoice")
    @Operation(summary = "createInvoice", description = "Creates Invoice")
    @IOperation(Name = "createInvoice", Description = "Creates Invoice")
    public Invoice createInvoice(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody InvoiceCreate creationContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(creationContainer, securityContext);

        return service.createInvoice(creationContainer, securityContext);
    }


    @PutMapping("/updateInvoice")
    @Operation(summary = "updateInvoice", description = "Updates Invoice")
    @IOperation(Name = "updateInvoice", Description = "Updates Invoice")
    public Invoice updateInvoice(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody InvoiceUpdate updateContainer,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(updateContainer, securityContext);
        Invoice invoice = service.getByIdOrNull(updateContainer.getId(),
                Invoice.class, Invoice_.security, securityContext);
        if (invoice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Invoice with id "
                    + updateContainer.getId());
        }
        updateContainer.setInvoice(invoice);

        return service.updateInvoice(updateContainer, securityContext);
    }
}