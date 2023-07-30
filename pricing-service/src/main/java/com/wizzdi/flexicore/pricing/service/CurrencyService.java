package com.wizzdi.flexicore.pricing.service;


import com.wizzdi.flexicore.pricing.data.CurrencyRepository;
import com.wizzdi.flexicore.pricing.model.price.Currency;
import com.wizzdi.flexicore.pricing.request.CurrencyCreate;
import com.wizzdi.flexicore.pricing.request.CurrencyFiltering;
import com.wizzdi.flexicore.pricing.request.CurrencyUpdate;
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

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;

@Extension
@Component

public class CurrencyService implements Plugin {

		@Autowired
	private CurrencyRepository repository;

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

	public void validateFiltering(CurrencyFiltering filtering,
                                  SecurityContextBase securityContext) {
		basicService.validate(filtering, securityContext);
	}

	public PaginationResponse<Currency> getAllCurrencies(
			SecurityContextBase securityContext, CurrencyFiltering filtering) {
		List<Currency> list = listAllCurrencies(securityContext, filtering);
		long count = repository.countAllCurrencies(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public List<Currency> listAllCurrencies(SecurityContextBase securityContext, CurrencyFiltering filtering) {
		return repository.getAllCurrencies(securityContext, filtering);
	}

	public Currency createCurrency(CurrencyCreate creationContainer,
                                   SecurityContextBase securityContext) {
		Currency currency = createCurrencyNoMerge(creationContainer, securityContext);
		repository.merge(currency);
		return currency;
	}

	private Currency createCurrencyNoMerge(CurrencyCreate creationContainer,
                                       SecurityContextBase securityContext) {
		Currency currency = new Currency();
		currency.setId(Baseclass.getBase64ID());

		updateCurrencyNoMerge(currency, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(currency,securityContext);
		return currency;
	}

	private boolean updateCurrencyNoMerge(Currency currency,
			CurrencyCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, currency);

		return update;
	}

	public Currency updateCurrency(CurrencyUpdate updateContainer,
                                   SecurityContextBase securityContext) {
		Currency currency = updateContainer.getCurrency();
		if (updateCurrencyNoMerge(currency, updateContainer)) {
			repository.merge(currency);
		}
		return currency;
	}

	public void validate(CurrencyCreate creationContainer,
                         SecurityContextBase securityContext) {
		basicService.validate(creationContainer, securityContext);
	}
}