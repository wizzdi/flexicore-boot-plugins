package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.CellToLayout;
import com.flexicore.ui.dashboard.request.CellToLayoutCreate;
import com.flexicore.ui.dashboard.request.CellToLayoutFiltering;
import com.flexicore.ui.dashboard.request.CellToLayoutUpdate;
import com.flexicore.ui.dashboard.service.CellToLayoutService;
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
@Path("plugins/CellToLayout")
@Tag(name = "CellToLayout")
@Tag(name = "Presets")
@Extension
@Component
public class CellToLayoutRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private CellToLayoutService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllCellToLayout", description = "returns all CellToLayout")
	@Path("getAllCellToLayout")
	public PaginationResponse<CellToLayout> getAllCellToLayout(
			@HeaderParam("authenticationKey") String authenticationKey,
			CellToLayoutFiltering cellToLayoutFiltering,
			@Context SecurityContext securityContext) {
		return service.getAllCellToLayout(cellToLayoutFiltering, securityContext);

	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateCellToLayout", description = "Updates Dashbaord")
	@Path("updateCellToLayout")
	public CellToLayout updateCellToLayout(
			@HeaderParam("authenticationKey") String authenticationKey,
			CellToLayoutUpdate updateCellToLayout, @Context SecurityContext securityContext) {
		CellToLayout cellToLayout = updateCellToLayout.getId() != null ? service.getByIdOrNull(
				updateCellToLayout.getId(), CellToLayout.class, null, securityContext) : null;
		if (cellToLayout == null) {
			throw new BadRequestException("no ui field with id  "
					+ updateCellToLayout.getId());
		}
		updateCellToLayout.setCellToLayout(cellToLayout);
		service.validate(updateCellToLayout, securityContext);

		return service.updateCellToLayout(updateCellToLayout, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createCellToLayout", description = "Creates CellToLayout ")
	@Path("createCellToLayout")
	public CellToLayout createCellToLayout(
			@HeaderParam("authenticationKey") String authenticationKey,
			CellToLayoutCreate createCellToLayout, @Context SecurityContext securityContext) {
		service.validate(createCellToLayout, securityContext);
		return service.createCellToLayout(createCellToLayout, securityContext);

	}


}
