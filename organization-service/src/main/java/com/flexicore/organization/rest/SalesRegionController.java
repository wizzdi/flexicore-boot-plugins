package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;

import com.flexicore.organization.model.SalesRegion_;
import com.wizzdi.flexicore.security.response.PaginationResponse;


import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.organization.model.SalesRegion;
import com.flexicore.organization.request.SalesRegionCreate;
import com.flexicore.organization.request.SalesRegionFiltering;
import com.flexicore.organization.request.SalesRegionUpdate;
import com.flexicore.organization.service.SalesRegionService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import org.pf4j.Extension;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside
@RequestMapping("/plugins/SalesRegion")
@Tag(name = "SalesRegion")
@Extension
@RestController
public class SalesRegionController implements Plugin {


	@Autowired
	private SalesRegionService service;


	@Operation(summary = "getAllSalesRegions", description = "Lists all SalesRegions")
	@IOperation(Name = "getAllSalesRegions", Description = "Lists all SalesRegions")
	@PostMapping("/getAllSalesRegions")
	public PaginationResponse<SalesRegion> getAllSalesRegions(

			@RequestBody SalesRegionFiltering filtering,
			@RequestAttribute SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllSalesRegions(securityContext, filtering);
	}


	@PostMapping("/createSalesRegion")
	@Operation(summary = "createSalesRegion", description = "Creates SalesRegion")
	@IOperation(Name = "createSalesRegion", Description = "Creates SalesRegion")
	public SalesRegion createSalesRegion(

			@RequestBody SalesRegionCreate creationContainer,
			@RequestAttribute SecurityContext securityContext) {

		return service.createSalesRegion(creationContainer, securityContext);
	}


	@PutMapping("/updateSalesRegion")
	@Operation(summary = "updateSalesRegion", description = "Updates SalesRegion")
	@IOperation(Name = "updateSalesRegion", Description = "Updates SalesRegion")
	public SalesRegion updateSalesRegion(

			@RequestBody SalesRegionUpdate updateContainer,
			@RequestAttribute SecurityContext securityContext) {
		SalesRegion salesRegion = service.getByIdOrNull(
				updateContainer.getId(), SalesRegion.class, 
				securityContext);
		if (salesRegion == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no SalesRegion with id "
					+ updateContainer.getId());
		}
		updateContainer.setSalesRegion(salesRegion);

		return service.updateSalesRegion(updateContainer, securityContext);
	}

}
