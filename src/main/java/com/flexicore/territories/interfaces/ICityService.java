package com.flexicore.territories.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.City;
import com.flexicore.territories.request.CityCreationContainer;
import com.flexicore.territories.request.CityFiltering;
import com.flexicore.territories.request.CityUpdateContainer;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface ICityService extends ServicePlugin {
	<T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batch, SecurityContext securityContext);

	List<City> listAllCities(SecurityContext securityContext,
			com.flexicore.territories.request.CityFiltering filtering);

	City updateCity(CityUpdateContainer updateContainer,
			SecurityContext securityContext);

	City createCity(CityCreationContainer creationContainer,
			SecurityContext securityContext);

	void deleteCity(String cityid, SecurityContext securityContext);

	PaginationResponse<City> getAllCities(SecurityContext securityContext,
			CityFiltering filtering);
}
