package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.organization.model.SalesPerson_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.organization.model.SalesPerson;
import com.flexicore.organization.request.SalesPersonCreate;
import com.flexicore.organization.request.SalesPersonFiltering;
import com.flexicore.organization.request.SalesPersonUpdate;
import com.flexicore.organization.service.SalesPersonService;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import org.pf4j.Extension;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside
@RequestMapping("/plugins/SalesPerson")
@Tag(name = "SalesPerson")
@Extension
@RestController
public class SalesPersonController implements Plugin {


	@Autowired
	private SalesPersonService service;


	@Operation(summary = "listAllSalesPersons", description = "Lists all SalesPersons")
	@IOperation(Name = "listAllSalesPersons", Description = "Lists all SalesPersons")
	@PostMapping("/getAllSalesPeople")
	public PaginationResponse<SalesPerson> getAllSalesPeople(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody SalesPersonFiltering filtering,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllSalesPeople(securityContext, filtering);
	}


	@PostMapping("/createSalesPerson")
	@Operation(summary = "createSalesPerson", description = "Creates SalesPerson")
	@IOperation(Name = "createSalesPerson", Description = "Creates SalesPerson")
	public SalesPerson createSalesPerson(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody SalesPersonCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {

		return service.createSalesPerson(creationContainer, securityContext);
	}


	@PutMapping("/updateSalesPerson")
	@Operation(summary = "updateSalesPerson", description = "Updates SalesPerson")
	@IOperation(Name = "updateSalesPerson", Description = "Updates SalesPerson")
	public SalesPerson updateSalesPerson(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody SalesPersonUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		SalesPerson salesPerson = service.getByIdOrNull(
				updateContainer.getId(), SalesPerson.class, SalesPerson_.security,
				securityContext);
		if (salesPerson == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no SalesPerson with id "
					+ updateContainer.getId());
		}
		updateContainer.setSalesPerson(salesPerson);

		return service.updateSalesPerson(updateContainer, securityContext);
	}


}