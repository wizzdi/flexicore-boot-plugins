package com.flexicore.territories.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Neighbourhood_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.request.NeighbourhoodCreate;
import com.flexicore.territories.request.NeighbourhoodFilter;
import com.flexicore.territories.request.NeighbourhoodUpdate;
import com.flexicore.territories.service.NeighbourhoodService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RequestMapping("/plugins/neighbourhood")
@OperationsInside
@Extension
@RestController
public class NeighbourhoodRESTService implements Plugin {

	@Autowired
	private NeighbourhoodService service;


	@PutMapping("/updateNeighbourhood")
	public Neighbourhood updateNeighbourhood(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody NeighbourhoodUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		Neighbourhood neighbourhood = service.getByIdOrNull(updateContainer.getId(), Neighbourhood.class, Neighbourhood_.security, securityContextBase);
		if (neighbourhood == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"no Neighbourhood with id " + updateContainer.getId());
		}
		updateContainer.setNeighbourhood(neighbourhood);
		service.validate(updateContainer, securityContextBase);
		return service.updateNeighbourhood(updateContainer, securityContextBase);
	}

	@PostMapping("/getAllNeighbourhoods")
	public PaginationResponse<Neighbourhood> getAllNeighbourhoods(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody NeighbourhoodFilter filtering,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.validate(filtering,securityContextBase);
		return service.getAllNeighbourhoods(securityContextBase, filtering);
	}

	@PostMapping("/createNeighbourhood")
	public Neighbourhood createNeighbourhood(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody NeighbourhoodCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.validate(creationContainer, securityContextBase);

		return service.createNeighbourhood(creationContainer, securityContextBase);
	}

}