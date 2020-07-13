package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.PriceListToService;
import com.flexicore.billing.request.PriceListToServiceCreate;
import com.flexicore.billing.request.PriceListToServiceFiltering;
import com.flexicore.billing.request.PriceListToServiceUpdate;
import com.flexicore.billing.service.PriceListToServiceService;
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
@Path("plugins/PriceListToService")
@RequestScoped
@Tag(name = "PriceListToService")
@Extension
@Component
public class PriceListToServiceRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private PriceListToServiceService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllPriceListToServices", description = "Lists all PriceListToServices")
	@IOperation(Name = "getAllPriceListToServices", Description = "Lists all PriceListToServices")
	@Path("getAllPriceListToServices")
	public PaginationResponse<PriceListToService> getAllPriceListToServices(
			@HeaderParam("authenticationKey") String authenticationKey,
			PriceListToServiceFiltering filtering, @Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllPriceListToServices(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createPriceListToService")
	@Operation(summary = "createPriceListToService", description = "Creates PriceListToService")
	@IOperation(Name = "createPriceListToService", Description = "Creates PriceListToService")
	public PriceListToService createPriceListToService(
			@HeaderParam("authenticationKey") String authenticationKey,
			PriceListToServiceCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createPriceListToService(creationContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updatePriceListToService")
	@Operation(summary = "updatePriceListToService", description = "Updates PriceListToService")
	@IOperation(Name = "updatePriceListToService", Description = "Updates PriceListToService")
	public PriceListToService updatePriceListToService(
			@HeaderParam("authenticationKey") String authenticationKey,
			PriceListToServiceUpdate updateContainer,
			@Context SecurityContext securityContext) {
		service.validate(updateContainer, securityContext);
		PriceListToService priceListToService = service.getByIdOrNull(updateContainer.getId(),
				PriceListToService.class, null, securityContext);
		if (priceListToService == null) {
			throw new BadRequestException("no PriceListToService with id "
					+ updateContainer.getId());
		}
		updateContainer.setPriceListToService(priceListToService);

		return service.updatePriceListToService(updateContainer, securityContext);
	}
}