package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.billing.model.Invoice;
import com.flexicore.billing.model.Invoice_;
import com.flexicore.billing.request.InvoiceCreate;
import com.flexicore.billing.request.InvoiceFiltering;
import com.flexicore.billing.request.InvoiceUpdate;
import com.flexicore.billing.service.InvoiceService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



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

			@RequestBody InvoiceFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllInvoices(securityContext, filtering);
	}



	@PostMapping("/createInvoice")
	@Operation(summary = "createInvoice", description = "Creates Invoice")
	@IOperation(Name = "createInvoice", Description = "Creates Invoice")
	public Invoice createInvoice(

			@RequestBody InvoiceCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createInvoice(creationContainer, securityContext);
	}



	@PutMapping("/updateInvoice")
	@Operation(summary = "updateInvoice", description = "Updates Invoice")
	@IOperation(Name = "updateInvoice", Description = "Updates Invoice")
	public Invoice updateInvoice(

			@RequestBody InvoiceUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		Invoice invoice = service.getByIdOrNull(updateContainer.getId(),
				Invoice.class, Invoice_.security, securityContext);
		if (invoice == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Invoice with id "
					+ updateContainer.getId());
		}
		updateContainer.setInvoice(invoice);

		return service.updateInvoice(updateContainer, securityContext);
	}
}