package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.ui.dashboard.model.GraphTemplate;
import com.flexicore.ui.dashboard.request.GraphTemplateCreate;
import com.flexicore.ui.dashboard.request.GraphTemplateFiltering;
import com.flexicore.ui.dashboard.request.GraphTemplateUpdate;
import com.flexicore.ui.dashboard.service.GraphTemplateService;
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
@Path("plugins/GraphTemplate")
@Tag(name = "GraphTemplate")
@Tag(name = "Presets")
@Extension
@Component
public class GraphTemplateRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private GraphTemplateService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllGraphTemplate", description = "returns all GraphTemplate")
	@Path("getAllGraphTemplate")
	public PaginationResponse<GraphTemplate> getAllGraphTemplate(
			@HeaderParam("authenticationKey") String authenticationKey,
			GraphTemplateFiltering graphTemplateFiltering,
			@Context SecurityContext securityContext) {
		service.validate(graphTemplateFiltering, securityContext);
		return service.getAllGraphTemplate(graphTemplateFiltering, securityContext);

	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateGraphTemplate", description = "Updates Dashbaord")
	@Path("updateGraphTemplate")
	public GraphTemplate updateGraphTemplate(
			@HeaderParam("authenticationKey") String authenticationKey,
			GraphTemplateUpdate updateGraphTemplate, @Context SecurityContext securityContext) {
		GraphTemplate graphTemplate = updateGraphTemplate.getId() != null ? service.getByIdOrNull(
				updateGraphTemplate.getId(), GraphTemplate.class, null, securityContext) : null;
		if (graphTemplate == null) {
			throw new BadRequestException("no ui field with id  "
					+ updateGraphTemplate.getId());
		}
		updateGraphTemplate.setGraphTemplate(graphTemplate);
		service.validate(updateGraphTemplate, securityContext);

		return service.updateGraphTemplate(updateGraphTemplate, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createGraphTemplate", description = "Creates GraphTemplate ")
	@Path("createGraphTemplate")
	public GraphTemplate createGraphTemplate(
			@HeaderParam("authenticationKey") String authenticationKey,
			GraphTemplateCreate createGraphTemplate, @Context SecurityContext securityContext) {
		service.validate(createGraphTemplate, securityContext);
		return service.createGraphTemplate(createGraphTemplate, securityContext);

	}


}
