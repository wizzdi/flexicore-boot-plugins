package com.flexicore.territories.rest;


import com.flexicore.annotations.OperationsInside;
import com.flexicore.model.territories.Address;
import com.flexicore.model.territories.Address_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.reponse.AddressImportResponse;
import com.flexicore.territories.request.AddressCreate;
import com.flexicore.territories.request.AddressFilter;
import com.flexicore.territories.request.AddressImportRequest;
import com.flexicore.territories.request.AddressUpdate;
import com.flexicore.territories.service.AddressService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;


@OperationsInside
@Extension
@RestController
@RequestMapping("/plugins/address")
public class AddressRESTService implements Plugin {

	@Autowired
	private AddressService service;

	@PutMapping("/updateAddress")
	public Address updateAddress(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody AddressUpdate updateContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		Address address = service.getByIdOrNull(updateContainer.getId(), Address.class, Address_.security, securityContextBase);
		if (address == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"no Address with id " + updateContainer.getId());
		}
		updateContainer.setAddress(address);
		service.validate(updateContainer, securityContextBase);
		return service.updateAddress(updateContainer, securityContextBase);
	}


	@PostMapping("/importAddresses")
	public AddressImportResponse importAddresses(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody AddressImportRequest addressImportRequest,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		return service.importAddresses(securityContextBase, addressImportRequest);
	}

	@PostMapping("getAllAddresses")
	public PaginationResponse<Address> getAllAddresses(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody AddressFilter filtering, @RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		return service.getAllAddresses(securityContextBase, filtering);
	}


	@PostMapping("/createAddress")
	public Address createAddress(
			@RequestHeader("authenticationKey") String authenticationKey,
			@RequestBody AddressCreate creationContainer,
			@RequestAttribute("securityContext") SecurityContextBase securityContextBase) {
		service.validate(creationContainer, securityContextBase);
		return service.createAddress(creationContainer, securityContextBase);
	}

}