package com.flexicore.territories.invokers;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.dynamic.InvokerInfo;
import com.flexicore.interfaces.dynamic.InvokerMethodInfo;
import com.flexicore.interfaces.dynamic.ListingInvoker;
import com.flexicore.model.territories.Street;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.request.StreetFiltering;
import com.flexicore.territories.service.StreetService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@InvokerInfo(displayName = "Street Invoker", description = "Invoker for Street")
@Extension
@Component
public class StreetInvoker implements ListingInvoker<Street, StreetFiltering> {

	@PluginInfo(version = 1)
	@Autowired
	private StreetService streetService;

	@Override
	@InvokerMethodInfo(displayName = "listAllStreets", description = "lists all Streets", relatedClasses = {Street.class})
	public PaginationResponse<Street> listAll(StreetFiltering streetFiltering,
			SecurityContext securityContext) {
		streetService.validate(streetFiltering,securityContext);
		return streetService.getAllStreets(securityContext, streetFiltering);
	}

	@Override
	public Class<StreetFiltering> getFilterClass() {
		return StreetFiltering.class;
	}

	@Override
	public Class<?> getHandlingClass() {
		return Street.class;
	}
}
