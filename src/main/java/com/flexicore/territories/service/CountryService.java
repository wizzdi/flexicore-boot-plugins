package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.territories.data.CountryRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.territories.interfaces.ICountryService;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.Country;
import com.flexicore.territories.data.request.CountryUpdateContainer;
import com.flexicore.territories.data.request.CountryCreationContainer;

@PluginInfo(version = 1)
public class CountryService implements ICountryService {

	@Inject
	@PluginInfo(version = 1)
	private CountryRepository repository;
	@Inject
	private Logger logger;

	@Override
	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
												 Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	@Override
	public void deleteCountry(String countryid, SecurityContext securityContext) {
		Country country = getByIdOrNull(countryid, Country.class, null,
				securityContext);
		repository.remove(country);
	}

	@Override
	public List<Country> listAllCountries(
			SecurityContext securityContext,
			com.flexicore.territories.data.request.CountryFiltering filtering) {
		QueryInformationHolder<Country> queryInfo = new QueryInformationHolder<>(
				filtering, Country.class, securityContext);
		return repository.getAllFiltered(queryInfo);
	}

	@Override
	public Country updateCountry(CountryUpdateContainer updateContainer,
								 com.flexicore.security.SecurityContext securityContext) {
		Country country = updateContainer.getCountry();
		repository.merge(country);
		return country;
	}

	@Override
	public Country createCountry(CountryCreationContainer creationContainer,
								 com.flexicore.security.SecurityContext securityContext) {
		Country country = Country.s().CreateUnchecked("Country",
				securityContext);
		country.Init();
		repository.merge(country);
		return country;
	}
}