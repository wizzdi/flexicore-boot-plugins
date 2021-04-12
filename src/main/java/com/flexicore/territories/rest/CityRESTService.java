package com.flexicore.territories.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.City_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.request.CityCreate;
import com.flexicore.territories.request.CityFilter;
import com.flexicore.territories.request.CityUpdate;
import com.flexicore.territories.service.CityService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@OperationsInside
@RequestMapping("/plugins/city")
@Tag(name = "City")

@Extension
@RestController
public class CityRESTService implements Plugin {

	@Autowired
	private CityService service;



	@PostMapping("/getAllCities")
	public PaginationResponse<City> getAllCities(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody CityFilter filtering, @RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.validate(filtering, securityContextBase);
		return service.getAllCities(securityContextBase, filtering);
	}

	@PutMapping("/updateCity")
	public City updateCity(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody CityUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		City city = service.getByIdOrNull(updateContainer.getId(), City.class, City_.security, securityContextBase);
		if (city == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"no City with id " + updateContainer.getId());
		}
		updateContainer.setCity(city);
		service.validate(updateContainer, securityContextBase);
		return service.updateCity(updateContainer, securityContextBase);
	}

	@PostMapping("/createCity")
	public City createCity(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody CityCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.validate(creationContainer, securityContextBase);
		return service.createCity(creationContainer, securityContextBase);
	}


	@DeleteMapping("deleteCity/{id}")
	public void deleteCity(
			@RequestHeader("authenticationKey") String authenticationKey,
			@PathVariable("id") String id, @RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.deleteCity(id, securityContextBase);
	}
}