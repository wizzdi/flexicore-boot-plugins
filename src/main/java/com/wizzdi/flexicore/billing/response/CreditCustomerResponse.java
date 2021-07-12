package com.wizzdi.flexicore.billing.response;

import com.wizzdi.flexicore.billing.model.payment.InvoiceItem;
import com.wizzdi.flexicore.billing.model.payment.Payment;

import java.util.List;

public class CreditCustomerResponse {

    private List<InvoiceItem> matchedInvoiceItems;
    private List<InvoiceItem> unmatchedInvoiceItems;
    private List<Payment> matchedPayments;
    private List<Payment> unmatchedPayments;

    public List<InvoiceItem> getMatchedInvoiceItems() {
        return matchedInvoiceItems;
    }

    public <T extends CreditCustomerResponse> T setMatchedInvoiceItems(List<InvoiceItem> matchedInvoiceItems) {
        this.matchedInvoiceItems = matchedInvoiceItems;
        return (T) this;
    }

    public List<InvoiceItem> getUnmatchedInvoiceItems() {
        return unmatchedInvoiceItems;
    }

    public <T extends CreditCustomerResponse> T setUnmatchedInvoiceItems(List<InvoiceItem> unmatchedInvoiceItems) {
        this.unmatchedInvoiceItems = unmatchedInvoiceItems;
        return (T) this;
    }

    public List<Payment> getMatchedPayments() {
        return matchedPayments;
    }

    public <T extends CreditCustomerResponse> T setMatchedPayments(List<Payment> matchedPayments) {
        this.matchedPayments = matchedPayments;
        return (T) this;
    }

    public List<Payment> getUnmatchedPayments() {
        return unmatchedPayments;
    }

    public <T extends CreditCustomerResponse> T setUnmatchedPayments(List<Payment> unmatchedPayments) {
        this.unmatchedPayments = unmatchedPayments;
        return (T) this;
    }
}
