package com.flexicore.territories.rest;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.OperationsInside;
import javax.interceptor.Interceptors;
import com.flexicore.interfaces.RestServicePlugin;
import javax.ws.rs.Path;

import com.flexicore.model.territories.Street;
import com.flexicore.territories.service.AddressService;
import javax.inject.Inject;
import com.flexicore.security.SecurityContext;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.HeaderParam;
import io.swagger.v3.oas.annotations.Operation;
import com.flexicore.annotations.IOperation;
import com.flexicore.territories.data.request.AddressUpdateContainer;
import javax.ws.rs.core.Context;
import com.flexicore.model.territories.Address;
import com.flexicore.territories.data.request.AddressFiltering;
import javax.ws.rs.BadRequestException;
import com.flexicore.territories.data.request.AddressCreationContainer;

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/AddressService")
public class AddressRESTService implements RestServicePlugin {

	@Inject
	@PluginInfo(version = 1)
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
		Address address = service.getByIdOrNull(updateContainer.getId(), Address.class, null, securityContext);
		if (address == null) {
			throw new BadRequestException("no Address with id " + updateContainer.getId());
		}
		updateContainer.setAddress(address);
		service.validate(updateContainer,securityContext);
		return service.updateAddress(updateContainer, securityContext);
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
		Street street = service.getByIdOrNull(creationContainer.getStreetId(),
				Street.class, null, securityContext);
		if (street == null) {
			throw new BadRequestException("no Street with id "
					+ creationContainer.getStreetId());
		}
		creationContainer.setStreet(street);
		return service.createAddress(creationContainer, securityContext);
	}

}