package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.organization.model.Branch;
import com.flexicore.organization.model.Branch_;
import com.flexicore.organization.request.BranchCreate;
import com.flexicore.organization.request.BranchFiltering;
import com.flexicore.organization.request.BranchUpdate;
import com.flexicore.organization.service.BranchService;
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
@RequestMapping("/plugins/Branch")
@Tag(name = "Branch")
@Extension
@RestController
public class BranchController implements Plugin {


	@Autowired
	private BranchService service;

	@Operation(summary = "getAllBranches", description = "Lists all Branchs")
	@IOperation(Name = "getAllBranches", Description = "Lists all Branchs")
	@PostMapping("/getAllBranches")
	public PaginationResponse<Branch> getAllBranchs(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody BranchFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllBranches(securityContext, filtering);
	}

	@PostMapping("/createBranch")
	@Operation(summary = "createBranch", description = "Creates Branch")
	@IOperation(Name = "createBranch", Description = "Creates Branch")
	public Branch createBranch(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody BranchCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createBranch(creationContainer, securityContext);
	}

	@PutMapping("/updateBranch")
	@Operation(summary = "updateBranch", description = "Updates Branch")
	@IOperation(Name = "updateBranch", Description = "Updates Branch")
	public Branch updateBranch(

			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody BranchUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		Branch branch = service.getByIdOrNull(updateContainer.getId(),
				Branch.class, Branch_.security, securityContext);
		if (branch == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Branch with id "
					+ updateContainer.getId());
		}
		updateContainer.setBranch(branch);

		return service.updateBranch(updateContainer, securityContext);
	}
}