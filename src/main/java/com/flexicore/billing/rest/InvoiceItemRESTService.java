package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.InvoiceItem;
import com.flexicore.billing.request.InvoiceItemCreate;
import com.flexicore.billing.request.InvoiceItemFiltering;
import com.flexicore.billing.request.InvoiceItemUpdate;
import com.flexicore.billing.service.InvoiceItemService;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/InvoiceItem")
@RequestScoped
@Tag(name = "InvoiceItem")
@Extension
@Component
public class InvoiceItemRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private InvoiceItemService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllInvoiceItems", description = "Lists all InvoiceItems")
	@IOperation(Name = "getAllInvoiceItems", Description = "Lists all InvoiceItems")
	@Path("getAllInvoiceItems")
	public PaginationResponse<InvoiceItem> getAllInvoiceItems(
			@HeaderParam("authenticationKey") String authenticationKey,
			InvoiceItemFiltering filtering, @Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllInvoiceItems(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createInvoiceItem")
	@Operation(summary = "createInvoiceItem", description = "Creates InvoiceItem")
	@IOperation(Name = "createInvoiceItem", Description = "Creates InvoiceItem")
	public InvoiceItem createInvoiceItem(
			@HeaderParam("authenticationKey") String authenticationKey,
			InvoiceItemCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createInvoiceItem(creationContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updateInvoiceItem")
	@Operation(summary = "updateInvoiceItem", description = "Updates InvoiceItem")
	@IOperation(Name = "updateInvoiceItem", Description = "Updates InvoiceItem")
	public InvoiceItem updateInvoiceItem(
			@HeaderParam("authenticationKey") String authenticationKey,
			InvoiceItemUpdate updateContainer,
			@Context SecurityContext securityContext) {
		service.validate(updateContainer, securityContext);
		InvoiceItem invoiceItem = service.getByIdOrNull(updateContainer.getId(),
				InvoiceItem.class, null, securityContext);
		if (invoiceItem == null) {
			throw new BadRequestException("no InvoiceItem with id "
					+ updateContainer.getId());
		}
		updateContainer.setInvoiceItem(invoiceItem);

		return service.updateInvoiceItem(updateContainer, securityContext);
	}
}