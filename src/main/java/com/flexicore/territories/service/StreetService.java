package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.territories.data.StreetRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.territories.interfaces.IStreetService;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.Street;
import com.flexicore.territories.data.request.StreetUpdateContainer;
import com.flexicore.territories.data.request.StreetCreationContainer;

@PluginInfo(version = 1)
public class StreetService implements IStreetService {

	@Inject
	@PluginInfo(version = 1)
	private StreetRepository repository;
	@Inject
	private Logger logger;

	@Override
	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
												 Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	@Override
	public List<Street> listAllStreets(
			SecurityContext securityContext,
			com.flexicore.territories.data.request.StreetFiltering filtering) {
		QueryInformationHolder<Street> queryInfo = new QueryInformationHolder<>(
				filtering, Street.class, securityContext);
		return repository.getAllFiltered(queryInfo);
	}

	@Override
	public Street updateStreet(StreetUpdateContainer updateContainer,
							   com.flexicore.security.SecurityContext securityContext) {
		Street street = updateContainer.getStreet();
		street.setCity(updateContainer.getCity());
		repository.merge(street);
		return street;
	}

	@Override
	public Street createStreet(StreetCreationContainer creationContainer,
							   com.flexicore.security.SecurityContext securityContext) {
		Street street = Street.s().CreateUnchecked("Street",
				securityContext);
		street.Init();
		street.setCity(creationContainer.getCity());
		repository.merge(street);
		return street;
	}

	@Override
	public void deleteStreet(String streetid, SecurityContext securityContext) {
		Street street = getByIdOrNull(streetid, Street.class, null,
				securityContext);
		repository.remove(street);
	}
}