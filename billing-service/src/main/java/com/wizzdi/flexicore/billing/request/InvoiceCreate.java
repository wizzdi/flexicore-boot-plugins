package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.payment.Receipt;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

public class InvoiceCreate extends BasicCreate {


    @JsonIgnore
    private Receipt receipt;
    @JsonIgnore
    private OffsetDateTime invoiceDate;
    private String invoiceReference;


    @JsonIgnore
    public Receipt getReceipt() {
        return receipt;
    }

    public <T extends InvoiceCreate> T setReceipt(Receipt receipt) {
        this.receipt = receipt;
        return (T) this;
    }

    @JsonIgnore
    public OffsetDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public <T extends InvoiceCreate> T setInvoiceDate(OffsetDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
        return (T) this;
    }

    public String getInvoiceReference() {
        return invoiceReference;
    }

    public <T extends InvoiceCreate> T setInvoiceReference(String invoiceReference) {
        this.invoiceReference = invoiceReference;
        return (T) this;
    }
}
