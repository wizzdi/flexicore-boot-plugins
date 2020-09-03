package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.CellContentElement;
import com.flexicore.ui.dashboard.request.CellContentElementCreate;
import com.flexicore.ui.dashboard.request.CellContentElementFiltering;
import com.flexicore.ui.dashboard.request.CellContentElementUpdate;
import com.flexicore.ui.dashboard.service.CellContentElementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/CellContentElement")
@Tag(name = "CellContentElement")
@Tag(name = "Presets")
@Extension
@Component
public class CellContentElementRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private CellContentElementService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllCellContentElement", description = "returns all CellContentElement")
	@Path("getAllCellContentElement")
	public PaginationResponse<CellContentElement> getAllCellContentElement(
			@HeaderParam("authenticationKey") String authenticationKey,
			CellContentElementFiltering cellContentElementFiltering,
			@Context SecurityContext securityContext) {
		return service.getAllCellContentElement(cellContentElementFiltering, securityContext);

	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateCellContentElement", description = "Updates Dashbaord")
	@Path("updateCellContentElement")
	public CellContentElement updateCellContentElement(
			@HeaderParam("authenticationKey") String authenticationKey,
			CellContentElementUpdate updateCellContentElement, @Context SecurityContext securityContext) {
		CellContentElement cellContentElement = updateCellContentElement.getId() != null ? service.getByIdOrNull(
				updateCellContentElement.getId(), CellContentElement.class, null, securityContext) : null;
		if (cellContentElement == null) {
			throw new BadRequestException("no ui field with id  "
					+ updateCellContentElement.getId());
		}
		updateCellContentElement.setCellContentElement(cellContentElement);
		service.validate(updateCellContentElement, securityContext);

		return service.updateCellContentElement(updateCellContentElement, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createCellContentElement", description = "Creates CellContentElement ")
	@Path("createCellContentElement")
	public CellContentElement createCellContentElement(
			@HeaderParam("authenticationKey") String authenticationKey,
			CellContentElementCreate createCellContentElement, @Context SecurityContext securityContext) {
		service.validate(createCellContentElement, securityContext);
		return service.createCellContentElement(createCellContentElement, securityContext);

	}


}
