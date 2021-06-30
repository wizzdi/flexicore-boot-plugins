package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.BusinessService;
import com.flexicore.billing.model.Currency;
import com.flexicore.billing.model.PriceList;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PriceListToServiceFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> priceListIds = new HashSet<>();
    @JsonIgnore
    private List<PriceList> priceLists;
    private Set<String> businessServiceIds = new HashSet<>();
    @JsonIgnore
    private List<BusinessService> businessServices;
    private Set<String> currencyIds = new HashSet<>();
    @JsonIgnore
    private List<Currency> currencies;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends PriceListToServiceFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getPriceListIds() {
        return priceListIds;
    }

    public <T extends PriceListToServiceFiltering> T setPriceListIds(Set<String> priceListIds) {
        this.priceListIds = priceListIds;
        return (T) this;
    }

    @JsonIgnore
    public List<PriceList> getPriceLists() {
        return priceLists;
    }

    public <T extends PriceListToServiceFiltering> T setPriceLists(List<PriceList> priceLists) {
        this.priceLists = priceLists;
        return (T) this;
    }

    public Set<String> getBusinessServiceIds() {
        return businessServiceIds;
    }

    public <T extends PriceListToServiceFiltering> T setBusinessServiceIds(Set<String> businessServiceIds) {
        this.businessServiceIds = businessServiceIds;
        return (T) this;
    }

    @JsonIgnore
    public List<BusinessService> getBusinessServices() {
        return businessServices;
    }

    public <T extends PriceListToServiceFiltering> T setBusinessServices(List<BusinessService> businessServices) {
        this.businessServices = businessServices;
        return (T) this;
    }

    public Set<String> getCurrencyIds() {
        return currencyIds;
    }

    public <T extends PriceListToServiceFiltering> T setCurrencyIds(Set<String> currencyIds) {
        this.currencyIds = currencyIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Currency> getCurrencies() {
        return currencies;
    }

    public <T extends PriceListToServiceFiltering> T setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
        return (T) this;
    }
}
