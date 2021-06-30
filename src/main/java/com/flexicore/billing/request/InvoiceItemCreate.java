package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.ContractItem;
import com.flexicore.billing.model.Invoice;
import com.flexicore.billing.model.Payment;
import com.wizzdi.flexicore.security.request.BasicCreate;


public class InvoiceItemCreate extends BasicCreate {

    private String invoiceId;
    @JsonIgnore
    private Invoice invoice;
    private String contractItemId;
    @JsonIgnore
    private ContractItem contractItem;
    @JsonIgnore
    private Payment payment;
    private String paymentId;

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

    public String getContractItemId() {
        return contractItemId;
    }

    public <T extends InvoiceItemCreate> T setContractItemId(String contractItemId) {
        this.contractItemId = contractItemId;
        return (T) this;
    }

    @JsonIgnore
    public ContractItem getContractItem() {
        return contractItem;
    }

    public <T extends InvoiceItemCreate> T setContractItem(ContractItem contractItem) {
        this.contractItem = contractItem;
        return (T) this;
    }

    @JsonIgnore
    public Payment getPayment() {
        return payment;
    }

    public <T extends InvoiceItemCreate> T setPayment(Payment payment) {
        this.payment = payment;
        return (T) this;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public <T extends InvoiceItemCreate> T setPaymentId(String paymentId) {
        this.paymentId = paymentId;
        return (T) this;
    }
}
