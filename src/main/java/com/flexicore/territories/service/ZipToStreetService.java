package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.territories.data.ZipToStreetRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.territories.interfaces.IZipToStreetService;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.data.request.ZipToStreetCreationContainer;
import com.flexicore.model.territories.ZipToStreet;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.territories.data.request.ZipToStreetUpdateContainer;

@PluginInfo(version = 1)
public class ZipToStreetService implements IZipToStreetService {

	@Inject
	@PluginInfo(version = 1)
	private ZipToStreetRepository repository;
	@Inject
	private Logger logger;

	@Override
	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
												 Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	@Override
	public ZipToStreet createZipToStreet(
			ZipToStreetCreationContainer creationContainer,
			com.flexicore.security.SecurityContext securityContext) {
		ZipToStreet ziptostreet = ZipToStreet.s().CreateUnchecked(
				"ZipToStreet", securityContext);
		ziptostreet.Init();
		ziptostreet.setLeftside(creationContainer.getZip());
		ziptostreet.setRightside(creationContainer.getStreet());
		repository.merge(ziptostreet);
		return ziptostreet;
	}

	@Override
	public void deleteZipToStreet(String ziptostreetid,
								  SecurityContext securityContext) {
		ZipToStreet ziptostreet = getByIdOrNull(ziptostreetid,
				ZipToStreet.class, null, securityContext);
		repository.remove(ziptostreet);
	}

	@Override
	public List<ZipToStreet> listAllZipToStreets(
			SecurityContext securityContext,
			com.flexicore.territories.data.request.ZipToStreetFiltering filtering) {
		QueryInformationHolder<ZipToStreet> queryInfo = new QueryInformationHolder<>(
				filtering, ZipToStreet.class, securityContext);
		return repository.getAllFiltered(queryInfo);
	}

	@Override
	public ZipToStreet updateZipToStreet(
			ZipToStreetUpdateContainer updateContainer,
			com.flexicore.security.SecurityContext securityContext) {
		ZipToStreet ziptostreet = updateContainer.getZipToStreet();
		ziptostreet.setLeftside(updateContainer.getZip());
		ziptostreet.setRightside(updateContainer.getStreet());
		repository.merge(ziptostreet);
		return ziptostreet;
	}
}