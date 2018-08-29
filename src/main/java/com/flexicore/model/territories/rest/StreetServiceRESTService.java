package com.flexicore.model.territories.rest;

import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.OperationsInside;
import javax.interceptor.Interceptors;
import com.flexicore.interfaces.RestServicePlugin;
import javax.ws.rs.Path;

import com.flexicore.model.territories.City;
import com.flexicore.model.territories.service.StreetServiceService;
import javax.inject.Inject;
import com.flexicore.security.SecurityContext;
import java.util.List;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.flexicore.model.territories.data.containers.StreetFiltering;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.HeaderParam;
import io.swagger.annotations.ApiOperation;
import com.flexicore.annotations.IOperation;
import javax.ws.rs.core.Context;
import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.data.containers.StreetUpdateContainer;
import javax.ws.rs.BadRequestException;
import com.flexicore.model.territories.data.containers.StreetCreationContainer;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/StreetService")
public class StreetServiceRESTService implements RestServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private StreetServiceService service;

	@POST
	@Produces("application/json")
	@ApiOperation(value = "listAllStreets", notes = "Lists all Streets Filtered")
	@IOperation(Name = "listAllStreets", Description = "Lists all Streets Filtered")
	@Path("listAllStreets")
	public List<Street> listAllStreets(
			@HeaderParam("authenticationKey") String authenticationKey,
			StreetFiltering filtering, @Context SecurityContext securityContext) {
		return service.listAllStreets(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/updateStreet")
	@ApiOperation(value = "updateStreet", notes = "Updates Street")
	@IOperation(Name = "updateStreet", Description = "Updates Street")
	public Street updateStreet(
			@HeaderParam("authenticationKey") String authenticationKey,
			StreetUpdateContainer updateContainer,
			@Context SecurityContext securityContext) {
		Street street = service.getByIdOrNull(updateContainer.getId(),
				Street.class, null, securityContext);
		if (street == null) {
			throw new BadRequestException("no Street with id "
					+ updateContainer.getId());
		}
		updateContainer.setStreet(street);
		City city = service.getByIdOrNull(updateContainer.getCityId(),
				City.class, null, securityContext);
		if (city == null) {
			throw new BadRequestException("no City with id "
					+ updateContainer.getCityId());
		}
		updateContainer.setCity(city);
		return service.updateStreet(updateContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/createStreet")
	@ApiOperation(value = "createStreet", notes = "Creates Street")
	@IOperation(Name = "createStreet", Description = "Creates Street")
	public Street createStreet(
			@HeaderParam("authenticationKey") String authenticationKey,
			StreetCreationContainer creationContainer,
			@Context SecurityContext securityContext) {
		City city = service.getByIdOrNull(creationContainer.getCityId(),
				City.class, null, securityContext);
		if (city == null) {
			throw new BadRequestException("no City with id "
					+ creationContainer.getCityId());
		}
		creationContainer.setCity(city);
		return service.createStreet(creationContainer, securityContext);
	}

	@DELETE
	@ApiOperation(value = "deleteStreet", notes = "Deletes Street")
	@IOperation(Name = "deleteStreet", Description = "Deletes Street")
	@Path("deleteStreet/{id}")
	public void deleteStreet(
			@HeaderParam("authenticationKey") String authenticationKey,
			@PathParam("id") String id, @Context SecurityContext securityContext) {
		service.deleteStreet(id, securityContext);
	}
}