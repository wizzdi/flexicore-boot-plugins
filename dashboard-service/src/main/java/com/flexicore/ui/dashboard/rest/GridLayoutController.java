package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.request.GridLayoutCreate;
import com.flexicore.ui.dashboard.request.GridLayoutFilter;
import com.flexicore.ui.dashboard.request.GridLayoutUpdate;
import com.flexicore.ui.dashboard.service.GridLayoutService;
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
@RequestMapping("plugins/GridLayout")
@Tag(name = "GridLayout")
@Tag(name = "Presets")
@Extension
@Component
public class GridLayoutController implements Plugin {

	
	@Autowired
	private GridLayoutService service;

	
	
	@Operation(summary = "getAllGridLayout", description = "returns all GridLayout")
	@PostMapping("getAllGridLayout")
	public PaginationResponse<GridLayout> getAllGridLayout(
			@RequestBody
			GridLayoutFilter gridLayoutFilter,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(gridLayoutFilter, securityContext);
		return service.getAllGridLayout(gridLayoutFilter, securityContext);

	}

	
	
	@Operation(summary = "updateGridLayout", description = "Updates Dashbaord")
	@PutMapping("updateGridLayout")
	public GridLayout updateGridLayout(
			@RequestBody
			GridLayoutUpdate updateGridLayout, @RequestAttribute SecurityContextBase securityContext) {
		GridLayout gridLayout = updateGridLayout.getId() != null ? service.getByIdOrNull(
				updateGridLayout.getId(), GridLayout.class, SecuredBasic_.security, securityContext) : null;
		if (gridLayout == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ui field with id  "
					+ updateGridLayout.getId());
		}
		updateGridLayout.setGridLayout(gridLayout);
		service.validate(updateGridLayout, securityContext);

		return service.updateGridLayout(updateGridLayout, securityContext);

	}

	
	
	@Operation(summary = "createGridLayout", description = "Creates GridLayout ")
	@PostMapping("createGridLayout")
	public GridLayout createGridLayout(
			@RequestBody
			GridLayoutCreate createGridLayout, @RequestAttribute SecurityContextBase securityContext) {
		service.validate(createGridLayout, securityContext);
		return service.createGridLayout(createGridLayout, securityContext);

	}


}
