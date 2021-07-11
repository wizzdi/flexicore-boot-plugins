package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecurringPriceEntryFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> recurringPriceIds=new HashSet<>();
    @JsonIgnore
    private List<RecurringPrice> recurringPrices;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends RecurringPriceEntryFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getRecurringPriceIds() {
        return recurringPriceIds;
    }

    public <T extends RecurringPriceEntryFiltering> T setRecurringPriceIds(Set<String> recurringPriceIds) {
        this.recurringPriceIds = recurringPriceIds;
        return (T) this;
    }

    @JsonIgnore
    public List<RecurringPrice> getRecurringPrices() {
        return recurringPrices;
    }

    public <T extends RecurringPriceEntryFiltering> T setRecurringPrices(List<RecurringPrice> recurringPrices) {
        this.recurringPrices = recurringPrices;
        return (T) this;
    }
}
