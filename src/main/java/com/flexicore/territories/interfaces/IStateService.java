package com.flexicore.territories.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.State;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.StateCreate;
import com.flexicore.territories.request.StateFiltering;
import com.flexicore.territories.request.StateUpdate;

import java.util.List;

public interface IStateService extends ServicePlugin {
    <T extends Baseclass> T getByIdOrNull(String id,
                                          Class<T> c, List<String> batch, SecurityContext securityContext);

    List<State> listAllStates(
            SecurityContext securityContext,
            StateFiltering filtering);

    State updateState(StateUpdate updateContainer,
                      SecurityContext securityContext);

    State createState(StateCreate creationContainer,
                      SecurityContext securityContext);

    void deleteState(String stateid, SecurityContext securityContext);

    PaginationResponse<State> getAllStates(SecurityContext securityContext, StateFiltering filtering);
}
