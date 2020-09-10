package com.flexicore.territories.invokers;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.model.territories.State;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.StateFiltering;
import com.flexicore.territories.service.StateService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "State Invoker", description = "Invoker for State")
@Extension
@Component
public class StateInvoker implements ListingInvoker<State, StateFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private StateService stateService;

	@Override
	@InvokerMethodInfo(displayName = "listAllStates", description = "lists all States", relatedClasses = {State.class})
	public PaginationResponse<State> listAll(StateFiltering stateFiltering,
			SecurityContext securityContext) {
		stateService.validate(stateFiltering,securityContext);
		return stateService.getAllStates(securityContext, stateFiltering);
	}

	@Override
	public Class<StateFiltering> getFilterClass() {
		return StateFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return State.class;
	}
}
