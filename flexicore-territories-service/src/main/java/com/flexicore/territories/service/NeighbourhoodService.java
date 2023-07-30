package com.flexicore.territories.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.City_;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.security.SecurityContextBase;
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

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public Neighbourhood updateNeighbourhood(
			NeighbourhoodUpdate updateContainer,
			SecurityContextBase securityContextBase) {
		Neighbourhood neighbourhood = updateContainer.getNeighbourhood();
		if (updateNeighbourhoodNoMerge(neighbourhood, updateContainer)) {
			repository.merge(neighbourhood);

		}
		return neighbourhood;
	}
	public void validate(NeighbourhoodFilter neighbourhoodFiltering, SecurityContextBase securityContextBase) {
		Set<String> citiesIds = neighbourhoodFiltering.getCitiesIds();
		Map<String,City> cityMap = citiesIds.isEmpty()?new HashMap<>():repository.listByIds(City.class,citiesIds, City_.security,securityContextBase).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		citiesIds.removeAll(cityMap.keySet());
		if(!citiesIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Cities with ids "+citiesIds);
		}
		neighbourhoodFiltering.setCities(new ArrayList<>(cityMap.values()));
	}

		
	public void validate(NeighbourhoodCreate neighbourhoodCreationContainer, SecurityContextBase securityContextBase) {
		basicService.validate(neighbourhoodCreationContainer,
				securityContextBase);
		String cityId = neighbourhoodCreationContainer.getCityId();
		City city = cityId!=null?getByIdOrNull(cityId, City.class, City_.security, securityContextBase):null;
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
			SecurityContextBase securityContextBase, NeighbourhoodFilter filtering) {
		return repository.getAllNeighbourhoods(securityContextBase,filtering);
	}

	
	public PaginationResponse<Neighbourhood> getAllNeighbourhoods(
			SecurityContextBase securityContextBase, NeighbourhoodFilter filtering) {
		List<Neighbourhood> list = listAllNeighbourhoods(securityContextBase,filtering);
		long count=repository.countAllNeighbourhoods(securityContextBase,filtering);
		return new PaginationResponse<>(list,filtering,count);
	}

	
	public Neighbourhood createNeighbourhood(
			NeighbourhoodCreate creationContainer,
			SecurityContextBase securityContextBase) {
		Neighbourhood neighbourhood = createNeighbourhoodNoMerge(creationContainer, securityContextBase);
		repository.merge(neighbourhood);
		return neighbourhood;
	}

	
	public Neighbourhood createNeighbourhoodNoMerge(
			NeighbourhoodCreate creationContainer,
			SecurityContextBase securityContextBase) {

		Neighbourhood neighbourhood = new Neighbourhood().setId(Baseclass.getBase64ID());
		BaseclassService.createSecurityObjectNoMerge(neighbourhood,securityContextBase);
		updateNeighbourhoodNoMerge(neighbourhood, creationContainer);
		return neighbourhood;
	}

}