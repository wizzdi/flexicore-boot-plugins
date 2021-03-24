package com.flexicore.billing.service;


import com.flexicore.billing.data.PriceListRepository;
import com.flexicore.billing.model.PriceList;
import com.flexicore.billing.request.PriceListCreate;
import com.flexicore.billing.request.PriceListFiltering;
import com.flexicore.billing.request.PriceListUpdate;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class PriceListService implements Plugin {

		@Autowired
	private PriceListRepository repository;

	@Autowired
	private BasicService basicService;

public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return repository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return repository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return repository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		repository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		repository.massMerge(toMerge);
	}

	public void validateFiltering(PriceListFiltering filtering,
								  SecurityContextBase securityContext) {
		basicService.validate(filtering, securityContext);
	}

	public PaginationResponse<PriceList> getAllPriceLists(
			SecurityContextBase securityContext, PriceListFiltering filtering) {
		List<PriceList> list = repository.getAllPriceLists(securityContext, filtering);
		long count = repository.countAllPriceLists(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public PriceList createPriceList(PriceListCreate creationContainer,
												 SecurityContextBase securityContext) {
		PriceList priceList = createPriceListNoMerge(creationContainer, securityContext);
		repository.merge(priceList);
		return priceList;
	}

	private PriceList createPriceListNoMerge(PriceListCreate creationContainer,
                                       SecurityContextBase securityContext) {
		PriceList priceList = new PriceList();
		priceList.setId(Baseclass.getBase64ID());

		updatePriceListNoMerge(priceList, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(priceList,securityContext);

		return priceList;
	}

	private boolean updatePriceListNoMerge(PriceList priceList,
			PriceListCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, priceList);

		return update;
	}

	public PriceList updatePriceList(PriceListUpdate updateContainer,
												 SecurityContextBase securityContext) {
		PriceList priceList = updateContainer.getPriceList();
		if (updatePriceListNoMerge(priceList, updateContainer)) {
			repository.merge(priceList);
		}
		return priceList;
	}

	public void validate(PriceListCreate creationContainer,
                         SecurityContextBase securityContext) {
		basicService.validate(creationContainer, securityContext);
	}
}