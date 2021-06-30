package com.flexicore.billing.model;

import com.flexicore.model.SecuredBasic;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.lang.annotation.Target;
import java.time.OffsetDateTime;

@Entity
public class InvoiceItem extends SecuredBasic {

    @ManyToOne(targetEntity = Invoice.class)
    private Invoice invoice;
    @ManyToOne(targetEntity = ContractItem.class)
    private ContractItem contractItem;
    @ManyToOne(targetEntity = Payment.class)
    private Payment payment;


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

    @ManyToOne(targetEntity = Payment.class)
    public Payment getPayment() {
        return payment;
    }

    public <T extends InvoiceItem> T setPayment(Payment payment) {
        this.payment = payment;
        return (T) this;
    }
}
