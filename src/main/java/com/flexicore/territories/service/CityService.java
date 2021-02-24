package com.flexicore.territories.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.*;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.data.CityRepository;
import com.flexicore.territories.request.CityCreate;
import com.flexicore.territories.request.CityFilter;
import com.flexicore.territories.request.CityUpdate;
import com.wizzdi.flexicore.boot.base.annotations.plugins.PluginInfo;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class CityService implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(CityService.class);

	@PluginInfo(version = 1)
	@Autowired
	private CityRepository repository;

	@Autowired
	private BasicService basicService;

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	
	public List<City> listAllCities(SecurityContextBase securityContextBase,
			CityFilter filtering) {
		return repository.listAllCities(securityContextBase, filtering);
	}

	
	public PaginationResponse<City> getAllCities(
			SecurityContextBase securityContextBase, CityFilter filtering) {
		List<City> list = repository.listAllCities(securityContextBase, filtering);
		long count = repository.countAllCities(securityContextBase, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	
	public City updateCity(CityUpdate updateContainer,
						   com.flexicore.security.SecurityContextBase securityContextBase) {
		City city = updateContainer.getCity();
		if (updateCityNoMerge(updateContainer, city)) {
			repository.merge(city);
		}
		return city;
	}

	public void validate(CityCreate creationContainer,
						 SecurityContextBase securityContextBase) {
		basicService.validate(creationContainer, securityContextBase);
		String countryId = creationContainer.getCountryId();
		Country country = countryId != null ? getByIdOrNull(countryId, Country.class, Country_.security, securityContextBase) : null;
		if (country == null&& countryId != null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"no Country with id " + countryId);
		}
		creationContainer.setCountry(country);

		String stateId = creationContainer.getStateId();
		State state = stateId != null ? getByIdOrNull(stateId, State.class, State_.security, securityContextBase) : null;
		if (stateId != null && state == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"no State with id " + stateId);
		}
		creationContainer.setState(state);
	}

	public void validate(CityFilter cityFiltering,
						 SecurityContextBase securityContextBase) {
		basicService.validate(cityFiltering, securityContextBase);
		Set<String> countriesIds = cityFiltering.getCountriesIds();
		Map<String, Country> countryMap = countriesIds.isEmpty()?new HashMap<>():repository.listByIds(Country.class,countriesIds, Country_.security,securityContextBase).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		countriesIds.removeAll(countryMap.keySet());
		if(!countriesIds.isEmpty()){
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"No Countries with ids "+countriesIds);
		}
		cityFiltering.setCountries(new ArrayList<>(countryMap.values()));

		Set<String> stateIds = cityFiltering.getStatesIds();
		Map<String, State> stateMa = stateIds.isEmpty()?new HashMap<>():repository.listByIds(State.class,stateIds, State_.security,securityContextBase).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		stateIds.removeAll(stateMa.keySet());
		if(!stateIds.isEmpty()){
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"No States with ids "+stateIds);
		}
		cityFiltering.setStates(new ArrayList<>(stateMa.values()));
	}

	
	public City createCity(CityCreate creationContainer, SecurityContextBase securityContextBase) {
		City city = createCityNoMerge(creationContainer, securityContextBase);
		Baseclass baseclass=new Baseclass(creationContainer.getName(),securityContextBase);
		city.setSecurity(baseclass);
		repository.massMerge(Arrays.asList(city,baseclass));
		return city;
	}

	public City createCityNoMerge(CityCreate creationContainer,
								  SecurityContextBase securityContextBase) {
		City city = new City().setId(Baseclass.getBase64ID());
		updateCityNoMerge(creationContainer, city);
		return city;
	}

	public boolean updateCityNoMerge(CityCreate creationContainer,
									 City city) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, city);
		if (city.isSoftDelete()) {
			city.setSoftDelete(false);
			update = true;
		}
		if (creationContainer.getExternalId() != null && !creationContainer.getExternalId().equals(city.getExternalId())) {
			city.setExternalId(creationContainer.getExternalId());
			update = true;
		}

		if (creationContainer.getCountry() != null && (city.getCountry() == null || !creationContainer.getCountry().getId().equals(city.getCountry().getId()))) {
			city.setCountry(creationContainer.getCountry());
			update = true;
		}
		if (creationContainer.getState() != null && (city.getState() == null || !creationContainer.getState().getId().equals(city.getState().getId()))) {
			city.setState(creationContainer.getState());
			update = true;
		}
		return update;

	}

	
	public void deleteCity(String cityid, SecurityContextBase securityContextBase) {
		City city = getByIdOrNull(cityid, City.class, City_.security, securityContextBase);
		repository.remove(city);
	}
}