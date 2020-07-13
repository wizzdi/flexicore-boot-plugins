package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.PaymentMethod;
import com.flexicore.model.FilteringInformationHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvoiceFiltering extends FilteringInformationHolder {

    private Set<String> paymentMethodIds=new HashSet<>();
    @JsonIgnore
    private List<PaymentMethod> paymentMethods;

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
}
