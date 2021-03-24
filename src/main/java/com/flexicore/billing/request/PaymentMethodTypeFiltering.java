package com.flexicore.billing.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.Set;

public class PaymentMethodTypeFiltering extends PaginationFilter {
private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> canonicalClassNames;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends PaymentMethodTypeFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getCanonicalClassNames() {
        return canonicalClassNames;
    }

    public <T extends PaymentMethodTypeFiltering> T setCanonicalClassNames(Set<String> canonicalClassNames) {
        this.canonicalClassNames = canonicalClassNames;
        return (T) this;
    }
}
