package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.Contract;
import com.flexicore.billing.request.ContractCreate;
import com.flexicore.billing.request.ContractFiltering;
import com.flexicore.billing.request.ContractUpdate;
import com.flexicore.billing.service.ContractService;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/Contract")
@RequestScoped
@Tag(name = "Contract")
@Extension
@Component
public class ContractRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private ContractService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllContracts", description = "Lists all Contracts")
	@IOperation(Name = "getAllContracts", Description = "Lists all Contracts")
	@Path("getAllContracts")
	public PaginationResponse<Contract> getAllContracts(
			@HeaderParam("authenticationKey") String authenticationKey,
			ContractFiltering filtering, @Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllContracts(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createContract")
	@Operation(summary = "createContract", description = "Creates Contract")
	@IOperation(Name = "createContract", Description = "Creates Contract")
	public Contract createContract(
			@HeaderParam("authenticationKey") String authenticationKey,
			ContractCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createContract(creationContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updateContract")
	@Operation(summary = "updateContract", description = "Updates Contract")
	@IOperation(Name = "updateContract", Description = "Updates Contract")
	public Contract updateContract(
			@HeaderParam("authenticationKey") String authenticationKey,
			ContractUpdate updateContainer,
			@Context SecurityContext securityContext) {
		service.validate(updateContainer, securityContext);
		Contract contract = service.getByIdOrNull(updateContainer.getId(),
				Contract.class, null, securityContext);
		if (contract == null) {
			throw new BadRequestException("no Contract with id "
					+ updateContainer.getId());
		}
		updateContainer.setContract(contract);

		return service.updateContract(updateContainer, securityContext);
	}
}