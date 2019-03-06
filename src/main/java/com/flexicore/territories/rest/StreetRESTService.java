package com.flexicore.territories.rest;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.OperationsInside;
import javax.interceptor.Interceptors;
import com.flexicore.interfaces.RestServicePlugin;
import javax.ws.rs.Path;

import com.flexicore.model.territories.City;
import com.flexicore.territories.service.StreetService;
import javax.inject.Inject;
import com.flexicore.security.SecurityContext;
import java.util.List;

import com.flexicore.territories.data.request.StreetFiltering;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.HeaderParam;
import io.swagger.v3.oas.annotations.Operation;
import com.flexicore.annotations.IOperation;
import javax.ws.rs.core.Context;
import com.flexicore.model.territories.Street;
import com.flexicore.territories.data.request.StreetUpdateContainer;
import javax.ws.rs.BadRequestException;
import com.flexicore.territories.data.request.StreetCreationContainer;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/Street")
@Tag(name = "Street")

public class StreetRESTService implements RestServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private StreetService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "listAllStreets", description = "Lists all Streets Filtered")
	@IOperation(Name = "listAllStreets", Description = "Lists all Streets Filtered")
	@Path("listAllStreets")
	public List<Street> listAllStreets(
			@HeaderParam("authenticationKey") String authenticationKey,
			StreetFiltering filtering, @Context SecurityContext securityContext) {
		service.validate(filtering,securityContext);
		return service.listAllStreets(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllStreets", description = "Lists all Streets Filtered")
	@IOperation(Name = "getAllStreets", Description = "Lists all Streets Filtered")
	@Path("getAllStreets")
	public PaginationResponse<Street> getAllStreets(
			@HeaderParam("authenticationKey") String authenticationKey,
			StreetFiltering filtering, @Context SecurityContext securityContext) {
		service.validate(filtering,securityContext);
		return service.getAllStreets(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/updateStreet")
	@Operation(summary = "updateStreet", description = "Updates Street")
	@IOperation(Name = "updateStreet", Description = "Updates Street")
	public Street updateStreet(
			@HeaderParam("authenticationKey") String authenticationKey,
			StreetUpdateContainer updateContainer,
			@Context SecurityContext securityContext) {
		Street street = service.getByIdOrNull(updateContainer.getId(),
				Street.class, null, securityContext);
		if (street == null) {
			throw new BadRequestException("no Street with id " + updateContainer.getId());
		}
		updateContainer.setStreet(street);
		service.validate(updateContainer, securityContext);
		return service.updateStreet(updateContainer, securityContext);
	}



	@POST
	@Produces("application/json")
	@Path("/createStreet")
	@Operation(summary = "createStreet", description = "Creates Street")
	@IOperation(Name = "createStreet", Description = "Creates Street")
	public Street createStreet(
			@HeaderParam("authenticationKey") String authenticationKey,
			StreetCreationContainer creationContainer,
			@Context SecurityContext securityContext) {
		City city = service.getByIdOrNull(creationContainer.getCityId(),
				City.class, null, securityContext);
		if (city == null) {
			throw new BadRequestException("no City with id " + creationContainer.getCityId());
		}
		creationContainer.setCity(city);
		return service.createStreet(creationContainer, securityContext);
	}

	@DELETE
	@Operation(summary = "deleteStreet", description = "Deletes Street")
	@IOperation(Name = "deleteStreet", Description = "Deletes Street")
	@Path("deleteStreet/{id}")
	public void deleteStreet(
			@HeaderParam("authenticationKey") String authenticationKey,
			@PathParam("id") String id, @Context SecurityContext securityContext) {
		service.deleteStreet(id, securityContext);
	}
}