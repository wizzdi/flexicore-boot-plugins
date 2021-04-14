package com.flexicore.billing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.flexicore.organization.model.Customer;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Invoice extends SecuredBasic {

    @ManyToOne(targetEntity = PaymentMethod.class)
    private PaymentMethod usedPaymentMethod;
    @JsonIgnore
    @OneToMany(targetEntity = InvoiceItem.class,mappedBy = "invoice")
    private List<InvoiceItem> invoiceItems=new ArrayList<>();
    @ManyToOne(targetEntity = Contract.class)
    private Contract contract;
    private OffsetDateTime invoiceDate;

    public Invoice() {
    }


    @JsonIgnore
    @OneToMany(targetEntity = InvoiceItem.class,mappedBy = "invoice")
    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public <T extends Invoice> T setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
        return (T) this;
    }

    @ManyToOne(targetEntity = PaymentMethod.class)
    public PaymentMethod getUsedPaymentMethod() {
        return usedPaymentMethod;
    }

    public <T extends Invoice> T setUsedPaymentMethod(PaymentMethod usedPaymentMethod) {
        this.usedPaymentMethod = usedPaymentMethod;
        return (T) this;
    }


    public OffsetDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public <T extends Invoice> T setInvoiceDate(OffsetDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
        return (T) this;
    }

    @ManyToOne(targetEntity = Contract.class)
    public Contract getContract() {
        return contract;
    }

    public <T extends Invoice> T setContract(Contract contract) {
        this.contract = contract;
        return (T) this;
    }
}
