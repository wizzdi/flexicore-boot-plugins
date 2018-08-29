package com.flexicore.model.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.Exception;
import com.flexicore.model.territories.data.ZipServiceRepository;
import javax.inject.Inject;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.model.territories.data.containers.ZipUpdateContainer;
import com.flexicore.model.territories.Zip;
import com.flexicore.model.territories.data.containers.ZipCreationContainer;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.model.territories.data.containers.ZipFiltering;

@PluginInfo(version = 1)
public class ZipServiceService implements ServicePlugin {

	@Inject
	@PluginInfo(version = 1)
	private ZipServiceRepository repository;
	@Inject
	private Logger logger;

	public <T extends Baseclass> T getByIdOrNull(java.lang.String id,
			Class<T> c, List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public Zip updateZip(ZipUpdateContainer updateContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Zip zip = updateContainer.getZip();
		repository.merge(zip);
		return zip;
	}

	public Zip createZip(ZipCreationContainer creationContainer,
			com.flexicore.security.SecurityContext securityContext) {
		Zip zip = Zip.s().CreateUnchecked("Zip", securityContext.getUser());
		zip.Init();
		repository.merge(zip);
		return zip;
	}

	public void deleteZip(String zipid, SecurityContext securityContext) {
		Zip zip = getByIdOrNull(zipid, Zip.class, null, securityContext);
		repository.remove(zip);
	}

	public List<Zip> listAllZips(
			SecurityContext securityContext,
			com.flexicore.model.territories.data.containers.ZipFiltering filtering) {
		QueryInformationHolder<Zip> queryInfo = new QueryInformationHolder<>(
				filtering, Zip.class, securityContext);
		return repository.getAllFiltered(queryInfo);
	}
}