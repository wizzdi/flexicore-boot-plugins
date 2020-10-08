package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.request.GridLayoutCreate;
import com.flexicore.ui.dashboard.request.GridLayoutFiltering;
import com.flexicore.ui.dashboard.request.GridLayoutUpdate;
import com.flexicore.ui.dashboard.service.GridLayoutService;
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
@Path("plugins/GridLayout")
@Tag(name = "GridLayout")
@Tag(name = "Presets")
@Extension
@Component
public class GridLayoutRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private GridLayoutService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllGridLayout", description = "returns all GridLayout")
	@Path("getAllGridLayout")
	public PaginationResponse<GridLayout> getAllGridLayout(
			@HeaderParam("authenticationKey") String authenticationKey,
			GridLayoutFiltering gridLayoutFiltering,
			@Context SecurityContext securityContext) {
		service.validate(gridLayoutFiltering, securityContext);
		return service.getAllGridLayout(gridLayoutFiltering, securityContext);

	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateGridLayout", description = "Updates Dashbaord")
	@Path("updateGridLayout")
	public GridLayout updateGridLayout(
			@HeaderParam("authenticationKey") String authenticationKey,
			GridLayoutUpdate updateGridLayout, @Context SecurityContext securityContext) {
		GridLayout gridLayout = updateGridLayout.getId() != null ? service.getByIdOrNull(
				updateGridLayout.getId(), GridLayout.class, null, securityContext) : null;
		if (gridLayout == null) {
			throw new BadRequestException("no ui field with id  "
					+ updateGridLayout.getId());
		}
		updateGridLayout.setGridLayout(gridLayout);
		service.validate(updateGridLayout, securityContext);

		return service.updateGridLayout(updateGridLayout, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createGridLayout", description = "Creates GridLayout ")
	@Path("createGridLayout")
	public GridLayout createGridLayout(
			@HeaderParam("authenticationKey") String authenticationKey,
			GridLayoutCreate createGridLayout, @Context SecurityContext securityContext) {
		service.validate(createGridLayout, securityContext);
		return service.createGridLayout(createGridLayout, securityContext);

	}


}
