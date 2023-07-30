package com.flexicore.territories.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.State;
import com.flexicore.model.territories.State_;
import com.flexicore.security.SecurityContextBase;
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
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody StateFilter filtering, @RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.validate(filtering, securityContextBase);
		return service.getAllStates(securityContextBase, filtering);
	}

	@Operation(description = "update state",summary = "update state")

	@PutMapping("/updateState")
	public State updateState(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody StateUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		State state = service.getByIdOrNull(updateContainer.getId(), State.class, State_.security, securityContextBase);
		if (state == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no State with id " + updateContainer.getId());
		}
		updateContainer.setState(state);
		service.validate(updateContainer, securityContextBase);
		return service.updateState(updateContainer, securityContextBase);
	}


	@Operation(description = "create state",summary = "create state")

	@PostMapping("/createState")
	public State createState(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody StateCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.validate(creationContainer, securityContextBase);
		return service.createState(creationContainer, securityContextBase);
	}

}