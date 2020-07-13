package com.flexicore.billing.request;

import com.flexicore.model.FilteringInformationHolder;

import java.util.Set;

public class PaymentMethodTypeFiltering extends FilteringInformationHolder {

    private Set<String> canonicalClassNames;


    public Set<String> getCanonicalClassNames() {
        return canonicalClassNames;
    }

    public <T extends PaymentMethodTypeFiltering> T setCanonicalClassNames(Set<String> canonicalClassNames) {
        this.canonicalClassNames = canonicalClassNames;
        return (T) this;
    }
}
