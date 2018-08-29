package com.flexicore.model.territories.rest;

import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.OperationsInside;
import javax.interceptor.Interceptors;
import com.flexicore.interfaces.RestServicePlugin;
import javax.ws.rs.Path;

import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.service.CityServiceService;
import javax.inject.Inject;
import com.flexicore.security.SecurityContext;
import java.util.List;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.flexicore.model.territories.data.containers.CityFiltering;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.HeaderParam;
import io.swagger.annotations.ApiOperation;
import com.flexicore.annotations.IOperation;
import javax.ws.rs.core.Context;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.data.containers.CityUpdateContainer;
import javax.ws.rs.BadRequestException;
import com.flexicore.model.territories.data.containers.CityCreationContainer;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/CityService")
public class CityServiceRESTService implements RestServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private CityServiceService service;

	@POST
	@Produces("application/json")
	@ApiOperation(value = "listAllCities", notes = "Lists all Cities Filtered")
	@IOperation(Name = "listAllCities", Description = "Lists all Cities Filtered")
	@Path("listAllCities")
	public List<City> listAllCities(
			@HeaderParam("authenticationKey") String authenticationKey,
			CityFiltering filtering, @Context SecurityContext securityContext) {
		return service.listAllCities(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/updateCity")
	@ApiOperation(value = "updateCity", notes = "Updates City")
	@IOperation(Name = "updateCity", Description = "Updates City")
	public City updateCity(
			@HeaderParam("authenticationKey") String authenticationKey,
			CityUpdateContainer updateContainer,
			@Context SecurityContext securityContext) {
		City city = service.getByIdOrNull(updateContainer.getId(), City.class,
				null, securityContext);
		if (city == null) {
			throw new BadRequestException("no City with id "
					+ updateContainer.getId());
		}
		updateContainer.setCity(city);
		Country country = service.getByIdOrNull(updateContainer.getCountryId(),
				Country.class, null, securityContext);
		if (country == null) {
			throw new BadRequestException("no Country with id "
					+ updateContainer.getCountryId());
		}
		updateContainer.setCountry(country);
		return service.updateCity(updateContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/createCity")
	@ApiOperation(value = "createCity", notes = "Creates City")
	@IOperation(Name = "createCity", Description = "Creates City")
	public City createCity(
			@HeaderParam("authenticationKey") String authenticationKey,
			CityCreationContainer creationContainer,
			@Context SecurityContext securityContext) {
		Country country = service.getByIdOrNull(
				creationContainer.getCountryId(), Country.class, null,
				securityContext);
		if (country == null) {
			throw new BadRequestException("no Country with id "
					+ creationContainer.getCountryId());
		}
		creationContainer.setCountry(country);
		return service.createCity(creationContainer, securityContext);
	}

	@DELETE
	@ApiOperation(value = "deleteCity", notes = "Deletes City")
	@IOperation(Name = "deleteCity", Description = "Deletes City")
	@Path("deleteCity/{id}")
	public void deleteCity(
			@HeaderParam("authenticationKey") String authenticationKey,
			@PathParam("id") String id, @Context SecurityContext securityContext) {
		service.deleteCity(id, securityContext);
	}
}