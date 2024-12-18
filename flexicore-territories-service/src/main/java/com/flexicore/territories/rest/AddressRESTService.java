package com.flexicore.territories.rest;


import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.Address;
import com.flexicore.model.territories.Address_;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.reponse.AddressImportResponse;
import com.flexicore.territories.request.AddressCreate;
import com.flexicore.territories.request.AddressFilter;
import com.flexicore.territories.request.AddressImportRequest;
import com.flexicore.territories.request.AddressUpdate;
import com.flexicore.territories.service.AddressService;
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
@Extension
@RestController
@Tag(name = "Address")
@RequestMapping("/plugins/address")
public class AddressRESTService implements Plugin {

	@Autowired
	private AddressService service;

	@Operation(description = "update address",summary = "update address")

	@PutMapping("/updateAddress")
	public Address updateAddress(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody AddressUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		Address address = service.getByIdOrNull(updateContainer.getId(), Address.class,SecurityContext);
		if (address == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Address with id " + updateContainer.getId());
		}
		updateContainer.setAddress(address);
		service.validate(updateContainer, SecurityContext);
		return service.updateAddress(updateContainer, SecurityContext);
	}


	@Operation(description = "import address",summary = "import address")

	@PostMapping("/importAddresses")
	public AddressImportResponse importAddresses(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody AddressImportRequest addressImportRequest,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		return service.importAddresses(SecurityContext, addressImportRequest);
	}

	@Operation(description = "get all address",summary = "get all address")

	@PostMapping("getAllAddresses")
	public PaginationResponse<Address> getAllAddresses(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody AddressFilter filtering, @RequestAttribute("securityContext") SecurityContext SecurityContext) {
		return service.getAllAddresses(SecurityContext, filtering);
	}


	@Operation(description = "create address",summary = "create address")
	@PostMapping("/createAddress")
	public Address createAddress(
			
			@RequestHeader(value = "authenticationKey",required = false) String key,@RequestBody AddressCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContext SecurityContext) {
		service.validate(creationContainer, SecurityContext);
		return service.createAddress(creationContainer, SecurityContext);
	}

}
