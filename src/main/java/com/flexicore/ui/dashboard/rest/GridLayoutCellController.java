package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.dashboard.model.GridLayoutCell;
import com.flexicore.ui.dashboard.request.GridLayoutCellCreate;
import com.flexicore.ui.dashboard.request.GridLayoutCellFilter;
import com.flexicore.ui.dashboard.request.GridLayoutCellUpdate;
import com.flexicore.ui.dashboard.service.GridLayoutCellService;
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
@RequestMapping("plugins/GridLayoutCell")
@Tag(name = "GridLayoutCell")
@Tag(name = "Presets")
@Extension
@Component
public class GridLayoutCellController implements Plugin {

	
	@Autowired
	private GridLayoutCellService service;

	
	
	@Operation(summary = "getAllGridLayoutCell", description = "returns all GridLayoutCell")
	@PostMapping("getAllGridLayoutCell")
	public PaginationResponse<GridLayoutCell> getAllGridLayoutCell(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			GridLayoutCellFilter gridLayoutCellFilter,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(gridLayoutCellFilter, securityContext);
		return service.getAllGridLayoutCell(gridLayoutCellFilter, securityContext);

	}

	
	
	@Operation(summary = "updateGridLayoutCell", description = "Updates Dashbaord")
	@PutMapping("updateGridLayoutCell")
	public GridLayoutCell updateGridLayoutCell(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			GridLayoutCellUpdate updateGridLayoutCell, @RequestAttribute SecurityContextBase securityContext) {
		GridLayoutCell gridLayoutCell = updateGridLayoutCell.getId() != null ? service.getByIdOrNull(
				updateGridLayoutCell.getId(), GridLayoutCell.class, SecuredBasic_.security, securityContext) : null;
		if (gridLayoutCell == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ui field with id  "
					+ updateGridLayoutCell.getId());
		}
		updateGridLayoutCell.setGridLayoutCell(gridLayoutCell);
		service.validate(updateGridLayoutCell, securityContext);

		return service.updateGridLayoutCell(updateGridLayoutCell, securityContext);

	}

	
	
	@Operation(summary = "createGridLayoutCell", description = "Creates GridLayoutCell ")
	@PostMapping("createGridLayoutCell")
	public GridLayoutCell createGridLayoutCell(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			GridLayoutCellCreate createGridLayoutCell, @RequestAttribute SecurityContextBase securityContext) {
		service.validate(createGridLayoutCell, securityContext);
		return service.createGridLayoutCell(createGridLayoutCell, securityContext);

	}


}
