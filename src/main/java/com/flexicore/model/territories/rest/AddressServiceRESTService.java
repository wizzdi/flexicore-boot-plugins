package com.flexicore.model.territories.rest;

import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.OperationsInside;
import javax.interceptor.Interceptors;
import com.flexicore.interfaces.RestServicePlugin;
import javax.ws.rs.Path;

import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.service.AddressServiceService;
import javax.inject.Inject;
import com.flexicore.security.SecurityContext;
import java.util.List;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.HeaderParam;
import io.swagger.annotations.ApiOperation;
import com.flexicore.annotations.IOperation;
import com.flexicore.model.territories.data.containers.AddressUpdateContainer;
import javax.ws.rs.core.Context;
import com.flexicore.model.territories.Address;
import com.flexicore.model.territories.data.containers.AddressFiltering;
import javax.ws.rs.BadRequestException;
import com.flexicore.model.territories.data.containers.AddressCreationContainer;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/AddressService")
public class AddressServiceRESTService implements RestServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private AddressServiceService service;

	@POST
	@Produces("application/json")
	@Path("/updateAddress")
	@ApiOperation(value = "updateAddress", notes = "Updates Address")
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
		Street street = service.getByIdOrNull(updateContainer.getStreetId(),
				Street.class, null, securityContext);
		if (street == null) {
			throw new BadRequestException("no Street with id "
					+ updateContainer.getStreetId());
		}
		updateContainer.setStreet(street);
		return service.updateAddress(updateContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@ApiOperation(value = "listAllAddresses", notes = "Lists all Addresses Filtered")
	@IOperation(Name = "listAllAddresses", Description = "Lists all Addresses Filtered")
	@Path("listAllAddresses")
	public List<Address> listAllAddresses(
			@HeaderParam("authenticationKey") String authenticationKey,
			AddressFiltering filtering, @Context SecurityContext securityContext) {
		return service.listAllAddresses(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createAddress")
	@ApiOperation(value = "createAddress", notes = "Creates Address")
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

	@DELETE
	@ApiOperation(value = "deleteAddress", notes = "Deletes Address")
	@IOperation(Name = "deleteAddress", Description = "Deletes Address")
	@Path("deleteAddress/{id}")
	public void deleteAddress(
			@HeaderParam("authenticationKey") String authenticationKey,
			@PathParam("id") String id, @Context SecurityContext securityContext) {
		service.deleteAddress(id, securityContext);
	}
}