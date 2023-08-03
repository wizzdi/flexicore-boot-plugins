package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;

import com.flexicore.organization.model.Site_;
import com.wizzdi.flexicore.security.response.PaginationResponse;


import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.organization.model.Site;
import com.flexicore.organization.request.SiteCreate;
import com.flexicore.organization.request.SiteFiltering;
import com.flexicore.organization.request.SiteUpdate;
import com.flexicore.organization.service.SiteService;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import org.pf4j.Extension;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside
@RequestMapping("/plugins/Site")
@Tag(name = "Site")
@Extension
@RestController
public class SiteController implements Plugin {


	@Autowired
	private SiteService service;


	@Operation(summary = "getAllSites", description = "Lists all Sites")
	@IOperation(Name = "getAllSites", Description = "Lists all Sites")
	@PostMapping("/getAllSites")
	public PaginationResponse<Site> getAllSites(

			@RequestBody SiteFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllSites(securityContext, filtering);
	}


	@PostMapping("/createSite")
	@Operation(summary = "createSite", description = "Creates Site")
	@IOperation(Name = "createSite", Description = "Creates Site")
	public Site createSite(

			@RequestBody SiteCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createSite(creationContainer, securityContext);
	}


	@PutMapping("/updateSite")
	@Operation(summary = "updateSite", description = "Updates Site")
	@IOperation(Name = "updateSite", Description = "Updates Site")
	public Site updateSite(

			@RequestBody SiteUpdate updateContainer, @RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		Site site = service.getByIdOrNull(updateContainer.getId(), Site.class, Site_.security, securityContext);
		if (site == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Site with id "
					+ updateContainer.getId());
		}
		updateContainer.setSite(site);

		return service.updateSite(updateContainer, securityContext);
	}
}