package com.flexicore.territories.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.Country;
import com.flexicore.territories.data.request.CountryCreationContainer;
import com.flexicore.territories.data.request.CountryFiltering;
import com.flexicore.territories.data.request.CountryUpdateContainer;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface ICountryService extends ServicePlugin {
    <T extends Baseclass> T getByIdOrNull(String id,
                                          Class<T> c, List<String> batch, SecurityContext securityContext);

    void deleteCountry(String countryid, SecurityContext securityContext);

    List<Country> listAllCountries(
            SecurityContext securityContext,
            com.flexicore.territories.data.request.CountryFiltering filtering);

    PaginationResponse<Country> getAllCountries(SecurityContext securityContext, CountryFiltering filtering);

    Country updateCountry(CountryUpdateContainer updateContainer,
                          SecurityContext securityContext);

    Country createCountry(CountryCreationContainer creationContainer,
                          SecurityContext securityContext);

    void validate(CountryFiltering filtering, SecurityContext securityContext);
}
