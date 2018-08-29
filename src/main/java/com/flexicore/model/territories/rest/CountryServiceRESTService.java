package com.flexicore.model.territories.rest;

import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.OperationsInside;
import javax.interceptor.Interceptors;
import com.flexicore.interfaces.RestServicePlugin;
import javax.ws.rs.Path;
import com.flexicore.model.territories.service.CountryServiceService;
import javax.inject.Inject;
import com.flexicore.security.SecurityContext;
import java.util.List;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import io.swagger.annotations.ApiOperation;
import com.flexicore.annotations.IOperation;
import javax.ws.rs.core.Context;
import com.flexicore.model.territories.data.containers.CountryFiltering;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.data.containers.CountryUpdateContainer;
import javax.ws.rs.BadRequestException;
import com.flexicore.model.territories.data.containers.CountryCreationContainer;

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/CountryService")
public class CountryServiceRESTService implements RestServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private CountryServiceService service;

	@DELETE
	@ApiOperation(value = "deleteCountry", notes = "Deletes Country")
	@IOperation(Name = "deleteCountry", Description = "Deletes Country")
	@Path("deleteCountry/{id}")
	public void deleteCountry(
			@HeaderParam("authenticationKey") String authenticationKey,
			@PathParam("id") String id, @Context SecurityContext securityContext) {
		service.deleteCountry(id, securityContext);
	}

	@POST
	@Produces("application/json")
	@ApiOperation(value = "listAllCountries", notes = "Lists all Countries Filtered")
	@IOperation(Name = "listAllCountries", Description = "Lists all Countries Filtered")
	@Path("listAllCountries")
	public List<Country> listAllCountries(
			@HeaderParam("authenticationKey") String authenticationKey,
			CountryFiltering filtering, @Context SecurityContext securityContext) {
		return service.listAllCountries(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/updateCountry")
	@ApiOperation(value = "updateCountry", notes = "Updates Country")
	@IOperation(Name = "updateCountry", Description = "Updates Country")
	public Country updateCountry(
			@HeaderParam("authenticationKey") String authenticationKey,
			CountryUpdateContainer updateContainer,
			@Context SecurityContext securityContext) {
		Country country = service.getByIdOrNull(updateContainer.getId(),
				Country.class, null, securityContext);
		if (country == null) {
			throw new BadRequestException("no Country with id "
					+ updateContainer.getId());
		}
		updateContainer.setCountry(country);
		return service.updateCountry(updateContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/createCountry")
	@ApiOperation(value = "createCountry", notes = "Creates Country")
	@IOperation(Name = "createCountry", Description = "Creates Country")
	public Country createCountry(
			@HeaderParam("authenticationKey") String authenticationKey,
			CountryCreationContainer creationContainer,
			@Context SecurityContext securityContext) {
		return service.createCountry(creationContainer, securityContext);
	}
}