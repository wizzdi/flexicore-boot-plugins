package com.wizzdi.flexicore.pricing.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.data.PriceRepository;
import com.wizzdi.flexicore.pricing.model.price.Price;
import com.wizzdi.flexicore.pricing.request.PriceCreate;
import com.wizzdi.flexicore.pricing.request.PriceFiltering;
import com.wizzdi.flexicore.pricing.request.PriceUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Extension
@Component

public class PriceService implements Plugin {

		@Autowired
	private PriceRepository repository;

	@Autowired
	private BasicService basicService;


	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}


	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
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

	public void validateFiltering(PriceFiltering filtering,
                                  SecurityContext securityContext) {
		basicService.validate(filtering, securityContext);
	}

	public PaginationResponse<Price> getAllPrice(
			SecurityContext securityContext, PriceFiltering filtering) {
		List<Price> list = listAllPrice(securityContext, filtering);
		long count = repository.countAllPrice(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public List<Price> listAllPrice(SecurityContext securityContext, PriceFiltering filtering) {
		return repository.getAllPrice(securityContext, filtering);
	}

	public Price createPrice(PriceCreate creationContainer,
                                   SecurityContext securityContext) {
		Price price = createPriceNoMerge(creationContainer, securityContext);
		repository.merge(price);
		return price;
	}

	public Price createPriceNoMerge(PriceCreate creationContainer,
                                       SecurityContext securityContext) {
		Price price = new Price();
		price.setId(UUID.randomUUID().toString());

		updatePriceNoMerge(price, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(price,securityContext);
		return price;
	}

	public boolean updatePriceNoMerge(Price price,
			PriceCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, price);

		return update;
	}

	public Price updatePrice(PriceUpdate updateContainer,
                                   SecurityContext securityContext) {
		Price price = updateContainer.getPrice();
		if (updatePriceNoMerge(price, updateContainer)) {
			repository.merge(price);
		}
		return price;
	}

	public void validate(PriceCreate creationContainer,
                         SecurityContext securityContext) {
		basicService.validate(creationContainer, securityContext);
	}
}
