package com.flexicore.territories.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.*;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.data.StreetRepository;
import com.flexicore.territories.request.StreetCreate;
import com.flexicore.territories.request.StreetFilter;
import com.flexicore.territories.request.StreetUpdate;
import com.wizzdi.flexicore.boot.base.annotations.plugins.PluginInfo;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class StreetService implements Plugin {

	@PluginInfo(version = 1)
	@Autowired
	private StreetRepository repository;

	@Autowired
	private BasicService basicService;

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public List<Street> listAllStreets(SecurityContext SecurityContext,
									   StreetFilter filtering) {
		return repository.listAllStreets(SecurityContext, filtering);

	}

	
	public PaginationResponse<Street> getAllStreets(
			SecurityContext SecurityContext, StreetFilter filtering) {
		List<Street> list = listAllStreets(SecurityContext, filtering);
		long count = repository.countAllStreets(SecurityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	
	public Street updateStreet(StreetUpdate updateContainer,
							   SecurityContext SecurityContext) {
		Street street = updateContainer.getStreet();
		if (updateStreetNoMerge(street, updateContainer)) {
			repository.merge(street);
		}
		return street;
	}

	public void validate(StreetUpdate updateContainer,
						 SecurityContext SecurityContext) {
		City city = updateContainer.getCityId() != null ? getByIdOrNull(updateContainer.getCityId(), City.class,  SecurityContext) : null;
		if (city == null && updateContainer.getCityId() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no City with id "
					+ updateContainer.getCityId());
		}
		updateContainer.setCity(city);
	}

	public void validate(StreetFilter streetFiltering,
						 SecurityContext SecurityContext) {
		basicService.validate(streetFiltering, SecurityContext);
		Set<String> citiesIds = streetFiltering.getCitiesIds();
		Map<String,City> cityMap = citiesIds.isEmpty()?new HashMap<>():repository.listByIds(City.class,citiesIds,SecurityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
		citiesIds.removeAll(cityMap.keySet());
		if(!citiesIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Cities with ids "+citiesIds);
		}
		streetFiltering.setCities(new ArrayList<>(cityMap.values()));

		Set<String> countriesIds = streetFiltering.getCountriesIds();
		Map<String, Country> countryMap = countriesIds.isEmpty()?new HashMap<>():repository.listByIds(Country.class,countriesIds,SecurityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		countriesIds.removeAll(countryMap.keySet());
		if(!countriesIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Countries with ids "+countriesIds);
		}
		streetFiltering.setCountries(new ArrayList<>(countryMap.values()));


		Set<String> neighbourhoodsIds = streetFiltering.getNeighbourhoodsIds();
		Map<String, Neighbourhood> stringNeighbourhoodMap = neighbourhoodsIds.isEmpty()?new HashMap<>():repository.listByIds(Neighbourhood.class,neighbourhoodsIds,SecurityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		neighbourhoodsIds.removeAll(stringNeighbourhoodMap.keySet());
		if(!neighbourhoodsIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Neighbourhoods with ids "+neighbourhoodsIds);
		}
		streetFiltering.setNeighbourhoods(new ArrayList<>(stringNeighbourhoodMap.values()));
	}


	
	public Street createStreetNoMerge(
			StreetCreate streetCreationContainer,
			SecurityContext SecurityContext) {
		Street street = new Street().setId(UUID.randomUUID().toString());
		BaseclassService.createSecurityObjectNoMerge(street,SecurityContext);
		updateStreetNoMerge(street, streetCreationContainer);
		return street;

	}

	
	public boolean updateStreetNoMerge(Street street,
			StreetCreate streetCreationContainer) {

		boolean update = basicService.updateBasicNoMerge(
				streetCreationContainer, street);
		if (street.isSoftDelete()) {
			street.setSoftDelete(false);
			update = true;
		}
		if (streetCreationContainer.getExternalId() != null && !streetCreationContainer.getExternalId().equals(street.getExternalId())) {
			street.setExternalId(streetCreationContainer.getExternalId());
			update = true;
		}
		if (streetCreationContainer.getCity() != null && (street.getCity() == null || !streetCreationContainer.getCity().getId().equals(street.getCity().getId()))) {
			street.setCity(streetCreationContainer.getCity());
			update = true;
		}
		return update;

	}

	
	public Street createStreet(StreetCreate creationContainer,
							   SecurityContext SecurityContext) {
		Street street = createStreetNoMerge(creationContainer, SecurityContext);
		repository.merge(street);
		return street;
	}

	
	public void deleteStreet(String streetid, SecurityContext SecurityContext) {
		Street street = getByIdOrNull(streetid, Street.class,  SecurityContext);
		repository.remove(street);
	}
}
