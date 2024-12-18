package com.wizzdi.flexicore.billing.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Receipt extends Baseclass {

    @ManyToOne(targetEntity = Payment.class)
    private Payment payment;
    private String receiptReference;

    @JsonIgnore
    @OneToMany(targetEntity = Invoice.class,mappedBy = "receipt")
    private List<Invoice> invoices =new ArrayList<>();

    @ManyToOne(targetEntity = Payment.class)
    public Payment getPayment() {
        return payment;
    }

    public <T extends Receipt> T setPayment(Payment payment) {
        this.payment = payment;
        return (T) this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = Invoice.class,mappedBy = "receipt")
    public List<Invoice> getInvoices() {
        return invoices;
    }

    public <T extends Receipt> T setInvoices(List<Invoice> invoice) {
        this.invoices = invoice;
        return (T) this;
    }

    public String getReceiptReference() {
        return receiptReference;
    }

    public <T extends Receipt> T setReceiptReference(String externalId) {
        this.receiptReference = externalId;
        return (T) this;
    }
}
