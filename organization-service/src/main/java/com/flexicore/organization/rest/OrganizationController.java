package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.model.Organization_;
import com.flexicore.organization.request.OrganizationCreate;
import com.flexicore.organization.request.OrganizationFiltering;
import com.flexicore.organization.request.OrganizationUpdate;
import com.flexicore.organization.service.OrganizationService;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside
@RequestMapping("/plugins/Organization")
@Tag(name = "Organization")
@Extension
@RestController
public class OrganizationController implements Plugin {


	@Autowired
	private OrganizationService service;


	@Operation(summary = "getAllOrganizations", description = "Lists all Organizations")
	@IOperation(Name = "getAllOrganizations", Description = "Lists all Organizations")
	@PostMapping("/getAllOrganizations")
	public PaginationResponse<Organization> getAllOrganizations(

			@RequestBody OrganizationFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllOrganizations(securityContext, filtering);
	}


	@PostMapping("/createOrganization")
	@Operation(summary = "createOrganization", description = "Creates Organization")
	@IOperation(Name = "createOrganization", Description = "Creates Organization")
	public Organization createOrganization(

			@RequestBody OrganizationCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createOrganization(creationContainer, securityContext);
	}


	@PutMapping("/updateOrganization")
	@Operation(summary = "updateOrganization", description = "Updates Organization")
	@IOperation(Name = "updateOrganization", Description = "Updates Organization")
	public Organization updateOrganization(

			@RequestBody OrganizationUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		Organization organization = service.getByIdOrNull(updateContainer.getId(),
				Organization.class, Organization_.security, securityContext);
		if (organization == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Organization with id "
					+ updateContainer.getId());
		}
		updateContainer.setOrganization(organization);

		return service.updateOrganization(updateContainer, securityContext);
	}
}