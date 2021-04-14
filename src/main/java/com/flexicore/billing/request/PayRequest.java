package com.flexicore.billing.request;

import com.flexicore.billing.model.Currency;
import com.flexicore.billing.model.Invoice;
import com.flexicore.billing.model.InvoiceItem;
import com.flexicore.billing.model.PaymentMethod;

import java.util.List;

public class PayRequest {

    private List<InvoiceItem> invoiceItem;
    private Invoice invoice;
    private PaymentMethod paymentMethod;
    private Currency currency;
    private long cents;

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public <T extends PayRequest> T setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return (T) this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public <T extends PayRequest> T setCurrency(Currency currency) {
        this.currency = currency;
        return (T) this;
    }

    public long getCents() {
        return cents;
    }

    public <T extends PayRequest> T setCents(long cents) {
        this.cents = cents;
        return (T) this;
    }

    public List<InvoiceItem> getInvoiceItem() {
        return invoiceItem;
    }

    public <T extends PayRequest> T setInvoiceItem(List<InvoiceItem> invoiceItem) {
        this.invoiceItem = invoiceItem;
        return (T) this;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public <T extends PayRequest> T setInvoice(Invoice invoice) {
        this.invoice = invoice;
        return (T) this;
    }
}
