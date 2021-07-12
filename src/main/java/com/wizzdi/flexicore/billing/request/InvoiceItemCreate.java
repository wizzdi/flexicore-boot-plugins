package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.billing.Charge;
import com.wizzdi.flexicore.billing.model.payment.Invoice;
import com.wizzdi.flexicore.security.request.BasicCreate;


public class InvoiceItemCreate extends BasicCreate {

    private String invoiceId;
    @JsonIgnore
    private Invoice invoice;
    @JsonIgnore
    private Charge charge;
    private String chargeId;

    public String getInvoiceId() {
        return invoiceId;
    }

    public <T extends InvoiceItemCreate> T setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
        return (T) this;
    }

    @JsonIgnore
    public Invoice getInvoice() {
        return invoice;
    }

    public <T extends InvoiceItemCreate> T setInvoice(Invoice invoice) {
        this.invoice = invoice;
        return (T) this;
    }



    @JsonIgnore
    public Charge getCharge() {
        return charge;
    }

    public <T extends InvoiceItemCreate> T setCharge(Charge charge) {
        this.charge = charge;
        return (T) this;
    }

    public String getChargeId() {
        return chargeId;
    }

    public <T extends InvoiceItemCreate> T setChargeId(String chargeId) {
        this.chargeId = chargeId;
        return (T) this;
    }
}
