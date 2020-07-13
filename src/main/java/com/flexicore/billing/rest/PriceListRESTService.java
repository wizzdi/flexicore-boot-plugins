package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.PriceList;
import com.flexicore.billing.request.PriceListCreate;
import com.flexicore.billing.request.PriceListFiltering;
import com.flexicore.billing.request.PriceListUpdate;
import com.flexicore.billing.service.PriceListService;
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
@Path("plugins/PriceList")
@RequestScoped
@Tag(name = "PriceList")
@Extension
@Component
public class PriceListRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private PriceListService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllPriceLists", description = "Lists all PriceLists")
	@IOperation(Name = "getAllPriceLists", Description = "Lists all PriceLists")
	@Path("getAllPriceLists")
	public PaginationResponse<PriceList> getAllPriceLists(
			@HeaderParam("authenticationKey") String authenticationKey,
			PriceListFiltering filtering, @Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllPriceLists(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createPriceList")
	@Operation(summary = "createPriceList", description = "Creates PriceList")
	@IOperation(Name = "createPriceList", Description = "Creates PriceList")
	public PriceList createPriceList(
			@HeaderParam("authenticationKey") String authenticationKey,
			PriceListCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createPriceList(creationContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updatePriceList")
	@Operation(summary = "updatePriceList", description = "Updates PriceList")
	@IOperation(Name = "updatePriceList", Description = "Updates PriceList")
	public PriceList updatePriceList(
			@HeaderParam("authenticationKey") String authenticationKey,
			PriceListUpdate updateContainer,
			@Context SecurityContext securityContext) {
		service.validate(updateContainer, securityContext);
		PriceList priceList = service.getByIdOrNull(updateContainer.getId(),
				PriceList.class, null, securityContext);
		if (priceList == null) {
			throw new BadRequestException("no PriceList with id "
					+ updateContainer.getId());
		}
		updateContainer.setPriceList(priceList);

		return service.updatePriceList(updateContainer, securityContext);
	}
}