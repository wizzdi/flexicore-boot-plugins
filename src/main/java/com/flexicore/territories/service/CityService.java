package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.State;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.territories.data.CityRepository;

import javax.ws.rs.BadRequestException;

import com.flexicore.model.Baseclass;
import com.flexicore.territories.request.CityFiltering;
import com.flexicore.territories.interfaces.ICityService;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.territories.City;
import com.flexicore.territories.request.CityUpdateContainer;
import com.flexicore.territories.request.CityCreationContainer;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class CityService implements ICityService {

	@PluginInfo(version = 1)
	@Autowired
	private CityRepository repository;

	@Autowired
	private BaseclassNewService baseclassNewService;
	@Autowired
	private Logger logger;

	@Override
	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
			Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	@Override
	public List<City> listAllCities(SecurityContext securityContext,
			CityFiltering filtering) {
		return repository.listAllCities(securityContext, filtering);
	}

	@Override
	public PaginationResponse<City> getAllCities(
			SecurityContext securityContext, CityFiltering filtering) {
		List<City> list = repository.listAllCities(securityContext, filtering);
		long count = repository.countAllCities(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	@Override
	public City updateCity(CityUpdateContainer updateContainer,
			com.flexicore.security.SecurityContext securityContext) {
		City city = updateContainer.getCity();
		if (updateCityNoMerge(updateContainer, city)) {
			repository.merge(city);
		}
		return city;
	}

	public void validate(CityCreationContainer creationContainer,
			SecurityContext securityContext) {
		baseclassNewService.validateCreate(creationContainer, securityContext);
		String countryId = creationContainer.getCountryId();
		Country country = countryId != null ? getByIdOrNull(countryId,
				Country.class, null, securityContext) : null;
		if (country == null) {
			throw new BadRequestException("no Country with id " + countryId);
		}
		creationContainer.setCountry(country);

		String stateId = creationContainer.getStateId();
		State state = stateId != null ? getByIdOrNull(stateId, State.class,
				null, securityContext) : null;
		if (stateId != null && state == null) {
			throw new BadRequestException("no State with id " + stateId);
		}
		creationContainer.setState(state);
	}

	public void validate(CityFiltering creationContainer,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(creationContainer, securityContext);
		String countryId = creationContainer.getCountryId();
		Country country = countryId != null ? getByIdOrNull(countryId,
				Country.class, null, securityContext) : null;
		if (countryId != null && country == null) {
			throw new BadRequestException("no Country with id " + countryId);
		}
		creationContainer.setCountry(country);

		String stateId = creationContainer.getStateId();
		State state = stateId != null ? getByIdOrNull(stateId, State.class,
				null, securityContext) : null;
		if (stateId != null && state == null) {
			throw new BadRequestException("no State with id " + stateId);
		}
		creationContainer.setState(state);
	}

	@Override
	public City createCity(CityCreationContainer creationContainer,
			com.flexicore.security.SecurityContext securityContext) {
		City city = createCityNoMerge(creationContainer, securityContext);
		repository.merge(city);
		return city;
	}

	public City createCityNoMerge(CityCreationContainer creationContainer,
			SecurityContext securityContext) {
		City city = new City(creationContainer.getName(), securityContext);
		updateCityNoMerge(creationContainer, city);
		return city;
	}

	public boolean updateCityNoMerge(CityCreationContainer creationContainer,
			City city) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(
				creationContainer, city);
		if (city.isSoftDelete()) {
			city.setSoftDelete(false);
			update = true;
		}
		if (creationContainer.getExternalId() != null
				&& !creationContainer.getExternalId().equals(
						city.getExternalId())) {
			city.setExternalId(creationContainer.getExternalId());
			update = true;
		}

		if (creationContainer.getCountry() != null
				&& (city.getCountry() == null || !creationContainer
						.getCountry().getId().equals(city.getCountry().getId()))) {
			city.setCountry(creationContainer.getCountry());
			update = true;
		}
		if (creationContainer.getState() != null
				&& (city.getState() == null || !creationContainer.getState()
						.getId().equals(city.getState().getId()))) {
			city.setState(creationContainer.getState());
			update = true;
		}
		return update;

	}

	@Override
	public void deleteCity(String cityid, SecurityContext securityContext) {
		City city = getByIdOrNull(cityid, City.class, null, securityContext);
		repository.remove(city);
	}
}