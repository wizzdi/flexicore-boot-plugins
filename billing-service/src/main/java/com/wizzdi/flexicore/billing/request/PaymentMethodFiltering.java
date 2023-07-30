package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.flexicore.organization.model.Customer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PaymentMethodFiltering extends PaginationFilter {
private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> paymentMethodTypeIds=new HashSet<>();
    @JsonIgnore
    private List<PaymentMethodType> paymentMethodTypes;
    private Set<String> customerIds=new HashSet<>();
    @JsonIgnore
    private List<Customer> customers;
    private Boolean active;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends PaymentMethodFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

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
