package com.flexicore.billing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Invoice extends Baseclass {

    @ManyToOne(targetEntity = PaymentMethod.class)
    private PaymentMethod usedPaymentMethod;
    @JsonIgnore
    @OneToMany(targetEntity = InvoiceItem.class,mappedBy = "invoice")
    private List<InvoiceItem> invoiceItems=new ArrayList<>();

    public Invoice() {
    }

    public Invoice(String name, SecurityContext securityContext) {
        super(name, securityContext);
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
}
