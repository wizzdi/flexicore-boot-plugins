package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;

import java.util.List;
import java.util.logging.Logger;

import com.flexicore.territories.data.ZipRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.territories.interfaces.IZipService;
import com.flexicore.security.SecurityContext;
import com.flexicore.territories.data.request.ZipUpdateContainer;
import com.flexicore.model.territories.Zip;
import com.flexicore.territories.data.request.ZipCreationContainer;
import com.flexicore.model.QueryInformationHolder;

@PluginInfo(version = 1)
public class ZipService implements IZipService {

	@Inject
	@PluginInfo(version = 1)
	private ZipRepository repository;
	@Inject
	private Logger logger;

	@Override
	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
												 Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	@Override
	public Zip updateZip(ZipUpdateContainer updateContainer,
						 com.flexicore.security.SecurityContext securityContext) {
		Zip zip = updateContainer.getZip();
		repository.merge(zip);
		return zip;
	}

	@Override
	public Zip createZip(ZipCreationContainer creationContainer,
						 com.flexicore.security.SecurityContext securityContext) {
		Zip zip = Zip.s().CreateUnchecked("Zip", securityContext);
		zip.Init();
		repository.merge(zip);
		return zip;
	}

	@Override
	public void deleteZip(String zipid, SecurityContext securityContext) {
		Zip zip = getByIdOrNull(zipid, Zip.class, null, securityContext);
		repository.remove(zip);
	}

	@Override
	public List<Zip> listAllZips(
			SecurityContext securityContext,
			com.flexicore.territories.data.request.ZipFiltering filtering) {
		QueryInformationHolder<Zip> queryInfo = new QueryInformationHolder<>(
				filtering, Zip.class, securityContext);
		return repository.getAllFiltered(queryInfo);
	}
}