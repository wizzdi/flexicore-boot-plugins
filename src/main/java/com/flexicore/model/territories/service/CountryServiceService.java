package com.flexicore.model.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.Exception;
import com.flexicore.model.territories.data.CountryServiceRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.data.containers.CountryFiltering;
import com.flexicore.model.territories.data.containers.CountryUpdateContainer;
import com.flexicore.model.territories.data.containers.CountryCreationContainer;

@PluginInfo(version = 1)
public class CountryServiceService implements ServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private CountryServiceRepository repository;
	@Inject
	private Logger logger;

	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
			Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public void deleteCountry(String countryid, SecurityContext securityContext) {
		Country country = getByIdOrNull(countryid, Country.class, null,
				securityContext);
		repository.remove(country);
	}

	public List<Country> listAllCountries(
			SecurityContext securityContext,
			com.flexicore.model.territories.data.containers.CountryFiltering filtering) {
		QueryInformationHolder<Country> queryInfo = new QueryInformationHolder<>(
				filtering, Country.class, securityContext);
		return repository.getAllFiltered(queryInfo);
	}

	public Country updateCountry(CountryUpdateContainer updateContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Country country = updateContainer.getCountry();
		repository.merge(country);
		return country;
	}

	public Country createCountry(CountryCreationContainer creationContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Country country = Country.s().CreateUnchecked("Country",
				securityContext.getUser());
		country.Init();
		repository.merge(country);
		return country;
	}
}