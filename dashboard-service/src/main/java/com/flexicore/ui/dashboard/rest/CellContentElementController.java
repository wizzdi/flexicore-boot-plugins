package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.dashboard.model.CellContentElement;
import com.flexicore.ui.dashboard.request.CellContentElementCreate;
import com.flexicore.ui.dashboard.request.CellContentElementFilter;
import com.flexicore.ui.dashboard.request.CellContentElementUpdate;
import com.flexicore.ui.dashboard.service.CellContentElementService;
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
@RequestMapping("plugins/CellContentElement")
@Tag(name = "CellContentElement")
@Tag(name = "Presets")
@Extension
@Component
public class CellContentElementController implements Plugin {

	
	@Autowired
	private CellContentElementService service;

	
	
	@Operation(summary = "getAllCellContentElement", description = "returns all CellContentElement")
	@PostMapping("getAllCellContentElement")
	public PaginationResponse<CellContentElement> getAllCellContentElement(
			 @RequestBody
			CellContentElementFilter cellContentElementFilter,
			@RequestAttribute SecurityContext securityContext) {
		service.validate(cellContentElementFilter, securityContext);
		return service.getAllCellContentElement(cellContentElementFilter, securityContext);

	}

	
	
	@Operation(summary = "updateCellContentElement", description = "Updates Dashbaord")
	@PutMapping("updateCellContentElement")
	public CellContentElement updateCellContentElement(
			@RequestBody
			CellContentElementUpdate updateCellContentElement, @RequestAttribute SecurityContext securityContext) {
		CellContentElement cellContentElement = updateCellContentElement.getId() != null ? service.getByIdOrNull(
				updateCellContentElement.getId(), CellContentElement.class,  securityContext) : null;
		if (cellContentElement == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ui field with id  "
					+ updateCellContentElement.getId());
		}
		updateCellContentElement.setCellContentElement(cellContentElement);
		service.validate(updateCellContentElement, securityContext);

		return service.updateCellContentElement(updateCellContentElement, securityContext);

	}

	
	
	@Operation(summary = "createCellContentElement", description = "Creates CellContentElement ")
	@PostMapping("createCellContentElement")
	public CellContentElement createCellContentElement(
			@RequestBody
			CellContentElementCreate createCellContentElement, @RequestAttribute SecurityContext securityContext) {
		service.validate(createCellContentElement, securityContext);
		return service.createCellContentElement(createCellContentElement, securityContext);

	}


}
