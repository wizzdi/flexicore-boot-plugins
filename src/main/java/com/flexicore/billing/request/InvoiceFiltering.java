package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.PaymentMethod;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvoiceFiltering extends PaginationFilter {
private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> paymentMethodIds=new HashSet<>();
    @JsonIgnore
    private List<PaymentMethod> paymentMethods;
    private boolean unpaid;
    private boolean automatic;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends InvoiceFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getPaymentMethodIds() {
        return paymentMethodIds;
    }

    public <T extends InvoiceFiltering> T setPaymentMethodIds(Set<String> paymentMethodIds) {
        this.paymentMethodIds = paymentMethodIds;
        return (T) this;
    }

    @JsonIgnore
    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public <T extends InvoiceFiltering> T setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
        return (T) this;
    }

    public boolean isUnpaid() {
        return unpaid;
    }

    public <T extends InvoiceFiltering> T setUnpaid(boolean unpaid) {
        this.unpaid = unpaid;
        return (T) this;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public <T extends InvoiceFiltering> T setAutomatic(boolean automatic) {
        this.automatic = automatic;
        return (T) this;
    }
}
