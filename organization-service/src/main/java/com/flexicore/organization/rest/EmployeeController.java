package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;

import com.flexicore.organization.model.Employee_;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.organization.model.Employee;
import com.flexicore.organization.request.EmployeeCreate;
import com.flexicore.organization.request.EmployeeFiltering;
import com.flexicore.organization.request.EmployeeUpdate;
import com.flexicore.organization.service.EmployeeService;
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

@RequestMapping("/plugins/Employee")

@Tag(name = "Employee")
@Extension
@RestController
public class EmployeeController implements Plugin {


	@Autowired
	private EmployeeService service;


	@Operation(summary = "listAllEmployees", description = "Lists all Employees")
	@IOperation(Name = "listAllEmployees", Description = "Lists all Employees")
	@PostMapping("/listAllEmployees")
	public PaginationResponse<Employee> listAllEmployees(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody EmployeeFiltering filtering,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.listAllEmployees(securityContext, filtering);
	}


	@PostMapping("/createEmployee")
	@Operation(summary = "createEmployee", description = "Creates Employee")
	@IOperation(Name = "createEmployee", Description = "Creates Employee")
	public Employee createEmployee(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody EmployeeCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer,securityContext);
		return service.createEmployee(creationContainer, securityContext);
	}


	@PutMapping("/updateEmployee")
	@Operation(summary = "updateEmployee", description = "Updates Employee")
	@IOperation(Name = "updateEmployee", Description = "Updates Employee")
	public Employee updateEmployee(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody EmployeeUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		Employee employee = service.getByIdOrNull(updateContainer.getId(),
				Employee.class, Employee_.security, securityContext);
		if (employee == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Employee with id "
					+ updateContainer.getId());
		}
		updateContainer.setEmployee(employee);

		return service.updateEmployee(updateContainer, securityContext);
	}

}