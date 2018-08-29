package com.flexicore.model.territories.rest;

import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.annotations.OperationsInside;
import javax.interceptor.Interceptors;
import com.flexicore.interfaces.RestServicePlugin;
import javax.ws.rs.Path;
import com.flexicore.model.territories.service.ZipServiceService;
import javax.inject.Inject;
import com.flexicore.security.SecurityContext;
import java.util.List;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.HeaderParam;
import io.swagger.annotations.ApiOperation;
import com.flexicore.annotations.IOperation;
import com.flexicore.model.territories.data.containers.ZipUpdateContainer;
import javax.ws.rs.core.Context;
import com.flexicore.model.territories.Zip;
import javax.ws.rs.BadRequestException;
import com.flexicore.model.territories.data.containers.ZipCreationContainer;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import com.flexicore.model.territories.data.containers.ZipFiltering;

@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/ZipService")
public class ZipServiceRESTService implements RestServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private ZipServiceService service;

	@POST
	@Produces("application/json")
	@Path("/updateZip")
	@ApiOperation(value = "updateZip", notes = "Updates Zip")
	@IOperation(Name = "updateZip", Description = "Updates Zip")
	public Zip updateZip(
			@HeaderParam("authenticationKey") String authenticationKey,
			ZipUpdateContainer updateContainer,
			@Context SecurityContext securityContext) {
		Zip zip = service.getByIdOrNull(updateContainer.getId(), Zip.class,
				null, securityContext);
		if (zip == null) {
			throw new BadRequestException("no Zip with id "
					+ updateContainer.getId());
		}
		updateContainer.setZip(zip);
		return service.updateZip(updateContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/createZip")
	@ApiOperation(value = "createZip", notes = "Creates Zip")
	@IOperation(Name = "createZip", Description = "Creates Zip")
	public Zip createZip(
			@HeaderParam("authenticationKey") String authenticationKey,
			ZipCreationContainer creationContainer,
			@Context SecurityContext securityContext) {
		return service.createZip(creationContainer, securityContext);
	}

	@DELETE
	@ApiOperation(value = "deleteZip", notes = "Deletes Zip")
	@IOperation(Name = "deleteZip", Description = "Deletes Zip")
	@Path("deleteZip/{id}")
	public void deleteZip(
			@HeaderParam("authenticationKey") String authenticationKey,
			@PathParam("id") String id, @Context SecurityContext securityContext) {
		service.deleteZip(id, securityContext);
	}

	@POST
	@Produces("application/json")
	@ApiOperation(value = "listAllZips", notes = "Lists all Zips Filtered")
	@IOperation(Name = "listAllZips", Description = "Lists all Zips Filtered")
	@Path("listAllZips")
	public List<Zip> listAllZips(
			@HeaderParam("authenticationKey") String authenticationKey,
			ZipFiltering filtering, @Context SecurityContext securityContext) {
		return service.listAllZips(securityContext, filtering);
	}
}