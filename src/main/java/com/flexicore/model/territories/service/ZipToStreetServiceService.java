package com.flexicore.model.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.Exception;
import com.flexicore.model.territories.data.ZipToStreetServiceRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.territories.data.containers.ZipToStreetCreationContainer;
import com.flexicore.model.territories.ZipToStreet;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.data.containers.ZipToStreetFiltering;
import com.flexicore.model.territories.data.containers.ZipToStreetUpdateContainer;

@PluginInfo(version = 1)
public class ZipToStreetServiceService implements ServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private ZipToStreetServiceRepository repository;
	@Inject
	private Logger logger;

	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
			Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public ZipToStreet createZipToStreet(
			ZipToStreetCreationContainer creationContainer,
			com.flexicore.security.SecurityContext securityContext) {
		ZipToStreet ziptostreet = ZipToStreet.s().CreateUnchecked(
				"ZipToStreet", securityContext.getUser());
		ziptostreet.Init();
		ziptostreet.setLeftside(creationContainer.getLeftside());
		ziptostreet.setRightside(creationContainer.getRightside());
		repository.merge(ziptostreet);
		return ziptostreet;
	}

	public void deleteZipToStreet(String ziptostreetid,
			SecurityContext securityContext) {
		ZipToStreet ziptostreet = getByIdOrNull(ziptostreetid,
				ZipToStreet.class, null, securityContext);
		repository.remove(ziptostreet);
	}

	public List<ZipToStreet> listAllZipToStreets(
			SecurityContext securityContext,
			com.flexicore.model.territories.data.containers.ZipToStreetFiltering filtering) {
		QueryInformationHolder<ZipToStreet> queryInfo = new QueryInformationHolder<>(
				filtering, ZipToStreet.class, securityContext);
		return repository.getAllFiltered(queryInfo);
	}

	public ZipToStreet updateZipToStreet(
			ZipToStreetUpdateContainer updateContainer,
			com.flexicore.security.SecurityContext securityContext) {
		ZipToStreet ziptostreet = updateContainer.getZipToStreet();
		ziptostreet.setLeftside(updateContainer.getLeftside());
		ziptostreet.setRightside(updateContainer.getRightside());
		repository.merge(ziptostreet);
		return ziptostreet;
	}
}