package com.flexicore.territories.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.City_;
import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.Street_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.request.StreetCreate;
import com.flexicore.territories.request.StreetFilter;
import com.flexicore.territories.request.StreetUpdate;
import com.flexicore.territories.service.StreetService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@OperationsInside
@RequestMapping("/plugins/street")
@Tag(name = "Street")
@Extension
@RestController
public class StreetRESTService implements Plugin {

	@Autowired
	private StreetService service;


	@PostMapping("/getAllStreets")
	public PaginationResponse<Street> getAllStreets(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody StreetFilter filtering, @RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.validate(filtering, securityContextBase);
		return service.getAllStreets(securityContextBase, filtering);
	}


	@PutMapping("/updateStreet")
	public Street updateStreet(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody StreetUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		Street street = service.getByIdOrNull(updateContainer.getId(),
				Street.class, Street_.security, securityContextBase);
		if (street == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"no Street with id "
					+ updateContainer.getId());
		}
		updateContainer.setStreet(street);
		service.validate(updateContainer, securityContextBase);
		return service.updateStreet(updateContainer, securityContextBase);
	}

	@PostMapping("/createStreet")
	public Street createStreet(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody StreetCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		City city = service.getByIdOrNull(creationContainer.getCityId(),
				City.class, City_.security, securityContextBase);
		if (city == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"no City with id "
					+ creationContainer.getCityId());
		}
		creationContainer.setCity(city);
		return service.createStreet(creationContainer, securityContextBase);
	}

	@DeleteMapping("/deleteStreet/{id}")
	public void deleteStreet(
			@RequestHeader("authenticationKey") String authenticationKey,
			@PathVariable("id") String id, @RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.deleteStreet(id, securityContextBase);
	}
}