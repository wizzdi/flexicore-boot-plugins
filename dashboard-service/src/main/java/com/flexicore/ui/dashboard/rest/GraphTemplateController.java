package com.flexicore.ui.dashboard.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.dashboard.model.GraphTemplate;
import com.flexicore.ui.dashboard.request.GraphTemplateCreate;
import com.flexicore.ui.dashboard.request.GraphTemplateFilter;
import com.flexicore.ui.dashboard.request.GraphTemplateUpdate;
import com.flexicore.ui.dashboard.service.GraphTemplateService;
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
@RequestMapping("plugins/GraphTemplate")
@Tag(name = "GraphTemplate")
@Tag(name = "Presets")
@Extension
@Component
public class GraphTemplateController implements Plugin {

	
	@Autowired
	private GraphTemplateService service;

	
	
	@Operation(summary = "getAllGraphTemplate", description = "returns all GraphTemplate")
	@PostMapping("getAllGraphTemplate")
	public PaginationResponse<GraphTemplate> getAllGraphTemplate(
			@RequestBody
			GraphTemplateFilter graphTemplateFilter,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(graphTemplateFilter, securityContext);
		return service.getAllGraphTemplate(graphTemplateFilter, securityContext);

	}

	
	
	@Operation(summary = "updateGraphTemplate", description = "Updates Dashbaord")
	@PutMapping("updateGraphTemplate")
	public GraphTemplate updateGraphTemplate(
			@RequestBody
			GraphTemplateUpdate updateGraphTemplate, @RequestAttribute SecurityContextBase securityContext) {
		GraphTemplate graphTemplate = updateGraphTemplate.getId() != null ? service.getByIdOrNull(
				updateGraphTemplate.getId(), GraphTemplate.class, SecuredBasic_.security, securityContext) : null;
		if (graphTemplate == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ui field with id  "
					+ updateGraphTemplate.getId());
		}
		updateGraphTemplate.setGraphTemplate(graphTemplate);
		service.validate(updateGraphTemplate, securityContext);

		return service.updateGraphTemplate(updateGraphTemplate, securityContext);

	}

	
	
	@Operation(summary = "createGraphTemplate", description = "Creates GraphTemplate ")
	@PostMapping("createGraphTemplate")
	public GraphTemplate createGraphTemplate(
			@RequestBody
			GraphTemplateCreate createGraphTemplate, @RequestAttribute SecurityContextBase securityContext) {
		service.validate(createGraphTemplate, securityContext);
		return service.createGraphTemplate(createGraphTemplate, securityContext);

	}


}
