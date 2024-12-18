package com.flexicore.territories.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.State;
import com.flexicore.model.territories.State_;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.request.StateCreate;
import com.flexicore.territories.request.StateFilter;
import com.flexicore.territories.request.StateUpdate;
import com.flexicore.territories.service.StateService;
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
@RequestMapping("/plugins/state")
@Tag(name = "State")
@Extension
@RestController
public class StateRESTService implements Plugin {

	@Autowired
	private StateService service;

	@Operation(description = "get all states",summary = "get all states")

	@PostMapping("/getAllStates")
	public PaginationResponse<State> getAllStates(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody StateFilter filtering, @RequestAttribute("securityContext") SecurityContext SecurityContext) {
		service.validate(filtering, SecurityContext);
		return service.getAllStates(SecurityContext, filtering);
	}

	@Operation(description = "update state",summary = "update state")

	@PutMapping("/updateState")
	public State updateState(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody StateUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		State state = service.getByIdOrNull(updateContainer.getId(), State.class,SecurityContext);
		if (state == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no State with id " + updateContainer.getId());
		}
		updateContainer.setState(state);
		service.validate(updateContainer, SecurityContext);
		return service.updateState(updateContainer, SecurityContext);
	}


	@Operation(description = "create state",summary = "create state")

	@PostMapping("/createState")
	public State createState(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody StateCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		service.validate(creationContainer, SecurityContext);
		return service.createState(creationContainer, SecurityContext);
	}

}
