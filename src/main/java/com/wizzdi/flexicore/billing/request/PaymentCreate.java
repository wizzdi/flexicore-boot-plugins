package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.wizzdi.flexicore.pricing.model.price.Money;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

public class PaymentCreate extends BasicCreate {


    @JsonIgnore
    private Money money;
    private String moneyId;
    private OffsetDateTime datePaid;
    private String paymentReference;


    public OffsetDateTime getDatePaid() {
        return datePaid;
    }

    public <T extends PaymentCreate> T setDatePaid(OffsetDateTime datePaid) {
        this.datePaid = datePaid;
        return (T) this;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public <T extends PaymentCreate> T setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
        return (T) this;
    }

    public String getMoneyId() {
        return moneyId;
    }

    public <T extends PaymentCreate> T setMoneyId(String moneyId) {
        this.moneyId = moneyId;
        return (T) this;
    }

    @JsonIgnore
    public Money getMoney() {
        return money;
    }

    public <T extends PaymentCreate> T setMoney(Money money) {
        this.money = money;
        return (T) this;
    }
}
