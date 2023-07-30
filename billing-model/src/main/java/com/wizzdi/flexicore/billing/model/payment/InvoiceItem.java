package com.wizzdi.flexicore.billing.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.billing.Charge;
import com.flexicore.model.SecuredBasic;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class InvoiceItem extends SecuredBasic {

    @ManyToOne(targetEntity = Invoice.class)
    private Invoice invoice;
    @JsonIgnore
    @OneToOne(targetEntity = Charge.class,mappedBy = "invoiceItem")
    private Charge charge;


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


    @JsonIgnore
    @OneToOne(targetEntity = Charge.class,mappedBy = "invoiceItem")
    public Charge getCharge() {
        return charge;
    }

    public <T extends InvoiceItem> T setCharge(Charge charge) {
        this.charge = charge;
        return (T) this;
    }
}
