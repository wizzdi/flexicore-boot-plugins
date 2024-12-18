package com.flexicore.territories.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.City_;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.request.CityCreate;
import com.flexicore.territories.request.CityFilter;
import com.flexicore.territories.request.CityUpdate;
import com.flexicore.territories.service.CityService;
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
@RequestMapping("/plugins/city")
@Tag(name = "City")

@Extension
@RestController
public class CityRESTService implements Plugin {

	@Autowired
	private CityService service;



	@Operation(description = "get all cities",summary = "get all cities")

	@PostMapping("/getAllCities")
	public PaginationResponse<City> getAllCities(
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody CityFilter filtering, @RequestAttribute("securityContext") SecurityContext SecurityContext) {
		service.validate(filtering, SecurityContext);
		return service.getAllCities(SecurityContext, filtering);
	}

	@Operation(description = "update city",summary = "update city")
	@PutMapping("/updateCity")
	public City updateCity(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody CityUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		City city = service.getByIdOrNull(updateContainer.getId(), City.class,SecurityContext);
		if (city == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no City with id " + updateContainer.getId());
		}
		updateContainer.setCity(city);
		service.validate(updateContainer, SecurityContext);
		return service.updateCity(updateContainer, SecurityContext);
	}

	@Operation(description = "create city",summary = "create city")

	@PostMapping("/createCity")
	public City createCity(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody CityCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		service.validate(creationContainer, SecurityContext);
		return service.createCity(creationContainer, SecurityContext);
	}


	@Operation(description = "delete city",summary = "delete city")

	@DeleteMapping("deleteCity/{id}")
	public void deleteCity(
			
			@PathVariable("id") String id, @RequestAttribute("securityContext") SecurityContext SecurityContext) {
		service.deleteCity(id, SecurityContext);
	}
}
