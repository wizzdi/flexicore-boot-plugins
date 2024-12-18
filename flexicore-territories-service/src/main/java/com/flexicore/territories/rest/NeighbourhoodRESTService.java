package com.flexicore.territories.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Neighbourhood_;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.request.NeighbourhoodCreate;
import com.flexicore.territories.request.NeighbourhoodFilter;
import com.flexicore.territories.request.NeighbourhoodUpdate;
import com.flexicore.territories.service.NeighbourhoodService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/plugins/neighbourhood")
@OperationsInside
@Tag(name = "Neighbourhood")
@Extension
@RestController
public class NeighbourhoodRESTService implements Plugin {

	@Autowired
	private NeighbourhoodService service;


	@Operation(description = "update neighbourhood",summary = "update neighbourhood")

	@PutMapping("/updateNeighbourhood")
	public Neighbourhood updateNeighbourhood(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody NeighbourhoodUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		Neighbourhood neighbourhood = service.getByIdOrNull(updateContainer.getId(), Neighbourhood.class,SecurityContext);
		if (neighbourhood == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Neighbourhood with id " + updateContainer.getId());
		}
		updateContainer.setNeighbourhood(neighbourhood);
		service.validate(updateContainer, SecurityContext);
		return service.updateNeighbourhood(updateContainer, SecurityContext);
	}

	@Operation(description = "get all neighbourhoods",summary = "get all neighbourhoods")

	@PostMapping("/getAllNeighbourhoods")
	public PaginationResponse<Neighbourhood> getAllNeighbourhoods(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody NeighbourhoodFilter filtering,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		service.validate(filtering,SecurityContext);
		return service.getAllNeighbourhoods(SecurityContext, filtering);
	}

	@Operation(description = "create neighbourhood",summary = "create neighbourhood")

	@PostMapping("/createNeighbourhood")
	public Neighbourhood createNeighbourhood(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody NeighbourhoodCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		service.validate(creationContainer, SecurityContext);

		return service.createNeighbourhood(creationContainer, SecurityContext);
	}

}
