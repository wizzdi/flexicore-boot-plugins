package com.flexicore.territories.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;

import com.flexicore.annotations.ProtectedREST;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.model.territories.Address;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.AddressCreationContainer;
import com.flexicore.territories.request.AddressFiltering;
import com.flexicore.territories.request.AddressUpdateContainer;
import com.flexicore.territories.reponse.AddressImportResponse;
import com.flexicore.territories.request.AddressImportRequest;
import com.flexicore.territories.service.AddressService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/Address")
@OpenAPIDefinition(tags = {@Tag(name = "Address", description = "Address Api"),
		@Tag(name = "Street", description = "Street Api"),
		@Tag(name = "City", description = "City Api"),
		@Tag(name = "Country", description = "Country Api"),
		@Tag(name = "Neighbourhood", description = "Neighbourhood Api")

})
@Tag(name = "Address")
@Extension
@Component
public class AddressRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private AddressService service;

	@POST
	@Produces("application/json")
	@Path("/updateAddress")
	@Operation(summary = "updateAddress", description = "Updates Address")
	@IOperation(Name = "updateAddress", Description = "Updates Address")
	public Address updateAddress(
			@HeaderParam("authenticationKey") String authenticationKey,
			AddressUpdateContainer updateContainer,
			@Context SecurityContext securityContext) {
		Address address = service.getByIdOrNull(updateContainer.getId(),
				Address.class, null, securityContext);
		if (address == null) {
			throw new BadRequestException("no Address with id "
					+ updateContainer.getId());
		}
		updateContainer.setAddress(address);
		service.validate(updateContainer, securityContext);
		return service.updateAddress(updateContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "importAddresses", description = "Import Addresses")
	@IOperation(Name = "importAddresses", Description = "Import Addresses")
	@Path("importAddresses")
	public AddressImportResponse importAddresses(
			@HeaderParam("authenticationKey") String authenticationKey,
			AddressImportRequest addressImportRequest,
			@Context SecurityContext securityContext) {
		return service.importAddresses(securityContext, addressImportRequest);
	}

	@POST
	@Produces("application/json")
	@Operation(summary = "listAllAddresses", description = "Lists all Addresses Filtered")
	@IOperation(Name = "listAllAddresses", Description = "Lists all Addresses Filtered")
	@Path("listAllAddresses")
	public PaginationResponse<Address> listAllAddresses(
			@HeaderParam("authenticationKey") String authenticationKey,
			AddressFiltering filtering, @Context SecurityContext securityContext) {
		return service.listAllAddresses(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createAddress")
	@Operation(summary = "createAddress", description = "Creates Address")
	@IOperation(Name = "createAddress", Description = "Creates Address")
	public Address createAddress(
			@HeaderParam("authenticationKey") String authenticationKey,
			AddressCreationContainer creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);
		return service.createAddress(creationContainer, securityContext);
	}

}