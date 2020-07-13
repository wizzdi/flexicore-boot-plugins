package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.request.CurrencyCreate;
import com.flexicore.billing.request.CurrencyFiltering;
import com.flexicore.billing.request.CurrencyUpdate;
import com.flexicore.billing.service.CurrencyService;
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
@Path("plugins/Currency")
@RequestScoped
@Tag(name = "Currency")
@Extension
@Component
public class CurrencyRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private CurrencyService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllCurrencies", description = "Lists all Currencies")
	@IOperation(Name = "getAllCurrencies", Description = "Lists all Currencies")
	@Path("getAllCurrencies")
	public PaginationResponse<Currency> getAllCurrencies(
			@HeaderParam("authenticationKey") String authenticationKey,
			CurrencyFiltering filtering, @Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllCurrencies(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createCurrency")
	@Operation(summary = "createCurrency", description = "Creates Currency")
	@IOperation(Name = "createCurrency", Description = "Creates Currency")
	public Currency createCurrency(
			@HeaderParam("authenticationKey") String authenticationKey,
			CurrencyCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createCurrency(creationContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updateCurrency")
	@Operation(summary = "updateCurrency", description = "Updates Currency")
	@IOperation(Name = "updateCurrency", Description = "Updates Currency")
	public Currency updateCurrency(
			@HeaderParam("authenticationKey") String authenticationKey,
			CurrencyUpdate updateContainer,
			@Context SecurityContext securityContext) {
		service.validate(updateContainer, securityContext);
		Currency currency = service.getByIdOrNull(updateContainer.getId(),
				Currency.class, null, securityContext);
		if (currency == null) {
			throw new BadRequestException("no Currency with id "
					+ updateContainer.getId());
		}
		updateContainer.setCurrency(currency);

		return service.updateCurrency(updateContainer, securityContext);
	}
}