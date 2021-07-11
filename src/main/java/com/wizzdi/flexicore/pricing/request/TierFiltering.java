package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.PricingScheme;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TierFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> pricingSchemesIds = new HashSet<>();
    @JsonIgnore
    private List<PricingScheme> pricingSchemes;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends TierFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }


    public Set<String> getPricingSchemesIds() {
        return pricingSchemesIds;
    }

    public <T extends TierFiltering> T setPricingSchemesIds(Set<String> pricingSchemesIds) {
        this.pricingSchemesIds = pricingSchemesIds;
        return (T) this;
    }

    @JsonIgnore
    public List<PricingScheme> getPricingSchemes() {
        return pricingSchemes;
    }

    public <T extends TierFiltering> T setPricingSchemes(List<PricingScheme> pricingSchemes) {
        this.pricingSchemes = pricingSchemes;
        return (T) this;
    }
}
