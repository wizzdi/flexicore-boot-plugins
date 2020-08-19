package com.flexicore.billing.request;

import com.flexicore.billing.model.Currency;
import com.flexicore.billing.model.PaymentMethod;

public class PayRequest {

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
}
