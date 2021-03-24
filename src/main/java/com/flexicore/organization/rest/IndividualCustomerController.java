package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.organization.model.IndividualCustomer;
import com.flexicore.organization.request.IndividualCustomerCreate;
import com.flexicore.organization.request.IndividualCustomerFiltering;
import com.flexicore.organization.request.IndividualCustomerUpdate;
import com.flexicore.organization.service.IndividualCustomerService;
import com.flexicore.security.SecurityContextBase;
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


@OperationsInside

@RequestMapping("plugins/IndividualCustomer")

@Tag(name = "IndividualCustomer")
@Extension
@Component
public class IndividualCustomerController implements Plugin {


	@Autowired
	private IndividualCustomerService service;


	@Operation(summary = "getAllIndividualCustomers", description = "Lists all IndividualCustomers")
	@IOperation(Name = "getAllIndividualCustomers", Description = "Lists all IndividualCustomers")
	@PostMapping("getAllIndividualCustomers")
	public PaginationResponse<IndividualCustomer> getAllIndividualCustomers(

			@RequestBody IndividualCustomerFiltering filtering, @RequestAttribute SecurityContextBase securityContextBase) {
		service.validateFiltering(filtering, securityContextBase);
		return service.getAllIndividualCustomers(securityContextBase, filtering);
	}


	@PostMapping("/createIndividualCustomer")
	@Operation(summary = "createIndividualCustomer", description = "Creates IndividualCustomer")
	@IOperation(Name = "createIndividualCustomer", Description = "Creates IndividualCustomer")
	public IndividualCustomer createIndividualCustomer(

			@RequestBody IndividualCustomerCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContextBase) {
		service.validate(creationContainer, securityContextBase);

		return service.createIndividualCustomer(creationContainer, securityContextBase);
	}


	@PutMapping("/updateIndividualCustomer")
	@Operation(summary = "updateIndividualCustomer", description = "Updates IndividualCustomer")
	@IOperation(Name = "updateIndividualCustomer", Description = "Updates IndividualCustomer")
	public IndividualCustomer updateIndividualCustomer(

			@RequestBody IndividualCustomerUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContextBase) {
		service.validate(updateContainer, securityContextBase);
		IndividualCustomer individualCustomer = service.getByIdOrNull(updateContainer.getId(),
				IndividualCustomer.class, null, securityContextBase);
		if (individualCustomer == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no IndividualCustomer with id "
					+ updateContainer.getId());
		}
		updateContainer.setIndividualCustomer(individualCustomer);

		return service.updateIndividualCustomer(updateContainer, securityContextBase);
	}
}