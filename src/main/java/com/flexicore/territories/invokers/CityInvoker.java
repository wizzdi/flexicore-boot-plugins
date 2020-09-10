package com.flexicore.territories.invokers;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.model.territories.City;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.CityFiltering;
import com.flexicore.territories.service.CityService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "City Invoker", description = "Invoker for City")
@Extension
@Component
public class CityInvoker implements ListingInvoker<City, CityFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private CityService cityService;

	@Override
	@InvokerMethodInfo(displayName = "listAllCitys", description = "lists all Citys", relatedClasses = {City.class})
	public PaginationResponse<City> listAll(CityFiltering cityFiltering,
			SecurityContext securityContext) {
		cityService.validate(cityFiltering,securityContext);
		return cityService.getAllCities(securityContext, cityFiltering);
	}

	@Override
	public Class<CityFiltering> getFilterClass() {
		return CityFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return City.class;
	}
}
