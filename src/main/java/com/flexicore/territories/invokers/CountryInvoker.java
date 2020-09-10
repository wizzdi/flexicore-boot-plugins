package com.flexicore.territories.invokers;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.model.territories.Country;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.CountryFiltering;
import com.flexicore.territories.service.CountryService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Country Invoker", description = "Invoker for Country")
@Extension
@Component
public class CountryInvoker implements ListingInvoker<Country, CountryFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private CountryService countryService;

	@Override
	@InvokerMethodInfo(displayName = "listAllCountrys", description = "lists all Countrys", relatedClasses = {Country.class})
	public PaginationResponse<Country> listAll(CountryFiltering countryFiltering,
			SecurityContext securityContext) {
		countryService.validate(countryFiltering,securityContext);
		return countryService.getAllCountries(securityContext, countryFiltering);
	}

	@Override
	public Class<CountryFiltering> getFilterClass() {
		return CountryFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return Country.class;
	}
}
