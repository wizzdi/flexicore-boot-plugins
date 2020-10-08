package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.request.CellContentCreate;
import com.flexicore.ui.dashboard.request.CellContentFiltering;
import com.flexicore.ui.dashboard.request.CellContentUpdate;
import com.flexicore.ui.dashboard.service.CellContentService;
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
@Path("plugins/CellContent")
@Tag(name = "CellContent")
@Tag(name = "Presets")
@Extension
@Component
public class CellContentRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private CellContentService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllCellContent", description = "returns all CellContent")
	@Path("getAllCellContent")
	public PaginationResponse<CellContent> getAllCellContent(
			@HeaderParam("authenticationKey") String authenticationKey,
			CellContentFiltering cellContentFiltering,
			@Context SecurityContext securityContext) {
		service.validate(cellContentFiltering, securityContext);
		return service.getAllCellContent(cellContentFiltering, securityContext);

	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateCellContent", description = "Updates Dashbaord")
	@Path("updateCellContent")
	public CellContent updateCellContent(
			@HeaderParam("authenticationKey") String authenticationKey,
			CellContentUpdate updateCellContent, @Context SecurityContext securityContext) {
		CellContent cellContent = updateCellContent.getId() != null ? service.getByIdOrNull(
				updateCellContent.getId(), CellContent.class, null, securityContext) : null;
		if (cellContent == null) {
			throw new BadRequestException("no ui field with id  "
					+ updateCellContent.getId());
		}
		updateCellContent.setCellContent(cellContent);
		service.validate(updateCellContent, securityContext);

		return service.updateCellContent(updateCellContent, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createCellContent", description = "Creates CellContent ")
	@Path("createCellContent")
	public CellContent createCellContent(
			@HeaderParam("authenticationKey") String authenticationKey,
			CellContentCreate createCellContent, @Context SecurityContext securityContext) {
		service.validate(createCellContent, securityContext);
		return service.createCellContent(createCellContent, securityContext);

	}


}
