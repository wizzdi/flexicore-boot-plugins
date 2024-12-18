package com.flexicore.ui.rest;

import com.flexicore.annotations.OperationsInside;

import com.flexicore.ui.model.GridPreset_;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.request.GridPresetCopy;
import com.flexicore.ui.request.GridPresetCreate;
import com.flexicore.ui.request.GridPresetUpdate;
import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.request.GridPresetFiltering;
import com.flexicore.ui.service.GridPresetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;




import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Asaf on 04/06/2017.
 */


@OperationsInside
@RestController
@RequestMapping("plugins/GridPresets")
@Tag(name = "GridPresets", description = "Grid presets support free definition of grids using Dynamic Execution as source of data")
@Tag(name = "Presets")
@Extension
@Component
public class GridPresetController implements Plugin {

	
	@Autowired
	private GridPresetService service;

	

	@Operation(summary = "getAllGridPresets", description = "returns all GridPresets")
	@PostMapping("getAllGridPresets")
	public PaginationResponse<GridPreset> getAllGridPresets(
			 @RequestBody
			GridPresetFiltering gridPresetFiltering,
			@RequestAttribute SecurityContext securityContext) {
		return service.getAllGridPresets(gridPresetFiltering, securityContext);

	}

	

	@Operation(summary = "updateGridPreset", description = "Updates Dashbaord")
	@PutMapping("updateGridPreset")
	public GridPreset updateGridPreset(
			 @RequestBody
			GridPresetUpdate updateGridPreset,
			@RequestAttribute SecurityContext securityContext) {
		GridPreset gridPreset = updateGridPreset.getId() != null ? service.getByIdOrNull(updateGridPreset.getId(), GridPreset.class,securityContext) : null;
		if (gridPreset == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no GridPreset with id  "
					+ updateGridPreset.getId());
		}
		updateGridPreset.setPreset(gridPreset);
		service.validate(updateGridPreset, securityContext);

		return service.updateGridPreset(updateGridPreset, securityContext);

	}

	

	@Operation(summary = "createGridPreset", description = "Creates Grid Preset ")
	@PostMapping("createGridPreset")
	public GridPreset createGridPreset(
			 @RequestBody
			GridPresetCreate createGridPreset,
			@RequestAttribute SecurityContext securityContext) {
		service.validate(createGridPreset, securityContext);
		return service.createGridPreset(createGridPreset, securityContext);

	}

	

	@Operation(summary = "copyGridPreset", description = "Copies Grid Preset")
	@PostMapping("copyGridPreset")
	public GridPreset copyGridPreset(
			 @RequestBody
			GridPresetCopy gridPresetCopy,
			@RequestAttribute SecurityContext securityContext) {
		service.validate(gridPresetCopy, securityContext);
		return service.copyGridPreset(gridPresetCopy, securityContext);

	}

}
