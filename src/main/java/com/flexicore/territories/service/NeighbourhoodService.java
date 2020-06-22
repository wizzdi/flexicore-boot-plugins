package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.territories.data.NeighbourhoodRepository;
import com.flexicore.territories.request.NeighbourhoodCreationContainer;
import com.flexicore.territories.request.NeighbourhoodFiltering;
import com.flexicore.territories.request.NeighbourhoodUpdateContainer;
import com.flexicore.territories.interfaces.INeighbourhoodService;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.logging.Logger;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class NeighbourhoodService implements INeighbourhoodService {

	@PluginInfo(version = 1)
	@Autowired
	private NeighbourhoodRepository repository;
	@Autowired
	private BaseclassNewService baseclassNewService;

	@Autowired
	private Logger logger;

	@Override
	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	@Override
	public Neighbourhood updateNeighbourhood(
			NeighbourhoodUpdateContainer updateContainer,
			SecurityContext securityContext) {
		Neighbourhood neighbourhood = updateContainer.getNeighbourhood();
		if (updateNeighbourhoodNoMerge(neighbourhood, updateContainer)) {
			repository.merge(neighbourhood);

		}
		return neighbourhood;
	}
	@Override
	public void validate(
			NeighbourhoodCreationContainer neighbourhoodCreationContainer,
			SecurityContext securityContext) {
		baseclassNewService.validateCreate(neighbourhoodCreationContainer,
				securityContext);
		City city = getByIdOrNull(neighbourhoodCreationContainer.getCityId(),
				City.class, null, securityContext);
		if (city == null) {
			throw new BadRequestException("no City with id "
					+ neighbourhoodCreationContainer.getCityId());
		}
		neighbourhoodCreationContainer.setCity(city);
	}

	@Override
	public boolean updateNeighbourhoodNoMerge(Neighbourhood neighbourhood,
			NeighbourhoodCreationContainer creationContainer) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(
				creationContainer, neighbourhood);
		if (creationContainer.getExternalId() != null
				&& !creationContainer.getExternalId().equals(
						neighbourhood.getExternalId())) {
			neighbourhood.setExternalId(creationContainer.getExternalId());
			update = true;
		}
		if (creationContainer.getCity() != null
				&& (neighbourhood.getCity() == null || !creationContainer
						.getCity().getId()
						.equals(neighbourhood.getCity().getId()))) {
			neighbourhood.setCity(creationContainer.getCity());
			update = true;
		}

		return update;
	}

	@Override
	public PaginationResponse<Neighbourhood> listAllNeighbourhoodes(
			SecurityContext securityContext, NeighbourhoodFiltering filtering) {
		QueryInformationHolder<Neighbourhood> queryInfo = new QueryInformationHolder<>(
				filtering, Neighbourhood.class, securityContext);
		return new PaginationResponse<>(repository.getAllFiltered(queryInfo),
				filtering, repository.countAllFiltered(queryInfo));
	}

	@Override
	public List<Neighbourhood> getAllNeighbourhoodes(
			SecurityContext securityContext, NeighbourhoodFiltering filtering) {
		return repository.getAllNeighbourhoods(securityContext, filtering);
	}

	@Override
	public Neighbourhood createNeighbourhood(
			NeighbourhoodCreationContainer creationContainer,
			SecurityContext securityContext) {
		Neighbourhood neighbourhood = createNeighbourhoodNoMerge(
				creationContainer, securityContext);
		repository.merge(neighbourhood);
		return neighbourhood;
	}

	@Override
	public Neighbourhood createNeighbourhoodNoMerge(
			NeighbourhoodCreationContainer creationContainer,
			SecurityContext securityContext) {

		Neighbourhood neighbourhood = new Neighbourhood(
				creationContainer.getName(), securityContext);
		updateNeighbourhoodNoMerge(neighbourhood, creationContainer);
		return neighbourhood;
	}

}