package com.flexicore.territories.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.model.territories.State;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.StateCreate;
import com.flexicore.territories.request.StateFiltering;
import com.flexicore.territories.request.StateUpdate;
import com.flexicore.territories.service.StateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/State")
@Tag(name = "State")
@Extension
@Component
public class StateRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private StateService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllStates", description = "Lists all States Filtered")
	@IOperation(Name = "getAllStates", Description = "Lists all States Filtered")
	@Path("getAllStates")
	public PaginationResponse<State> getAllStates(
			@HeaderParam("authenticationKey") String authenticationKey,
			StateFiltering filtering, @Context SecurityContext securityContext) {
		service.validate(filtering, securityContext);
		return service.getAllStates(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/updateState")
	@Operation(summary = "updateState", description = "Updates State")
	@IOperation(Name = "updateState", Description = "Updates State")
	public State updateState(
			@HeaderParam("authenticationKey") String authenticationKey,
			StateUpdate updateContainer,
			@Context SecurityContext securityContext) {
		State state = service.getByIdOrNull(updateContainer.getId(),
				State.class, null, securityContext);
		if (state == null) {
			throw new BadRequestException("no State with id "
					+ updateContainer.getId());
		}
		updateContainer.setState(state);
		service.validate(updateContainer, securityContext);
		return service.updateState(updateContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/createState")
	@Operation(summary = "createState", description = "Creates State")
	@IOperation(Name = "createState", Description = "Creates State")
	public State createState(
			@HeaderParam("authenticationKey") String authenticationKey,
			StateCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);
		return service.createState(creationContainer, securityContext);
	}

}