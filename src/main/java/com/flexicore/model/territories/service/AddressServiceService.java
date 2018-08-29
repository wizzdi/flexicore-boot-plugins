package com.flexicore.model.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.Exception;
import com.flexicore.model.territories.data.AddressServiceRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.territories.data.containers.AddressUpdateContainer;
import com.flexicore.model.territories.Address;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.data.containers.AddressFiltering;
import com.flexicore.model.territories.data.containers.AddressCreationContainer;

@PluginInfo(version = 1)
public class AddressServiceService implements ServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private AddressServiceRepository repository;
	@Inject
	private Logger logger;

	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
			Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public Address updateAddress(AddressUpdateContainer updateContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Address address = updateContainer.getAddress();
		address.setStreet(updateContainer.getStreet());
		repository.merge(address);
		return address;
	}

	public List<Address> listAllAddresses(
			SecurityContext securityContext,
			com.flexicore.model.territories.data.containers.AddressFiltering filtering) {
		QueryInformationHolder<Address> queryInfo = new QueryInformationHolder<>(
				filtering, Address.class, securityContext);
		return repository.getAllFiltered(queryInfo);
	}

	public Address createAddress(AddressCreationContainer creationContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Address address = Address.s().CreateUnchecked("Address",
				securityContext.getUser());
		address.Init();
		address.setStreet(creationContainer.getStreet());
		repository.merge(address);
		return address;
	}

	public void deleteAddress(String addressid, SecurityContext securityContext) {
		Address address = getByIdOrNull(addressid, Address.class, null,
				securityContext);
		repository.remove(address);
	}
}