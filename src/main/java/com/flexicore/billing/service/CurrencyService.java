package com.flexicore.billing.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.data.CurrencyRepository;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.request.CurrencyCreate;
import com.flexicore.billing.request.CurrencyFiltering;
import com.flexicore.billing.request.CurrencyUpdate;
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
public class CurrencyService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private CurrencyRepository repository;

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

	public void validateFiltering(CurrencyFiltering filtering,
								  SecurityContext securityContext) {
		baseclassNewService.validateFilter(filtering, securityContext);
	}

	public PaginationResponse<Currency> getAllCurrencies(
			SecurityContext securityContext, CurrencyFiltering filtering) {
		List<Currency> list = repository.getAllCurrencies(securityContext, filtering);
		long count = repository.countAllCurrencies(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public Currency createCurrency(CurrencyCreate creationContainer,
												 SecurityContext securityContext) {
		Currency currency = createCurrencyNoMerge(creationContainer, securityContext);
		repository.merge(currency);
		return currency;
	}

	private Currency createCurrencyNoMerge(CurrencyCreate creationContainer,
                                       SecurityContext securityContext) {
		Currency currency = new Currency(creationContainer.getName(),securityContext);
		updateCurrencyNoMerge(currency, creationContainer);
		return currency;
	}

	private boolean updateCurrencyNoMerge(Currency currency,
			CurrencyCreate creationContainer) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer, currency);

		return update;
	}

	public Currency updateCurrency(CurrencyUpdate updateContainer,
												 SecurityContext securityContext) {
		Currency currency = updateContainer.getCurrency();
		if (updateCurrencyNoMerge(currency, updateContainer)) {
			repository.merge(currency);
		}
		return currency;
	}

	public void validate(CurrencyCreate creationContainer,
                         SecurityContext securityContext) {
		baseclassNewService.validate(creationContainer, securityContext);
	}
}