package com.flexicore.territories.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.City_;
import com.flexicore.model.territories.Neighbourhood;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.data.NeighbourhoodRepository;
import com.flexicore.territories.request.NeighbourhoodCreate;
import com.flexicore.territories.request.NeighbourhoodFilter;
import com.flexicore.territories.request.NeighbourhoodUpdate;
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
public class NeighbourhoodService implements Plugin {

	@PluginInfo(version = 1)
	@Autowired
	private NeighbourhoodRepository repository;
	@Autowired
	private BasicService basicService;

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}


	public Neighbourhood updateNeighbourhood(
			NeighbourhoodUpdate updateContainer,
			SecurityContext SecurityContext) {
		Neighbourhood neighbourhood = updateContainer.getNeighbourhood();
		if (updateNeighbourhoodNoMerge(neighbourhood, updateContainer)) {
			repository.merge(neighbourhood);

		}
		return neighbourhood;
	}
	public void validate(NeighbourhoodFilter neighbourhoodFiltering, SecurityContext SecurityContext) {
		Set<String> citiesIds = neighbourhoodFiltering.getCitiesIds();
		Map<String,City> cityMap = citiesIds.isEmpty()?new HashMap<>():repository.listByIds(City.class,citiesIds, SecurityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		citiesIds.removeAll(cityMap.keySet());
		if(!citiesIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Cities with ids "+citiesIds);
		}
		neighbourhoodFiltering.setCities(new ArrayList<>(cityMap.values()));
	}

		
	public void validate(NeighbourhoodCreate neighbourhoodCreationContainer, SecurityContext SecurityContext) {
		basicService.validate(neighbourhoodCreationContainer,
				SecurityContext);
		String cityId = neighbourhoodCreationContainer.getCityId();
		City city = cityId!=null?getByIdOrNull(cityId, City.class,SecurityContext):null;
		if (city == null&&cityId!=null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no City with id " + cityId);
		}
		neighbourhoodCreationContainer.setCity(city);
	}

	
	public boolean updateNeighbourhoodNoMerge(Neighbourhood neighbourhood,
			NeighbourhoodCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, neighbourhood);

		if (creationContainer.getExternalId() != null && !creationContainer.getExternalId().equals(neighbourhood.getExternalId())) {
			neighbourhood.setExternalId(creationContainer.getExternalId());
			update = true;
		}
		if (creationContainer.getCity() != null && (neighbourhood.getCity() == null || !creationContainer.getCity().getId().equals(neighbourhood.getCity().getId()))) {
			neighbourhood.setCity(creationContainer.getCity());
			update = true;
		}

		return update;
	}


	public List<Neighbourhood> listAllNeighbourhoods(
			SecurityContext SecurityContext, NeighbourhoodFilter filtering) {
		return repository.getAllNeighbourhoods(SecurityContext,filtering);
	}

	
	public PaginationResponse<Neighbourhood> getAllNeighbourhoods(
			SecurityContext SecurityContext, NeighbourhoodFilter filtering) {
		List<Neighbourhood> list = listAllNeighbourhoods(SecurityContext,filtering);
		long count=repository.countAllNeighbourhoods(SecurityContext,filtering);
		return new PaginationResponse<>(list,filtering,count);
	}

	
	public Neighbourhood createNeighbourhood(
			NeighbourhoodCreate creationContainer,
			SecurityContext SecurityContext) {
		Neighbourhood neighbourhood = createNeighbourhoodNoMerge(creationContainer, SecurityContext);
		repository.merge(neighbourhood);
		return neighbourhood;
	}

	
	public Neighbourhood createNeighbourhoodNoMerge(
			NeighbourhoodCreate creationContainer,
			SecurityContext SecurityContext) {

		Neighbourhood neighbourhood = new Neighbourhood().setId(UUID.randomUUID().toString());
		BaseclassService.createSecurityObjectNoMerge(neighbourhood,SecurityContext);
		updateNeighbourhoodNoMerge(neighbourhood, creationContainer);
		return neighbourhood;
	}

}
