package com.flexicore.billing.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.data.BusinessServiceRepository;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.request.BusinessServiceCreate;
import com.flexicore.billing.request.BusinessServiceFiltering;
import com.flexicore.billing.request.BusinessServiceUpdate;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@PluginInfo(version = 1)
@Extension
@Component
@Primary
public class BusinessServiceService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private BusinessServiceRepository repository;

	@Autowired
	private BaseclassNewService baseclassNewService;

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
                                                 List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids,
                                                   SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}

	public void validateFiltering(BusinessServiceFiltering filtering,
								  SecurityContext securityContext) {
		baseclassNewService.validateFilter(filtering, securityContext);
	}

	public PaginationResponse<BusinessService> getAllBusinessServices(
			SecurityContext securityContext, BusinessServiceFiltering filtering) {
		List<BusinessService> list = repository.getAllBusinessServices(securityContext, filtering);
		long count = repository.countAllBusinessServices(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public BusinessService createBusinessService(BusinessServiceCreate creationContainer,
												 SecurityContext securityContext) {
		BusinessService businessService = createBusinessServiceNoMerge(creationContainer, securityContext);
		repository.merge(businessService);
		return businessService;
	}

	private BusinessService createBusinessServiceNoMerge(BusinessServiceCreate creationContainer,
                                       SecurityContext securityContext) {
		BusinessService businessService = new BusinessService(creationContainer.getName(),securityContext);
		updateBusinessServiceNoMerge(businessService, creationContainer);
		return businessService;
	}

	private boolean updateBusinessServiceNoMerge(BusinessService businessService,
			BusinessServiceCreate creationContainer) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer, businessService);

		return update;
	}

	public BusinessService updateBusinessService(BusinessServiceUpdate updateContainer,
												 SecurityContext securityContext) {
		BusinessService businessService = updateContainer.getBusinessService();
		if (updateBusinessServiceNoMerge(businessService, updateContainer)) {
			repository.merge(businessService);
		}
		return businessService;
	}

	public void validate(BusinessServiceCreate creationContainer,
                         SecurityContext securityContext) {
		baseclassNewService.validate(creationContainer, securityContext);
	}
}