package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.dashboard.model.DashboardPreset;
import com.flexicore.ui.dashboard.request.DashboardPresetCreate;
import com.flexicore.ui.dashboard.request.DashboardPresetFilter;
import com.flexicore.ui.dashboard.request.DashboardPresetUpdate;
import com.flexicore.ui.dashboard.service.DashboardPresetService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


/**
 * Created by Asaf on 04/06/2017.
 */


@OperationsInside
@RestController
@RequestMapping("plugins/DashboardPreset")
@Tag(name = "DashboardPreset")
@Tag(name = "Presets")
@Extension
@Component
public class DashboardPresetController implements Plugin {

	
	@Autowired
	private DashboardPresetService service;

	
	
	@Operation(summary = "getAllDashboardPreset", description = "returns all DashboardPreset")
	@PostMapping("getAllDashboardPreset")
	public PaginationResponse<DashboardPreset> getAllDashboardPreset(
			@RequestBody
			DashboardPresetFilter dashboardPresetFilter,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(dashboardPresetFilter,securityContext);
		return service.getAllDashboardPreset(dashboardPresetFilter, securityContext);

	}

	
	
	@Operation(summary = "updateDashboardPreset", description = "Updates Dashbaord")
	@PutMapping("updateDashboardPreset")
	public DashboardPreset updateDashboardPreset(
			@RequestBody
			DashboardPresetUpdate updateDashboardPreset, @RequestAttribute SecurityContextBase securityContext) {
		DashboardPreset dashboardPreset = updateDashboardPreset.getId() != null ? service.getByIdOrNull(
				updateDashboardPreset.getId(), DashboardPreset.class, SecuredBasic_.security, securityContext) : null;
		if (dashboardPreset == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ui field with id  "
					+ updateDashboardPreset.getId());
		}
		updateDashboardPreset.setDashboardPreset(dashboardPreset);
		service.validate(updateDashboardPreset, securityContext);

		return service.updateDashboardPreset(updateDashboardPreset, securityContext);

	}

	
	
	@Operation(summary = "createDashboardPreset", description = "Creates DashboardPreset ")
	@PostMapping("createDashboardPreset")
	public DashboardPreset createDashboardPreset(
			@RequestBody
			DashboardPresetCreate createDashboardPreset, @RequestAttribute SecurityContextBase securityContext) {
		service.validate(createDashboardPreset, securityContext);
		return service.createDashboardPreset(createDashboardPreset, securityContext);

	}


}
