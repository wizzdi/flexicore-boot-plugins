package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.request.CellContentCreate;
import com.flexicore.ui.dashboard.request.CellContentFiltering;
import com.flexicore.ui.dashboard.request.CellContentUpdate;
import com.flexicore.ui.dashboard.service.CellContentService;
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
@RequestMapping("plugins/CellContent")
@Tag(name = "CellContent")
@Tag(name = "Presets")
@Extension
@Component
public class CellContentController implements Plugin {

	
	@Autowired
	private CellContentService service;

	
	
	@Operation(summary = "getAllCellContent", description = "returns all CellContent")
	@PostMapping("getAllCellContent")
	public PaginationResponse<CellContent> getAllCellContent(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			CellContentFiltering cellContentFiltering,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(cellContentFiltering, securityContext);
		return service.getAllCellContent(cellContentFiltering, securityContext);

	}

	
	
	@Operation(summary = "updateCellContent", description = "Updates Dashbaord")
	@PutMapping("updateCellContent")
	public CellContent updateCellContent(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			CellContentUpdate updateCellContent, @RequestAttribute SecurityContextBase securityContext) {
		CellContent cellContent = updateCellContent.getId() != null ? service.getByIdOrNull(
				updateCellContent.getId(), CellContent.class, SecuredBasic_.security, securityContext) : null;
		if (cellContent == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ui field with id  "
					+ updateCellContent.getId());
		}
		updateCellContent.setCellContent(cellContent);
		service.validate(updateCellContent, securityContext);

		return service.updateCellContent(updateCellContent, securityContext);

	}

	
	
	@Operation(summary = "createCellContent", description = "Creates CellContent ")
	@PostMapping("createCellContent")
	public CellContent createCellContent(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody
			CellContentCreate createCellContent, @RequestAttribute SecurityContextBase securityContext) {
		service.validate(createCellContent, securityContext);
		return service.createCellContent(createCellContent, securityContext);

	}


}
