package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.Contract;
import com.flexicore.billing.model.PaymentMethod;
import com.flexicore.organization.model.Customer;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

public class InvoiceCreate extends BasicCreate {


    @JsonIgnore
    private Customer customer;
    @JsonIgnore
    private Contract contract;
    @JsonIgnore
    private OffsetDateTime invoiceDate;


    @JsonIgnore
    public Customer getCustomer() {
        return customer;
    }

    public <T extends InvoiceCreate> T setCustomer(Customer customer) {
        this.customer = customer;
        return (T) this;
    }

    @JsonIgnore
    public Contract getContract() {
        return contract;
    }

    public <T extends InvoiceCreate> T setContract(Contract contract) {
        this.contract = contract;
        return (T) this;
    }

    @JsonIgnore
    public OffsetDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public <T extends InvoiceCreate> T setInvoiceDate(OffsetDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
        return (T) this;
    }
}
