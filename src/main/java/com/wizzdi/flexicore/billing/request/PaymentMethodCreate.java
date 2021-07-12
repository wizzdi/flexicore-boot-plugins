package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType;
import com.flexicore.organization.model.Customer;
import com.wizzdi.flexicore.security.request.BasicCreate;


public class PaymentMethodCreate extends BasicCreate {

    private String customerId;
    @JsonIgnore
    private Customer customer;
    private String paymentMethodTypeId;
    @JsonIgnore
    private PaymentMethodType paymentMethodType;
    private Boolean active;


    public String getCustomerId() {
        return customerId;
    }

    public <T extends PaymentMethodCreate> T setCustomerId(String customerId) {
        this.customerId = customerId;
        return (T) this;
    }

    @JsonIgnore
    public Customer getCustomer() {
        return customer;
    }

    public <T extends PaymentMethodCreate> T setCustomer(Customer customer) {
        this.customer = customer;
        return (T) this;
    }

    public String getPaymentMethodTypeId() {
        return paymentMethodTypeId;
    }

    public <T extends PaymentMethodCreate> T setPaymentMethodTypeId(String paymentMethodTypeId) {
        this.paymentMethodTypeId = paymentMethodTypeId;
        return (T) this;
    }

    @JsonIgnore
    public PaymentMethodType getPaymentMethodType() {
        return paymentMethodType;
    }

    public <T extends PaymentMethodCreate> T setPaymentMethodType(PaymentMethodType paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
        return (T) this;
    }

    public Boolean getActive() {
        return active;
    }

    public <T extends PaymentMethodCreate> T setActive(Boolean active) {
        this.active = active;
        return (T) this;
    }
}
