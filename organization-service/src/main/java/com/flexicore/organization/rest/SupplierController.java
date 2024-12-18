package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.organization.model.Supplier_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.organization.model.Supplier;
import com.flexicore.organization.request.SupplierCreate;
import com.flexicore.organization.request.SupplierFiltering;
import com.flexicore.organization.request.SupplierUpdate;
import com.flexicore.organization.service.SupplierService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
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
@RequestMapping("/plugins/Supplier")
@Tag(name = "Supplier")
@Extension
@RestController
public class SupplierController implements Plugin {


	@Autowired
	private SupplierService service;


	@Operation(summary = "getAllSuppliers", description = "Lists all Suppliers")
	@IOperation(Name = "getAllSuppliers", Description = "Lists all Suppliers")
	@PostMapping("/getAllSuppliers")
	public PaginationResponse<Supplier> listAllSuppliers(

			@RequestBody SupplierFiltering filtering,
			@RequestAttribute SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.listAllSuppliers(securityContext, filtering);
	}



	@PostMapping("/createSupplier")
	@Operation(summary = "createSupplier", description = "Creates Supplier")
	@IOperation(Name = "createSupplier", Description = "Creates Supplier")
	public Supplier createSupplier(

			@RequestBody SupplierCreate creationContainer,
			@RequestAttribute SecurityContext securityContext) {

		service.validateCreate(creationContainer, securityContext);

		return service.createSupplier(creationContainer, securityContext);
	}


	@PutMapping("/updateSupplier")
	@Operation(summary = "updateSupplier", description = "Updates Supplier")
	@IOperation(Name = "updateSupplier", Description = "Updates Supplier")
	public Supplier updateSupplier(

			@RequestBody SupplierUpdate updateContainer,
			@RequestAttribute SecurityContext securityContext) {
		Supplier Supplier = service.getByIdOrNull(updateContainer.getId(),
				Supplier.class,  securityContext);
		if (Supplier == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Supplier with id "
					+ updateContainer.getId());
		}
		updateContainer.setSupplier(Supplier);
		service.validateUpdate(updateContainer, securityContext);

		return service.updateSupplier(updateContainer, securityContext);
	}


}
