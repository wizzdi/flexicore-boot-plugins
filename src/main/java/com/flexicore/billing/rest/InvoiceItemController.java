package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.billing.model.InvoiceItem;
import com.flexicore.billing.model.InvoiceItem_;
import com.flexicore.billing.request.InvoiceItemCreate;
import com.flexicore.billing.request.InvoiceItemFiltering;
import com.flexicore.billing.request.InvoiceItemUpdate;
import com.flexicore.billing.service.InvoiceItemService;
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

@RequestMapping("/plugins/InvoiceItem")

@Tag(name = "InvoiceItem")
@Extension
@RestController
public class InvoiceItemController implements Plugin {

		@Autowired
	private InvoiceItemService service;



	@Operation(summary = "getAllInvoiceItems", description = "Lists all InvoiceItems")
	@IOperation(Name = "getAllInvoiceItems", Description = "Lists all InvoiceItems")
	@PostMapping("/getAllInvoiceItems")
	public PaginationResponse<InvoiceItem> getAllInvoiceItems(

			@RequestBody InvoiceItemFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllInvoiceItems(securityContext, filtering);
	}



	@PostMapping("/createInvoiceItem")
	@Operation(summary = "createInvoiceItem", description = "Creates InvoiceItem")
	@IOperation(Name = "createInvoiceItem", Description = "Creates InvoiceItem")
	public InvoiceItem createInvoiceItem(

			@RequestBody InvoiceItemCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createInvoiceItem(creationContainer, securityContext);
	}



	@PutMapping("/updateInvoiceItem")
	@Operation(summary = "updateInvoiceItem", description = "Updates InvoiceItem")
	@IOperation(Name = "updateInvoiceItem", Description = "Updates InvoiceItem")
	public InvoiceItem updateInvoiceItem(

			@RequestBody InvoiceItemUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		InvoiceItem invoiceItem = service.getByIdOrNull(updateContainer.getId(),
				InvoiceItem.class, InvoiceItem_.security, securityContext);
		if (invoiceItem == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no InvoiceItem with id "
					+ updateContainer.getId());
		}
		updateContainer.setInvoiceItem(invoiceItem);

		return service.updateInvoiceItem(updateContainer, securityContext);
	}
}