package com.flexicore.model.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.Exception;
import com.flexicore.model.territories.data.StreetServiceRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.Street;
import com.flexicore.model.territories.data.containers.StreetFiltering;
import com.flexicore.model.territories.data.containers.StreetUpdateContainer;
import com.flexicore.model.territories.data.containers.StreetCreationContainer;

@PluginInfo(version = 1)
public class StreetServiceService implements ServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private StreetServiceRepository repository;
	@Inject
	private Logger logger;

	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
			Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public List<Street> listAllStreets(
			SecurityContext securityContext,
			com.flexicore.model.territories.data.containers.StreetFiltering filtering) {
		QueryInformationHolder<Street> queryInfo = new QueryInformationHolder<>(
				filtering, Street.class, securityContext);
		return repository.getAllFiltered(queryInfo);
	}

	public Street updateStreet(StreetUpdateContainer updateContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Street street = updateContainer.getStreet();
		street.setCity(updateContainer.getCity());
		repository.merge(street);
		return street;
	}

	public Street createStreet(StreetCreationContainer creationContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Street street = Street.s().CreateUnchecked("Street",
				securityContext.getUser());
		street.Init();
		street.setCity(creationContainer.getCity());
		repository.merge(street);
		return street;
	}

	public void deleteStreet(String streetid, SecurityContext securityContext) {
		Street street = getByIdOrNull(streetid, Street.class, null,
				securityContext);
		repository.remove(street);
	}
}