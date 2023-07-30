package com.wizzdi.flexicore.billing.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class PaymentFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;


    private String paymentReferenceLike;
    private Set<String> paymentReferences =new HashSet<>();

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends PaymentFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public String getPaymentReferenceLike() {
        return paymentReferenceLike;
    }

    public <T extends PaymentFiltering> T setPaymentReferenceLike(String paymentReferenceLike) {
        this.paymentReferenceLike = paymentReferenceLike;
        return (T) this;
    }

    public Set<String> getPaymentReferences() {
        return paymentReferences;
    }

    public <T extends PaymentFiltering> T setPaymentReferences(Set<String> paymentReferences) {
        this.paymentReferences = paymentReferences;
        return (T) this;
    }
}
