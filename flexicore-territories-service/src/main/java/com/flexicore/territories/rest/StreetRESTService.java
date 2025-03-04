package com.flexicore.territories.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.City_;
import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.Street_;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.request.StreetCreate;
import com.flexicore.territories.request.StreetFilter;
import com.flexicore.territories.request.StreetUpdate;
import com.flexicore.territories.service.StreetService;
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
@RequestMapping("/plugins/street")
@Tag(name = "Street")
@Extension
@RestController
public class StreetRESTService implements Plugin {

	@Autowired
	private StreetService service;


	@Operation(description = "get all streets",summary = "get all streets")

	@PostMapping("/getAllStreets")
	public PaginationResponse<Street> getAllStreets(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody StreetFilter filtering, @RequestAttribute("securityContext") SecurityContext SecurityContext) {
		service.validate(filtering, SecurityContext);
		return service.getAllStreets(SecurityContext, filtering);
	}


	@Operation(description = "update street",summary = "update street")

	@PutMapping("/updateStreet")
	public Street updateStreet(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody StreetUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		Street street = service.getByIdOrNull(updateContainer.getId(),
				Street.class,  SecurityContext);
		if (street == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Street with id "
					+ updateContainer.getId());
		}
		updateContainer.setStreet(street);
		service.validate(updateContainer, SecurityContext);
		return service.updateStreet(updateContainer, SecurityContext);
	}
	@Operation(description = "create street",summary = "create street")

	@PostMapping("/createStreet")
	public Street createStreet(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody StreetCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		City city = service.getByIdOrNull(creationContainer.getCityId(),
				City.class,  SecurityContext);
		if (city == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no City with id "
					+ creationContainer.getCityId());
		}
		creationContainer.setCity(city);
		return service.createStreet(creationContainer, SecurityContext);
	}

	@Operation(description = "delete street",summary = "delete street")

	@DeleteMapping("/deleteStreet/{id}")
	public void deleteStreet(
			
			@PathVariable("id") String id, @RequestAttribute("securityContext") SecurityContext SecurityContext) {
		service.deleteStreet(id, SecurityContext);
	}
}
