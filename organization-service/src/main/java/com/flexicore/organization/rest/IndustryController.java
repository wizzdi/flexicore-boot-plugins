package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.organization.model.Industry_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.organization.model.Industry;
import com.flexicore.organization.request.IndustryCreate;
import com.flexicore.organization.request.IndustryFiltering;
import com.flexicore.organization.request.IndustryUpdate;
import com.flexicore.organization.service.IndustryService;
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
@RequestMapping("/plugins/Industry")
@Tag(name = "Industry")
@Extension
@RestController
public class IndustryController implements Plugin {


	@Autowired
	private IndustryService service;


	@Operation(summary = "getAllIndustries", description = "Lists all Industries")
	@IOperation(Name = "getAllIndustries", Description = "Lists all Industries")
	@PostMapping("/getAllIndustries")
	public PaginationResponse<Industry> getAllIndustries(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody IndustryFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllIndustries(securityContext, filtering);
	}


	@PostMapping("/createIndustry")
	@Operation(summary = "createIndustry", description = "Creates Industry")
	@IOperation(Name = "createIndustry", Description = "Creates Industry")
	public Industry createIndustry(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody IndustryCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createIndustry(creationContainer, securityContext);
	}


	@PutMapping("/updateIndustry")
	@Operation(summary = "updateIndustry", description = "Updates Industry")
	@IOperation(Name = "updateIndustry", Description = "Updates Industry")
	public Industry updateIndustry(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody IndustryUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		Industry industry = service.getByIdOrNull(updateContainer.getId(),
				Industry.class, Industry_.security, securityContext);
		if (industry == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Industry with id "
					+ updateContainer.getId());
		}
		updateContainer.setIndustry(industry);

		return service.updateIndustry(updateContainer, securityContext);
	}
}