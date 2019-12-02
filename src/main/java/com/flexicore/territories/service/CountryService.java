package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.Country;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.territories.data.CountryRepository;
import com.flexicore.territories.interfaces.ICountryService;
import com.flexicore.territories.request.CountryCreationContainer;
import com.flexicore.territories.request.CountryFiltering;
import com.flexicore.territories.request.CountryUpdateContainer;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

@PluginInfo(version = 1)
public class CountryService implements ICountryService {

    @Inject
    @PluginInfo(version = 1)
    private CountryRepository repository;
    @Inject
    private BaseclassNewService baseclassNewService;

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

        return repository.listAllCountries(securityContext, filtering);
    }


    @Override
    public PaginationResponse<Country> getAllCountries(SecurityContext securityContext, CountryFiltering filtering) {

        List<Country> list = repository.listAllCountries(securityContext, filtering);
        long count = repository.countAllCountries(securityContext, filtering);
        return new PaginationResponse<>(list, filtering, count);
    }

    @Override
    public void validate(CountryFiltering filtering, SecurityContext securityContext) {
        baseclassNewService.validateFilter(filtering, securityContext);
    }

    @Override
    public Country updateCountry(CountryUpdateContainer updateContainer,
                                 com.flexicore.security.SecurityContext securityContext) {
        Country country = updateContainer.getCountry();
        if (updateCountryNoMerge(country, updateContainer)) {
            repository.merge(country);

        }
        return country;
    }

    @Override
    public Country createCountry(CountryCreationContainer creationContainer,
                                 com.flexicore.security.SecurityContext securityContext) {
        Country country = createCountryNoMerge(creationContainer, securityContext);
        repository.merge(country);
        return country;
    }

    private Country createCountryNoMerge(CountryCreationContainer creationContainer, SecurityContext securityContext) {
        Country country = new Country(creationContainer.getName(), securityContext);
        updateCountryNoMerge(country, creationContainer);
        return country;
    }

    private boolean updateCountryNoMerge(Country country, CountryCreationContainer creationContainer) {
        boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer,country);
        return update;

    }
}