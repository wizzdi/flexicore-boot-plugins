package com.wizzdi.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.billing.ChargeReference;
import com.wizzdi.flexicore.billing.model.payment.InvoiceItem;
import com.wizzdi.flexicore.pricing.model.price.Money;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

public class ChargeCreate extends BasicCreate {


    @JsonIgnore
    private Money money;
    private String moneyId;
    private OffsetDateTime chargeDate;
    @JsonIgnore
    private ChargeReference chargeReference;
    private String chargeReferenceId;
    @JsonIgnore
    private InvoiceItem invoiceItem;
    private String invoiceItemId;


    public OffsetDateTime getChargeDate() {
        return chargeDate;
    }

    public <T extends ChargeCreate> T setChargeDate(OffsetDateTime chargeDate) {
        this.chargeDate = chargeDate;
        return (T) this;
    }


    public String getMoneyId() {
        return moneyId;
    }

    public <T extends ChargeCreate> T setMoneyId(String moneyId) {
        this.moneyId = moneyId;
        return (T) this;
    }

    @JsonIgnore
    public Money getMoney() {
        return money;
    }

    public <T extends ChargeCreate> T setMoney(Money money) {
        this.money = money;
        return (T) this;
    }

    @JsonIgnore
    public ChargeReference getChargeReference() {
        return chargeReference;
    }

    public <T extends ChargeCreate> T setChargeReference(ChargeReference chargeReference) {
        this.chargeReference = chargeReference;
        return (T) this;
    }

    public String getChargeReferenceId() {
        return chargeReferenceId;
    }

    public <T extends ChargeCreate> T setChargeReferenceId(String chargeReferenceId) {
        this.chargeReferenceId = chargeReferenceId;
        return (T) this;
    }

    @JsonIgnore
    public InvoiceItem getInvoiceItem() {
        return invoiceItem;
    }

    public <T extends ChargeCreate> T setInvoiceItem(InvoiceItem invoiceItem) {
        this.invoiceItem = invoiceItem;
        return (T) this;
    }

    public String getInvoiceItemId() {
        return invoiceItemId;
    }

    public <T extends ChargeCreate> T setInvoiceItemId(String invoiceItemId) {
        this.invoiceItemId = invoiceItemId;
        return (T) this;
    }
}
