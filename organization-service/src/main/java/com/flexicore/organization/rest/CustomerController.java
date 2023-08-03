package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.organization.model.Customer_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.organization.model.Customer;
import com.flexicore.organization.request.CustomerCreate;
import com.flexicore.organization.request.CustomerFiltering;
import com.flexicore.organization.request.CustomerUpdate;
import com.flexicore.organization.service.CustomerService;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;



@OperationsInside

@RequestMapping("/plugins/Customer")

@Tag(name = "Customer")
@Extension
@RestController
public class CustomerController implements Plugin {


	@Autowired
	private CustomerService service;


	@Operation(summary = "getAllCustomers", description = "Lists all Customers")
	@IOperation(Name = "getAllCustomers", Description = "Lists all Customers")
	@PostMapping("/getAllCustomers")
	public PaginationResponse<Customer> getAllCustomers(

			@RequestBody CustomerFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllCustomers(securityContext, filtering);
	}


	@PostMapping("/createCustomer")
	@Operation(summary = "createCustomer", description = "Creates Customer")
	@IOperation(Name = "createCustomer", Description = "Creates Customer")
	public Customer createCustomer(

			@RequestBody CustomerCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createCustomer(creationContainer, securityContext);
	}

	@PutMapping("/updateCustomer")
	@Operation(summary = "updateCustomer", description = "Updates Customer")
	@IOperation(Name = "updateCustomer", Description = "Updates Customer")
	public Customer updateCustomer(

			@RequestBody CustomerUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		Customer customer = service.getByIdOrNull(updateContainer.getId(),
				Customer.class, Customer_.security, securityContext);
		if (customer == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Customer with id "
					+ updateContainer.getId());
		}
		updateContainer.setCustomer(customer);

		return service.updateCustomer(updateContainer, securityContext);
	}
}