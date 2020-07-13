package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.PaymentMethodType;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.organization.model.Customer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PaymentMethodFiltering extends FilteringInformationHolder {

    private Set<String> paymentMethodTypeIds=new HashSet<>();
    @JsonIgnore
    private List<PaymentMethodType> paymentMethodTypes;
    private Set<String> customerIds=new HashSet<>();
    @JsonIgnore
    private List<Customer> customers;
    private Boolean active;

    public Set<String> getPaymentMethodTypeIds() {
        return paymentMethodTypeIds;
    }

    public <T extends PaymentMethodFiltering> T setPaymentMethodTypeIds(Set<String> paymentMethodTypeIds) {
        this.paymentMethodTypeIds = paymentMethodTypeIds;
        return (T) this;
    }

    @JsonIgnore
    public List<PaymentMethodType> getPaymentMethodTypes() {
        return paymentMethodTypes;
    }

    public <T extends PaymentMethodFiltering> T setPaymentMethodTypes(List<PaymentMethodType> paymentMethodTypes) {
        this.paymentMethodTypes = paymentMethodTypes;
        return (T) this;
    }

    public Set<String> getCustomerIds() {
        return customerIds;
    }

    public <T extends PaymentMethodFiltering> T setCustomerIds(Set<String> customerIds) {
        this.customerIds = customerIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Customer> getCustomers() {
        return customers;
    }

    public <T extends PaymentMethodFiltering> T setCustomers(List<Customer> customers) {
        this.customers = customers;
        return (T) this;
    }

    public Boolean getActive() {
        return active;
    }

    public <T extends PaymentMethodFiltering> T setActive(Boolean active) {
        this.active = active;
        return (T) this;
    }
}
