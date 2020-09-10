package com.flexicore.territories.invokers;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.model.territories.Address;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.AddressFiltering;
import com.flexicore.territories.service.AddressService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Address Invoker", description = "Invoker for Address")
@Extension
@Component
public class AddressInvoker implements ListingInvoker<Address, AddressFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private AddressService addressService;

	@Override
	@InvokerMethodInfo(displayName = "listAllAddresss", description = "lists all Addresss", relatedClasses = {Address.class})
	public PaginationResponse<Address> listAll(AddressFiltering addressFiltering,
			SecurityContext securityContext) {
		addressService.validate(addressFiltering,securityContext);
		return addressService.listAllAddresses(securityContext, addressFiltering);
	}

	@Override
	public Class<AddressFiltering> getFilterClass() {
		return AddressFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return Address.class;
	}
}
