package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.territories.data.StreetRepository;

import javax.ws.rs.BadRequestException;

import com.flexicore.model.Baseclass;
import com.flexicore.territories.request.StreetFiltering;
import com.flexicore.territories.interfaces.IStreetService;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.territories.Street;
import com.flexicore.territories.request.StreetUpdateContainer;
import com.flexicore.territories.request.StreetCreationContainer;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class StreetService implements IStreetService {

	@PluginInfo(version = 1)
	@Autowired
	private StreetRepository repository;

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
	public List<Street> listAllStreets(SecurityContext securityContext,
			StreetFiltering filtering) {
		return repository.listAllStreets(securityContext, filtering);

	}

	@Override
	public PaginationResponse<Street> getAllStreets(
			SecurityContext securityContext, StreetFiltering filtering) {
		List<Street> list = repository.listAllStreets(securityContext,
				filtering);
		long count = repository.countAllStreets(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	@Override
	public Street updateStreet(StreetUpdateContainer updateContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Street street = updateContainer.getStreet();
		if (updateStreetNoMerge(street, updateContainer)) {
			repository.merge(street);
		}
		return street;
	}

	public void validate(StreetUpdateContainer updateContainer,
			SecurityContext securityContext) {
		City city = updateContainer.getCityId() != null
				? getByIdOrNull(updateContainer.getCityId(), City.class, null,
						securityContext) : null;
		if (city == null && updateContainer.getCityId() != null) {
			throw new BadRequestException("no City with id "
					+ updateContainer.getCityId());
		}
		updateContainer.setCity(city);
	}

	public void validate(StreetFiltering streetFiltering,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(streetFiltering, securityContext);
		Set<String> citiesIds = streetFiltering.getCitiesIds().stream().map(f->f.getId()).collect(Collectors.toSet());
		Map<String,City> cityMap = citiesIds.isEmpty()?new HashMap<>():repository.listByIds(City.class,citiesIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
		citiesIds.removeAll(cityMap.keySet());
		if(!citiesIds.isEmpty()){
			throw new BadRequestException("No Cities with ids "+citiesIds);
		}
		streetFiltering.setCities(new ArrayList<>(cityMap.values()));

		Set<String> countriesIds = streetFiltering.getCountriesIds().stream().map(f->f.getId()).collect(Collectors.toSet());
		Map<String, Country> countryMap = countriesIds.isEmpty()?new HashMap<>():repository.listByIds(Country.class,countriesIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		countriesIds.removeAll(countryMap.keySet());
		if(!countriesIds.isEmpty()){
			throw new BadRequestException("No Countries with ids "+countriesIds);
		}
		streetFiltering.setCountries(new ArrayList<>(countryMap.values()));


		Set<String> neighbourhoodsIds = streetFiltering.getNeighbourhoodsIds().stream().map(f->f.getId()).collect(Collectors.toSet());
		Map<String, Neighbourhood> stringNeighbourhoodMap = neighbourhoodsIds.isEmpty()?new HashMap<>():repository.listByIds(Neighbourhood.class,neighbourhoodsIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		neighbourhoodsIds.removeAll(stringNeighbourhoodMap.keySet());
		if(!neighbourhoodsIds.isEmpty()){
			throw new BadRequestException("No Neighbourhoods with ids "+neighbourhoodsIds);
		}
		streetFiltering.setNeighbourhoods(new ArrayList<>(stringNeighbourhoodMap.values()));
	}


	@Override
	public Street createStreetNoMerge(
			StreetCreationContainer streetCreationContainer,
			SecurityContext securityContext) {
		Street street = new Street(streetCreationContainer.getName(),
				securityContext);
		updateStreetNoMerge(street, streetCreationContainer);
		return street;

	}

	@Override
	public boolean updateStreetNoMerge(Street street,
			StreetCreationContainer streetCreationContainer) {

		boolean update = baseclassNewService.updateBaseclassNoMerge(
				streetCreationContainer, street);
		if (street.isSoftDelete()) {
			street.setSoftDelete(false);
			update = true;
		}
		if (streetCreationContainer.getExternalId() != null
				&& !streetCreationContainer.getExternalId().equals(
						street.getExternalId())) {
			street.setExternalId(streetCreationContainer.getExternalId());
			update = true;
		}
		if (streetCreationContainer.getCity() != null
				&& (street.getCity() == null || !streetCreationContainer
						.getCity().getId().equals(street.getCity().getId()))) {
			street.setCity(streetCreationContainer.getCity());
			update = true;
		}
		return update;

	}

	@Override
	public Street createStreet(StreetCreationContainer creationContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Street street = createStreetNoMerge(creationContainer, securityContext);
		repository.merge(street);
		return street;
	}

	@Override
	public void deleteStreet(String streetid, SecurityContext securityContext) {
		Street street = getByIdOrNull(streetid, Street.class, null,
				securityContext);
		repository.remove(street);
	}
}