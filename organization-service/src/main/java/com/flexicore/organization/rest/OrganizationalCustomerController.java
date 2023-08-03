package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.organization.model.OrganizationalCustomer_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.organization.model.OrganizationalCustomer;
import com.flexicore.organization.request.OrganizationalCustomerCreate;
import com.flexicore.organization.request.OrganizationalCustomerFiltering;
import com.flexicore.organization.request.OrganizationalCustomerUpdate;
import com.flexicore.organization.service.OrganizationalCustomerService;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;



@OperationsInside
@RequestMapping("/plugins/OrganizationalCustomer")
@Tag(name = "OrganizationalCustomer")
@Extension
@RestController
public class OrganizationalCustomerController implements Plugin {


	@Autowired
	private OrganizationalCustomerService service;


	@Operation(summary = "getAllOrganizationalCustomers", description = "Lists all OrganizationalCustomers")
	@IOperation(Name = "getAllOrganizationalCustomers", Description = "Lists all OrganizationalCustomers")
	@PostMapping("/getAllOrganizationalCustomers")
	public PaginationResponse<OrganizationalCustomer> getAllOrganizationalCustomers(

			@RequestBody OrganizationalCustomerFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllOrganizationalCustomers(securityContext, filtering);
	}


	@PostMapping("/createOrganizationalCustomer")
	@Operation(summary = "createOrganizationalCustomer", description = "Creates OrganizationalCustomer")
	@IOperation(Name = "createOrganizationalCustomer", Description = "Creates OrganizationalCustomer")
	public OrganizationalCustomer createOrganizationalCustomer(

			@RequestBody OrganizationalCustomerCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createOrganizationalCustomer(creationContainer, securityContext);
	}


	@PutMapping("/updateOrganizationalCustomer")
	@Operation(summary = "updateOrganizationalCustomer", description = "Updates OrganizationalCustomer")
	@IOperation(Name = "updateOrganizationalCustomer", Description = "Updates OrganizationalCustomer")
	public OrganizationalCustomer updateOrganizationalCustomer(

			@RequestBody OrganizationalCustomerUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		OrganizationalCustomer organizationalCustomer = service.getByIdOrNull(updateContainer.getId(),
				OrganizationalCustomer.class, OrganizationalCustomer_.security, securityContext);
		if (organizationalCustomer == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no OrganizationalCustomer with id "
					+ updateContainer.getId());
		}
		updateContainer.setOrganizationalCustomer(organizationalCustomer);

		return service.updateOrganizationalCustomer(updateContainer, securityContext);
	}
}