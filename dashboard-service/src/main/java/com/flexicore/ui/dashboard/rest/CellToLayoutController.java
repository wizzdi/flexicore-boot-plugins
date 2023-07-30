package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.dashboard.model.CellToLayout;
import com.flexicore.ui.dashboard.request.CellToLayoutCreate;
import com.flexicore.ui.dashboard.request.CellToLayoutFilter;
import com.flexicore.ui.dashboard.request.CellToLayoutUpdate;
import com.flexicore.ui.dashboard.service.CellToLayoutService;
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
@RequestMapping("plugins/CellToLayout")
@Tag(name = "CellToLayout")
@Tag(name = "Presets")
@Extension
@Component
public class CellToLayoutController implements Plugin {

	
	@Autowired
	private CellToLayoutService service;

	
	
	@Operation(summary = "getAllCellToLayout", description = "returns all CellToLayout")
	@PostMapping("getAllCellToLayout")
	public PaginationResponse<CellToLayout> getAllCellToLayout(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			CellToLayoutFilter cellToLayoutFilter,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(cellToLayoutFilter,securityContext);
		return service.getAllCellToLayout(cellToLayoutFilter, securityContext);

	}

	
	
	@Operation(summary = "updateCellToLayout", description = "Updates Dashbaord")
	@PutMapping("updateCellToLayout")
	public CellToLayout updateCellToLayout(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			CellToLayoutUpdate updateCellToLayout, @RequestAttribute SecurityContextBase securityContext) {
		CellToLayout cellToLayout = updateCellToLayout.getId() != null ? service.getByIdOrNull(
				updateCellToLayout.getId(), CellToLayout.class, SecuredBasic_.security, securityContext) : null;
		if (cellToLayout == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ui field with id  "
					+ updateCellToLayout.getId());
		}
		updateCellToLayout.setCellToLayout(cellToLayout);
		service.validate(updateCellToLayout, securityContext);

		return service.updateCellToLayout(updateCellToLayout, securityContext);

	}

	
	
	@Operation(summary = "createCellToLayout", description = "Creates CellToLayout ")
	@PostMapping("createCellToLayout")
	public CellToLayout createCellToLayout(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			CellToLayoutCreate createCellToLayout, @RequestAttribute SecurityContextBase securityContext) {
		service.validate(createCellToLayout, securityContext);
		return service.createCellToLayout(createCellToLayout, securityContext);

	}


}
