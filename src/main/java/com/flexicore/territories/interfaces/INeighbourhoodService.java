package com.flexicore.territories.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.data.request.NeighbourhoodCreationContainer;
import com.flexicore.territories.data.request.NeighbourhoodFiltering;
import com.flexicore.territories.data.request.NeighbourhoodUpdateContainer;

import java.util.List;

public interface INeighbourhoodService extends ServicePlugin {
    <T extends Baseclass> T getByIdOrNull(String id,
                                          Class<T> c, List<String> batch, SecurityContext securityContext);

    Neighbourhood updateNeighbourhood(NeighbourhoodUpdateContainer updateContainer,
                          SecurityContext securityContext);

    void validate(NeighbourhoodCreationContainer neighbourhoodCreationContainer, SecurityContext securityContext);

    PaginationResponse<Neighbourhood> listAllNeighbourhoodes(SecurityContext securityContext, NeighbourhoodFiltering filtering);

    Neighbourhood createNeighbourhood(NeighbourhoodCreationContainer creationContainer,
                          SecurityContext securityContext);
}
