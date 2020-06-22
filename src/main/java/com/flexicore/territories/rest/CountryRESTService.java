package com.flexicore.territories.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.model.territories.Country;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.reponse.ImportCountriesResponse;
import com.flexicore.territories.request.CountryCreationContainer;
import com.flexicore.territories.request.CountryFiltering;
import com.flexicore.territories.request.CountryUpdateContainer;
import com.flexicore.territories.request.ImportCountriesRequest;
import com.flexicore.territories.service.CountryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/Country")
@Tag(name = "Country")
@Extension
@Component
public class CountryRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private CountryService service;

	@DELETE
	@Operation(summary = "deleteCountry", description = "Deletes Country")
	@IOperation(Name = "deleteCountry", Description = "Deletes Country")
	@Path("deleteCountry/{id}")
	public void deleteCountry(
			@HeaderParam("authenticationKey") String authenticationKey,
			@PathParam("id") String id, @Context SecurityContext securityContext) {
		service.deleteCountry(id, securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "importCountries", description = "Import Countries")
	@IOperation(Name = "importCountries", Description = "Import Countries")
	@Path("importCountries")
	public ImportCountriesResponse importCountries(
			@HeaderParam("authenticationKey") String authenticationKey,
			ImportCountriesRequest addressImportRequest,
			@Context SecurityContext securityContext) {

		return service.importCountries(securityContext, addressImportRequest);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "listAllCountries", description = "Lists all Countries Filtered")
	@IOperation(Name = "listAllCountries", Description = "Lists all Countries Filtered")
	@Path("listAllCountries")
	public List<Country> listAllCountries(
			@HeaderParam("authenticationKey") String authenticationKey,
			CountryFiltering filtering, @Context SecurityContext securityContext) {
		service.validate(filtering, securityContext);
		return service.listAllCountries(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllCountries", description = "Lists all Countries Filtered")
	@IOperation(Name = "getAllCountries", Description = "Lists all Countries Filtered")
	@Path("getAllCountries")
	public PaginationResponse<Country> getAllCountries(
			@HeaderParam("authenticationKey") String authenticationKey,
			CountryFiltering filtering, @Context SecurityContext securityContext) {
		service.validate(filtering, securityContext);
		return service.getAllCountries(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/updateCountry")
	@Operation(summary = "updateCountry", description = "Updates Country")
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
	@Operation(summary = "createCountry", description = "Creates Country")
	@IOperation(Name = "createCountry", Description = "Creates Country")
	public Country createCountry(
			@HeaderParam("authenticationKey") String authenticationKey,
			CountryCreationContainer creationContainer,
			@Context SecurityContext securityContext) {
		return service.createCountry(creationContainer, securityContext);
	}
}