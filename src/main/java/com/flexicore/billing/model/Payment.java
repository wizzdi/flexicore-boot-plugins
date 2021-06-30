package com.flexicore.billing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Payment extends SecuredBasic {

    @JsonIgnore
    @OneToMany(targetEntity = InvoiceItem.class,mappedBy = "payment")
    private List<InvoiceItem> invoiceItems=new ArrayList<>();
    @ManyToOne(targetEntity = ContractItem.class)
    private ContractItem contractItem;
    private OffsetDateTime datePaid;
    private String paymentReference;
    private long price;
    @ManyToOne(targetEntity = Currency.class)
    private Currency currency;
    public Payment() {
    }

    @JsonIgnore
    @OneToMany(targetEntity = InvoiceItem.class,mappedBy = "payment")
    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public <T extends Payment> T setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
        return (T) this;
    }

    public long getPrice() {
        return price;
    }

    public <T extends Payment> T setPrice(long price) {
        this.price = price;
        return (T) this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public <T extends Payment> T setCurrency(Currency currency) {
        this.currency = currency;
        return (T) this;
    }

    @ManyToOne(targetEntity = ContractItem.class)
    public ContractItem getContractItem() {
        return contractItem;
    }

    public <T extends Payment> T setContractItem(ContractItem contractItem) {
        this.contractItem = contractItem;
        return (T) this;
    }

    public OffsetDateTime getDatePaid() {
        return datePaid;
    }

    public <T extends Payment> T setDatePaid(OffsetDateTime datePayed) {
        this.datePaid = datePayed;
        return (T) this;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public <T extends Payment> T setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
        return (T) this;
    }
}
