package com.wizzdi.flexicore.billing.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class ChargeReferenceFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> chargeReferences =new HashSet<>();

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends ChargeReferenceFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getChargeReferences() {
        return chargeReferences;
    }

    public <T extends ChargeReferenceFiltering> T setChargeReferences(Set<String> chargeReferences) {
        this.chargeReferences = chargeReferences;
        return (T) this;
    }
}
