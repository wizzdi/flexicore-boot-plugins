package com.flexicore.territories.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.Street;
import com.flexicore.territories.request.StreetCreationContainer;
import com.flexicore.territories.request.StreetUpdateContainer;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface IStreetService extends ServicePlugin {
	<T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batch, SecurityContext securityContext);

	List<Street> listAllStreets(SecurityContext securityContext,
			com.flexicore.territories.request.StreetFiltering filtering);

	PaginationResponse<Street> getAllStreets(SecurityContext securityContext,
			com.flexicore.territories.request.StreetFiltering filtering);

	Street updateStreet(StreetUpdateContainer updateContainer,
			SecurityContext securityContext);

	Street createStreetNoMerge(StreetCreationContainer streetCreationContainer,
			SecurityContext securityContext);

	boolean updateStreetNoMerge(Street street,
			StreetCreationContainer streetCreationContainer);

	Street createStreet(StreetCreationContainer creationContainer,
			SecurityContext securityContext);

	void deleteStreet(String streetid, SecurityContext securityContext);
}
