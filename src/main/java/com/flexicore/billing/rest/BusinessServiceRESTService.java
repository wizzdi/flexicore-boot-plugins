package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.request.BusinessServiceCreate;
import com.flexicore.billing.request.BusinessServiceFiltering;
import com.flexicore.billing.request.BusinessServiceUpdate;
import com.flexicore.billing.service.BusinessServiceService;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.billing.model.BusinessService;
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
@Path("plugins/BusinessService")
@RequestScoped
@Tag(name = "BusinessService")
@Extension
@Component
public class BusinessServiceRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private BusinessServiceService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllBusinessServices", description = "Lists all BusinessServices")
	@IOperation(Name = "getAllBusinessServices", Description = "Lists all BusinessServices")
	@Path("getAllBusinessServices")
	public PaginationResponse<BusinessService> getAllBusinessServices(
			@HeaderParam("authenticationKey") String authenticationKey,
			BusinessServiceFiltering filtering, @Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllBusinessServices(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createBusinessService")
	@Operation(summary = "createBusinessService", description = "Creates BusinessService")
	@IOperation(Name = "createBusinessService", Description = "Creates BusinessService")
	public BusinessService createBusinessService(
			@HeaderParam("authenticationKey") String authenticationKey,
			BusinessServiceCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createBusinessService(creationContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updateBusinessService")
	@Operation(summary = "updateBusinessService", description = "Updates BusinessService")
	@IOperation(Name = "updateBusinessService", Description = "Updates BusinessService")
	public BusinessService updateBusinessService(
			@HeaderParam("authenticationKey") String authenticationKey,
			BusinessServiceUpdate updateContainer,
			@Context SecurityContext securityContext) {
		service.validate(updateContainer, securityContext);
		BusinessService businessService = service.getByIdOrNull(updateContainer.getId(),
				BusinessService.class, null, securityContext);
		if (businessService == null) {
			throw new BadRequestException("no BusinessService with id "
					+ updateContainer.getId());
		}
		updateContainer.setBusinessService(businessService);

		return service.updateBusinessService(updateContainer, securityContext);
	}
}