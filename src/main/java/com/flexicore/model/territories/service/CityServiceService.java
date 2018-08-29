package com.flexicore.model.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.Exception;
import com.flexicore.model.territories.data.CityServiceRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.data.containers.CityFiltering;
import com.flexicore.model.territories.data.containers.CityUpdateContainer;
import com.flexicore.model.territories.data.containers.CityCreationContainer;

@PluginInfo(version = 1)
public class CityServiceService implements ServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private CityServiceRepository repository;
	@Inject
	private Logger logger;

	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
			Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public List<City> listAllCities(
			SecurityContext securityContext,
			com.flexicore.model.territories.data.containers.CityFiltering filtering) {
		QueryInformationHolder<City> queryInfo = new QueryInformationHolder<>(
				filtering, City.class, securityContext);
		return repository.getAllFiltered(queryInfo);
	}

	public City updateCity(CityUpdateContainer updateContainer,
			com.flexicore.security.SecurityContext securityContext) {
		City city = updateContainer.getCity();
		city.setCountry(updateContainer.getCountry());
		repository.merge(city);
		return city;
	}

	public City createCity(CityCreationContainer creationContainer,
			com.flexicore.security.SecurityContext securityContext) {
		City city = City.s().CreateUnchecked("City", securityContext.getUser());
		city.Init();
		city.setCountry(creationContainer.getCountry());
		repository.merge(city);
		return city;
	}

	public void deleteCity(String cityid, SecurityContext securityContext) {
		City city = getByIdOrNull(cityid, City.class, null, securityContext);
		repository.remove(city);
	}
}