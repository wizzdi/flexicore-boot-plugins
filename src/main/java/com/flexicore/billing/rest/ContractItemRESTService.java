package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.ContractItem;
import com.flexicore.billing.request.ContractItemCreate;
import com.flexicore.billing.request.ContractItemFiltering;
import com.flexicore.billing.request.ContractItemUpdate;
import com.flexicore.billing.service.ContractItemService;
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
@Path("plugins/ContractItem")
@RequestScoped
@Tag(name = "ContractItem")
@Extension
@Component
public class ContractItemRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private ContractItemService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllContractItems", description = "Lists all ContractItems")
	@IOperation(Name = "getAllContractItems", Description = "Lists all ContractItems")
	@Path("getAllContractItems")
	public PaginationResponse<ContractItem> getAllContractItems(
			@HeaderParam("authenticationKey") String authenticationKey,
			ContractItemFiltering filtering, @Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllContractItems(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createContractItem")
	@Operation(summary = "createContractItem", description = "Creates ContractItem")
	@IOperation(Name = "createContractItem", Description = "Creates ContractItem")
	public ContractItem createContractItem(
			@HeaderParam("authenticationKey") String authenticationKey,
			ContractItemCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createContractItem(creationContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updateContractItem")
	@Operation(summary = "updateContractItem", description = "Updates ContractItem")
	@IOperation(Name = "updateContractItem", Description = "Updates ContractItem")
	public ContractItem updateContractItem(
			@HeaderParam("authenticationKey") String authenticationKey,
			ContractItemUpdate updateContainer,
			@Context SecurityContext securityContext) {
		service.validate(updateContainer, securityContext);
		ContractItem contractItem = service.getByIdOrNull(updateContainer.getId(),
				ContractItem.class, null, securityContext);
		if (contractItem == null) {
			throw new BadRequestException("no ContractItem with id "
					+ updateContainer.getId());
		}
		updateContainer.setContractItem(contractItem);

		return service.updateContractItem(updateContainer, securityContext);
	}
}