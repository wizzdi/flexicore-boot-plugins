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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/plugins/country")
@Tag(name = "Country")

@OperationsInside
@Extension
@RestController
public class CountryRESTService implements Plugin {

	@Autowired
	private CountryService service;


	@Operation(description = "delete country",summary = "delete country")

	@DeleteMapping("deleteCountry/{id}")
	public void deleteCountry(
			
			@PathVariable("id") String id, @RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.deleteCountry(id, securityContextBase);
	}

	@Operation(description = "import countries",summary = "import countries")

	@PostMapping("importCountries")
	public ImportCountriesResponse importCountries(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody ImportCountriesRequest addressImportRequest,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {

		return service.importCountries(securityContextBase, addressImportRequest);
	}


	@Operation(description = "get all countries",summary = "get all countries")

	@PostMapping("getAllCountries")
	public PaginationResponse<Country> getAllCountries(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody CountryFilter filtering,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.validate(filtering, securityContextBase);
		return service.getAllCountries(securityContextBase, filtering);
	}

	@Operation(description = "update country",summary = "update country")


	@PutMapping("/updateCountry")
	public Country updateCountry(

			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody CountryUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		Country country = service.getByIdOrNull(updateContainer.getId(), Country.class, Country_.security, securityContextBase);
		if (country == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Country with id "
					+ updateContainer.getId());
		}
		updateContainer.setCountry(country);
		return service.updateCountry(updateContainer, securityContextBase);
	}

	@Operation(description = "create country",summary = "create country")

	@PostMapping("/createCountry")
	public Country createCountry(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody CountryCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		return service.createCountry(creationContainer, securityContextBase);
	}
}