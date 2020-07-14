package com.flexicore.billing.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class InvoiceItem extends Baseclass {

    @ManyToOne(targetEntity = Invoice.class)
    private Invoice invoice;
    @ManyToOne(targetEntity = ContractItem.class)
    private ContractItem contractItem;

    public InvoiceItem() {
    }

    public InvoiceItem(String name, SecurityContext securityContext) {
        super(name, securityContext);
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
}
