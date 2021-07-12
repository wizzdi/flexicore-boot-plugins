package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Customer;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethod;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

public class ContractCreate extends BasicCreate {


    @JsonIgnore
    private Customer customer;
    private String customerId;
    private String automaticPaymentMethodId;
    @JsonIgnore
    private PaymentMethod automaticPaymentMethod;



    @JsonIgnore
    public Customer getCustomer() {
        return customer;
    }

    public <T extends ContractCreate> T setCustomer(Customer customer) {
        this.customer = customer;
        return (T) this;
    }

    public String getCustomerId() {
        return customerId;
    }

    public <T extends ContractCreate> T setCustomerId(String customerId) {
        this.customerId = customerId;
        return (T) this;
    }

    public String getAutomaticPaymentMethodId() {
        return automaticPaymentMethodId;
    }

    public <T extends ContractCreate> T setAutomaticPaymentMethodId(String automaticPaymentMethodId) {
        this.automaticPaymentMethodId = automaticPaymentMethodId;
        return (T) this;
    }

    @JsonIgnore
    public PaymentMethod getAutomaticPaymentMethod() {
        return automaticPaymentMethod;
    }

    public <T extends ContractCreate> T setAutomaticPaymentMethod(PaymentMethod automaticPaymentMethod) {
        this.automaticPaymentMethod = automaticPaymentMethod;
        return (T) this;
    }
}
