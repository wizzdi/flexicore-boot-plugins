package com.flexicore.territories.invokers;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.NeighbourhoodFiltering;
import com.flexicore.territories.service.NeighbourhoodService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Neighbourhood Invoker", description = "Invoker for Neighbourhood")
@Extension
@Component
public class NeighbourhoodInvoker implements ListingInvoker<Neighbourhood, NeighbourhoodFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private NeighbourhoodService neighbourhoodService;

	@Override
	@InvokerMethodInfo(displayName = "listAllNeighbourhoods", description = "lists all Neighbourhoods", relatedClasses = {Neighbourhood.class})
	public PaginationResponse<Neighbourhood> listAll(
			NeighbourhoodFiltering neighbourhoodFiltering,
			SecurityContext securityContext) {
		neighbourhoodService.validate(neighbourhoodFiltering,securityContext);
		return neighbourhoodService.listAllNeighbourhoodes(securityContext, neighbourhoodFiltering);
	}

	@Override
	public Class<NeighbourhoodFiltering> getFilterClass() {
		return NeighbourhoodFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return Neighbourhood.class;
	}
}
