package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.Street;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.data.NeighbourhoodRepository;
import com.flexicore.territories.data.request.NeighbourhoodCreationContainer;
import com.flexicore.territories.data.request.NeighbourhoodFiltering;
import com.flexicore.territories.data.request.NeighbourhoodUpdateContainer;
import com.flexicore.territories.interfaces.INeighbourhoodService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.logging.Logger;

@PluginInfo(version = 1)
public class NeighbourhoodService implements INeighbourhoodService {

    @Inject
    @PluginInfo(version = 1)
    private NeighbourhoodRepository repository;
    @Inject
    private Logger logger;

    @Override
    public <T extends Baseclass> T getByIdOrNull(String id,
                                                 Class<T> c, List<String> batch, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batch, securityContext);
    }

    @Override
    public Neighbourhood updateNeighbourhood(NeighbourhoodUpdateContainer updateContainer,
                                 SecurityContext securityContext) {
        Neighbourhood neighbourhood = updateContainer.getNeighbourhood();
        if (updateNeighbourhoodNoMerge(neighbourhood, updateContainer)) {
            repository.merge(neighbourhood);

        }
        return neighbourhood;
    }
    @Override
    public void validate(NeighbourhoodCreationContainer neighbourhoodCreationContainer, SecurityContext securityContext) {
        City city = getByIdOrNull(neighbourhoodCreationContainer.getCityId(), City.class, null, securityContext);
        if (city == null) {
            throw new BadRequestException("no City with id " + neighbourhoodCreationContainer.getCityId());
        }
        neighbourhoodCreationContainer.setCity(city);
    }

    private boolean updateNeighbourhoodNoMerge(Neighbourhood neighbourhood, NeighbourhoodCreationContainer creationContainer) {
        boolean update = false;
        if (creationContainer.getName() != null && !creationContainer.getName().equals(neighbourhood.getName())) {
            neighbourhood.setName(creationContainer.getName());
            update = true;
        }
        if (creationContainer.getDescription() != null && !creationContainer.getDescription().equals(neighbourhood.getDescription())) {
            neighbourhood.setDescription(creationContainer.getDescription());
            update = true;
        }
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

    @Override
    public PaginationResponse<Neighbourhood> listAllNeighbourhoodes(SecurityContext securityContext, NeighbourhoodFiltering filtering) {
        QueryInformationHolder<Neighbourhood> queryInfo = new QueryInformationHolder<>(filtering, Neighbourhood.class, securityContext);
        return new PaginationResponse<>(repository.getAllFiltered(queryInfo), filtering, repository.countAllFiltered(queryInfo));
    }

    @Override
    public Neighbourhood createNeighbourhood(NeighbourhoodCreationContainer creationContainer,
                                 SecurityContext securityContext) {
        Neighbourhood neighbourhood = createNeighbourhoodNoMerge(creationContainer, securityContext);
        repository.merge(neighbourhood);
        return neighbourhood;
    }

    private Neighbourhood createNeighbourhoodNoMerge(NeighbourhoodCreationContainer creationContainer, SecurityContext securityContext) {

        Neighbourhood neighbourhood = Neighbourhood.s().CreateUnchecked(creationContainer.getName(), securityContext);
        neighbourhood.Init();
        updateNeighbourhoodNoMerge(neighbourhood, creationContainer);
        return neighbourhood;
    }


}