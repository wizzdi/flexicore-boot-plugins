package com.wizzdi.flexicore.billing.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Invoice extends SecuredBasic {


    @JsonIgnore
    @OneToMany(targetEntity = InvoiceItem.class,mappedBy = "invoice")
    private List<InvoiceItem> invoiceItems=new ArrayList<>();

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime invoiceDate;
    @ManyToOne(targetEntity = Receipt.class)
    private Receipt receipt;
    private String invoiceReference;

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



    public OffsetDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public <T extends Invoice> T setInvoiceDate(OffsetDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
        return (T) this;
    }

    @ManyToOne(targetEntity = Receipt.class)
    public Receipt getReceipt() {
        return receipt;
    }

    public <T extends Invoice> T setReceipt(Receipt receipt) {
        this.receipt = receipt;
        return (T) this;
    }

    public String getInvoiceReference() {
        return invoiceReference;
    }

    public <T extends Invoice> T setInvoiceReference(String externalId) {
        this.invoiceReference = externalId;
        return (T) this;
    }
}
