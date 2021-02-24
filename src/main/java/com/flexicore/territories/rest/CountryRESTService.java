package com.flexicore.territories.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.Country_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.reponse.ImportCountriesResponse;
import com.flexicore.territories.request.CountryCreate;
import com.flexicore.territories.request.CountryFilter;
import com.flexicore.territories.request.CountryUpdate;
import com.flexicore.territories.request.ImportCountriesRequest;
import com.flexicore.territories.service.CountryService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RequestMapping("/plugins/country")
@OperationsInside
@Extension
@RestController
public class CountryRESTService implements Plugin {

	@Autowired
	private CountryService service;


	@DeleteMapping("deleteCountry/{id}")
	public void deleteCountry(
			@RequestHeader("authenticationKey") String authenticationKey,
			@PathVariable("id") String id, @RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.deleteCountry(id, securityContextBase);
	}

	@PostMapping("importCountries")
	public ImportCountriesResponse importCountries(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody ImportCountriesRequest addressImportRequest,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {

		return service.importCountries(securityContextBase, addressImportRequest);
	}


	@PostMapping("getAllCountries")
	public PaginationResponse<Country> getAllCountries(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody CountryFilter filtering,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.validate(filtering, securityContextBase);
		return service.getAllCountries(securityContextBase, filtering);
	}


	@PutMapping("/updateCountry")
	public Country updateCountry(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody CountryUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		Country country = service.getByIdOrNull(updateContainer.getId(), Country.class, Country_.security, securityContextBase);
		if (country == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"no Country with id "
					+ updateContainer.getId());
		}
		updateContainer.setCountry(country);
		return service.updateCountry(updateContainer, securityContextBase);
	}


	@PostMapping("/createCountry")
	public Country createCountry(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody CountryCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		return service.createCountry(creationContainer, securityContextBase);
	}
}