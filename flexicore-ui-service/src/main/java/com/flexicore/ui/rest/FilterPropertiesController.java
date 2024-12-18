package com.flexicore.ui.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.ui.model.FilterProperties_;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.model.FilterProperties;
import com.flexicore.ui.request.FilterPropertiesCreate;
import com.flexicore.ui.request.FilterPropertiesFiltering;
import com.flexicore.ui.request.FilterPropertiesUpdate;
import com.flexicore.ui.service.FilterPropertiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




/**
 * Created by Asaf on 04/06/2017.
 */


@OperationsInside
@RestController
@RequestMapping("plugins/FilterProperties")
@Tag(name = "FilterProperties")
@Tag(name = "Presets")
@Extension
@Component
public class FilterPropertiesController implements Plugin {

	
	@Autowired
	private FilterPropertiesService service;


	@Operation(summary = "getAllFilterProperties", description = "returns all FilterProperties")
	@PostMapping("getAllFilterProperties")
	public PaginationResponse<FilterProperties> getAllFilterProperties(
			 @RequestBody
			FilterPropertiesFiltering filterPropertiesFiltering,
			@RequestAttribute SecurityContext securityContext) {
		service.validate(filterPropertiesFiltering,securityContext);
		return service.getAllFilterProperties(filterPropertiesFiltering, securityContext);

	}


	@Operation(summary = "updateFilterProperties", description = "Updates FilterProperties")
	@PutMapping("updateFilterProperties")
	public FilterProperties updateFilterProperties(
			 @RequestBody
			FilterPropertiesUpdate updateFilterProperties, @RequestAttribute SecurityContext securityContext) {
		FilterProperties filterProperties = updateFilterProperties.getId() != null ? service.getByIdOrNull(updateFilterProperties.getId(), FilterProperties.class,securityContext) : null;
		if (filterProperties == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no FilterProperties id  " + updateFilterProperties.getId());
		}
		updateFilterProperties.setFilterProperties(filterProperties);
		service.validate(updateFilterProperties, securityContext);

		return service.updateFilterProperties(updateFilterProperties, securityContext);

	}


	@Operation(summary = "createFilterProperties", description = "Creates FilterProperties ")
	@PostMapping("createFilterProperties")
	public FilterProperties createFilterProperties(
			 @RequestBody
			FilterPropertiesCreate createFilterProperties, @RequestAttribute SecurityContext securityContext) {
		service.validateCreate(createFilterProperties, securityContext);
		return service.createFilterProperties(createFilterProperties, securityContext);

	}


}
