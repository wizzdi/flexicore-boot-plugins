package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.GridLayoutCell;
import com.flexicore.ui.dashboard.request.GridLayoutCellCreate;
import com.flexicore.ui.dashboard.request.GridLayoutCellFiltering;
import com.flexicore.ui.dashboard.request.GridLayoutCellUpdate;
import com.flexicore.ui.dashboard.service.GridLayoutCellService;
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
@Path("plugins/GridLayoutCell")
@Tag(name = "GridLayoutCell")
@Tag(name = "Presets")
@Extension
@Component
public class GridLayoutCellRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private GridLayoutCellService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllGridLayoutCell", description = "returns all GridLayoutCell")
	@Path("getAllGridLayoutCell")
	public PaginationResponse<GridLayoutCell> getAllGridLayoutCell(
			@HeaderParam("authenticationKey") String authenticationKey,
			GridLayoutCellFiltering gridLayoutCellFiltering,
			@Context SecurityContext securityContext) {
		return service.getAllGridLayoutCell(gridLayoutCellFiltering, securityContext);

	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateGridLayoutCell", description = "Updates Dashbaord")
	@Path("updateGridLayoutCell")
	public GridLayoutCell updateGridLayoutCell(
			@HeaderParam("authenticationKey") String authenticationKey,
			GridLayoutCellUpdate updateGridLayoutCell, @Context SecurityContext securityContext) {
		GridLayoutCell gridLayoutCell = updateGridLayoutCell.getId() != null ? service.getByIdOrNull(
				updateGridLayoutCell.getId(), GridLayoutCell.class, null, securityContext) : null;
		if (gridLayoutCell == null) {
			throw new BadRequestException("no ui field with id  "
					+ updateGridLayoutCell.getId());
		}
		updateGridLayoutCell.setGridLayoutCell(gridLayoutCell);
		service.validate(updateGridLayoutCell, securityContext);

		return service.updateGridLayoutCell(updateGridLayoutCell, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createGridLayoutCell", description = "Creates GridLayoutCell ")
	@Path("createGridLayoutCell")
	public GridLayoutCell createGridLayoutCell(
			@HeaderParam("authenticationKey") String authenticationKey,
			GridLayoutCellCreate createGridLayoutCell, @Context SecurityContext securityContext) {
		service.validate(createGridLayoutCell, securityContext);
		return service.createGridLayoutCell(createGridLayoutCell, securityContext);

	}


}
