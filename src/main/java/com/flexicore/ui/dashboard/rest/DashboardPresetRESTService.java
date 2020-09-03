package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;

import com.flexicore.ui.dashboard.model.DashboardPreset;
import com.flexicore.ui.dashboard.request.DashboardPresetCreate;
import com.flexicore.ui.dashboard.request.DashboardPresetFiltering;
import com.flexicore.ui.dashboard.request.DashboardPresetUpdate;
import com.flexicore.ui.dashboard.service.DashboardPresetService;
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
@Path("plugins/DashboardPreset")
@Tag(name = "DashboardPreset")
@Tag(name = "Presets")
@Extension
@Component
public class DashboardPresetRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private DashboardPresetService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllDashboardPreset", description = "returns all DashboardPreset")
	@Path("getAllDashboardPreset")
	public PaginationResponse<DashboardPreset> getAllDashboardPreset(
			@HeaderParam("authenticationKey") String authenticationKey,
			DashboardPresetFiltering dashboardPresetFiltering,
			@Context SecurityContext securityContext) {
		return service.getAllDashboardPreset(dashboardPresetFiltering, securityContext);

	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateDashboardPreset", description = "Updates Dashbaord")
	@Path("updateDashboardPreset")
	public DashboardPreset updateDashboardPreset(
			@HeaderParam("authenticationKey") String authenticationKey,
			DashboardPresetUpdate updateDashboardPreset, @Context SecurityContext securityContext) {
		DashboardPreset dashboardPreset = updateDashboardPreset.getId() != null ? service.getByIdOrNull(
				updateDashboardPreset.getId(), DashboardPreset.class, null, securityContext) : null;
		if (dashboardPreset == null) {
			throw new BadRequestException("no ui field with id  "
					+ updateDashboardPreset.getId());
		}
		updateDashboardPreset.setDashboardPreset(dashboardPreset);
		service.validate(updateDashboardPreset, securityContext);

		return service.updateDashboardPreset(updateDashboardPreset, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createDashboardPreset", description = "Creates DashboardPreset ")
	@Path("createDashboardPreset")
	public DashboardPreset createDashboardPreset(
			@HeaderParam("authenticationKey") String authenticationKey,
			DashboardPresetCreate createDashboardPreset, @Context SecurityContext securityContext) {
		service.validate(createDashboardPreset, securityContext);
		return service.createDashboardPreset(createDashboardPreset, securityContext);

	}


}
