package com.flexicore.billing.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.data.PriceListRepository;
import com.flexicore.billing.model.PriceList;
import com.flexicore.billing.request.PriceListCreate;
import com.flexicore.billing.request.PriceListFiltering;
import com.flexicore.billing.request.PriceListUpdate;
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
public class PriceListService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private PriceListRepository repository;

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

	public void validateFiltering(PriceListFiltering filtering,
								  SecurityContext securityContext) {
		baseclassNewService.validateFilter(filtering, securityContext);
	}

	public PaginationResponse<PriceList> getAllPriceLists(
			SecurityContext securityContext, PriceListFiltering filtering) {
		List<PriceList> list = repository.getAllPriceLists(securityContext, filtering);
		long count = repository.countAllPriceLists(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public PriceList createPriceList(PriceListCreate creationContainer,
												 SecurityContext securityContext) {
		PriceList priceList = createPriceListNoMerge(creationContainer, securityContext);
		repository.merge(priceList);
		return priceList;
	}

	private PriceList createPriceListNoMerge(PriceListCreate creationContainer,
                                       SecurityContext securityContext) {
		PriceList priceList = new PriceList(creationContainer.getName(),securityContext);
		updatePriceListNoMerge(priceList, creationContainer);
		return priceList;
	}

	private boolean updatePriceListNoMerge(PriceList priceList,
			PriceListCreate creationContainer) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer, priceList);

		return update;
	}

	public PriceList updatePriceList(PriceListUpdate updateContainer,
												 SecurityContext securityContext) {
		PriceList priceList = updateContainer.getPriceList();
		if (updatePriceListNoMerge(priceList, updateContainer)) {
			repository.merge(priceList);
		}
		return priceList;
	}

	public void validate(PriceListCreate creationContainer,
                         SecurityContext securityContext) {
		baseclassNewService.validate(creationContainer, securityContext);
	}
}