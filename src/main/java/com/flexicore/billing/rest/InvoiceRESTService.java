package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.Invoice;
import com.flexicore.billing.request.InvoiceCreate;
import com.flexicore.billing.request.InvoiceFiltering;
import com.flexicore.billing.request.InvoiceUpdate;
import com.flexicore.billing.service.InvoiceService;
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
@Path("plugins/Invoice")
@RequestScoped
@Tag(name = "Invoice")
@Extension
@Component
public class InvoiceRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private InvoiceService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllInvoices", description = "Lists all Invoices")
	@IOperation(Name = "getAllInvoices", Description = "Lists all Invoices")
	@Path("getAllInvoices")
	public PaginationResponse<Invoice> getAllInvoices(
			@HeaderParam("authenticationKey") String authenticationKey,
			InvoiceFiltering filtering, @Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllInvoices(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createInvoice")
	@Operation(summary = "createInvoice", description = "Creates Invoice")
	@IOperation(Name = "createInvoice", Description = "Creates Invoice")
	public Invoice createInvoice(
			@HeaderParam("authenticationKey") String authenticationKey,
			InvoiceCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createInvoice(creationContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updateInvoice")
	@Operation(summary = "updateInvoice", description = "Updates Invoice")
	@IOperation(Name = "updateInvoice", Description = "Updates Invoice")
	public Invoice updateInvoice(
			@HeaderParam("authenticationKey") String authenticationKey,
			InvoiceUpdate updateContainer,
			@Context SecurityContext securityContext) {
		service.validate(updateContainer, securityContext);
		Invoice invoice = service.getByIdOrNull(updateContainer.getId(),
				Invoice.class, null, securityContext);
		if (invoice == null) {
			throw new BadRequestException("no Invoice with id "
					+ updateContainer.getId());
		}
		updateContainer.setInvoice(invoice);

		return service.updateInvoice(updateContainer, securityContext);
	}
}