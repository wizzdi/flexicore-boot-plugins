package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Currency;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoneyFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> currenciesIds=new HashSet<>();
    @JsonIgnore
    private List<Currency> currencies;
    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends MoneyFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

	public Set<String> getCurrenciesIds() {
		return currenciesIds;
	}

	public <T extends MoneyFiltering> T setCurrenciesIds(Set<String> currenciesIds) {
		this.currenciesIds = currenciesIds;
		return (T) this;
	}


	@JsonIgnore
	public List<Currency> getCurrencies() {
		return currencies;
	}

	public <T extends MoneyFiltering> T setCurrencies(List<Currency> currencies) {
		this.currencies = currencies;
		return (T) this;
	}
}
