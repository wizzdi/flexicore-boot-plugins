package com.flexicore.billing.model;

import com.flexicore.model.SecuredBasic;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;

@Entity
public class InvoiceItem extends SecuredBasic {

    @ManyToOne(targetEntity = Invoice.class)
    private Invoice invoice;
    @ManyToOne(targetEntity = ContractItem.class)
    private ContractItem contractItem;
    private OffsetDateTime datePaid;
    private String paymentReference;

    public InvoiceItem() {
    }


    @ManyToOne(targetEntity = Invoice.class)
    public Invoice getInvoice() {
        return invoice;
    }

    public <T extends InvoiceItem> T setInvoice(Invoice invoice) {
        this.invoice = invoice;
        return (T) this;
    }

    @ManyToOne(targetEntity = ContractItem.class)
    public ContractItem getContractItem() {
        return contractItem;
    }

    public <T extends InvoiceItem> T setContractItem(ContractItem contractItem) {
        this.contractItem = contractItem;
        return (T) this;
    }

    public OffsetDateTime getDatePaid() {
        return datePaid;
    }

    public <T extends InvoiceItem> T setDatePaid(OffsetDateTime datePayed) {
        this.datePaid = datePayed;
        return (T) this;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public <T extends InvoiceItem> T setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
        return (T) this;
    }
}
