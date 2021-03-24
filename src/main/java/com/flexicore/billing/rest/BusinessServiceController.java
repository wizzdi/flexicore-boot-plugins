package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.model.BusinessService_;
import com.flexicore.billing.request.BusinessServiceCreate;
import com.flexicore.billing.request.BusinessServiceFiltering;
import com.flexicore.billing.request.BusinessServiceUpdate;
import com.flexicore.billing.service.BusinessServiceService;
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

@RequestMapping("/plugins/BusinessService")

@Tag(name = "BusinessService")
@Extension
@RestController
public class BusinessServiceController implements Plugin {

		@Autowired
	private BusinessServiceService service;



	@Operation(summary = "getAllBusinessServices", description = "Lists all BusinessServices")
	@IOperation(Name = "getAllBusinessServices", Description = "Lists all BusinessServices")
	@PostMapping("/getAllBusinessServices")
	public PaginationResponse<BusinessService> getAllBusinessServices(

			@RequestBody BusinessServiceFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllBusinessServices(securityContext, filtering);
	}



	@PostMapping("/createBusinessService")
	@Operation(summary = "createBusinessService", description = "Creates BusinessService")
	@IOperation(Name = "createBusinessService", Description = "Creates BusinessService")
	public BusinessService createBusinessService(

			@RequestBody BusinessServiceCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createBusinessService(creationContainer, securityContext);
	}



	@PutMapping("/updateBusinessService")
	@Operation(summary = "updateBusinessService", description = "Updates BusinessService")
	@IOperation(Name = "updateBusinessService", Description = "Updates BusinessService")
	public BusinessService updateBusinessService(

			@RequestBody BusinessServiceUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		BusinessService businessService = service.getByIdOrNull(updateContainer.getId(),
				BusinessService.class, BusinessService_.security, securityContext);
		if (businessService == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no BusinessService with id "
					+ updateContainer.getId());
		}
		updateContainer.setBusinessService(businessService);

		return service.updateBusinessService(updateContainer, securityContext);
	}
}