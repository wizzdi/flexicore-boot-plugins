package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.territories.data.CountryRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.territories.data.request.CountryFiltering;
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
	public List<Country> listAllCountries(SecurityContext securityContext, CountryFiltering filtering) {

		return repository.listAllCountries(securityContext,filtering);
	}


	@Override
	public PaginationResponse<Country> getAllCountries(SecurityContext securityContext, CountryFiltering filtering) {

		List<Country> list= repository.listAllCountries(securityContext,filtering);
		long count=repository.countAllCountries(securityContext,filtering);
		return new PaginationResponse<>(list,filtering,count);
	}

	@Override
	public void validate(CountryFiltering filtering, SecurityContext securityContext) {

	}

	@Override
	public Country updateCountry(CountryUpdateContainer updateContainer,
								 com.flexicore.security.SecurityContext securityContext) {
		Country country = updateContainer.getCountry();
		if(updateCountryNoMerge(country,updateContainer)){
			repository.merge(country);

		}
		return country;
	}

	@Override
	public Country createCountry(CountryCreationContainer creationContainer,
								 com.flexicore.security.SecurityContext securityContext) {
		Country country =createCountryNoMerge(creationContainer,securityContext);
		repository.merge(country);
		return country;
	}

	private Country createCountryNoMerge(CountryCreationContainer creationContainer, SecurityContext securityContext) {
		Country country=Country.s().CreateUnchecked("Country",
				securityContext);
		country.Init();
		updateCountryNoMerge(country,creationContainer);
		return country;
	}

	private boolean updateCountryNoMerge(Country country, CountryCreationContainer creationContainer) {
		boolean update=false;
		if(creationContainer.getName()!=null && !country.getName().equals(creationContainer.getName())){
			country.setName(creationContainer.getName());
			update=true;
		}
		if(creationContainer.getDescription()!=null && !country.getDescription().equals(creationContainer.getDescription())){
			country.setDescription(creationContainer.getDescription());
			update=true;
		}
		return update;

	}
}