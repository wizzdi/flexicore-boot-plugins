package com.flexicore.billing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.billing.model.ContractItem;
import com.flexicore.billing.model.Currency;
import com.wizzdi.flexicore.security.request.BasicCreate;

import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;

public class PaymentCreate extends BasicCreate {


    @JsonIgnore
    private ContractItem contractItem;
    private String contractItemId;
    private OffsetDateTime datePaid;
    private String paymentReference;
    private Long price;
    @JsonIgnore
    private Currency currency;
    private String currencyId;



    public ContractItem getContractItem() {
        return contractItem;
    }

    public <T extends PaymentCreate> T setContractItem(ContractItem contractItem) {
        this.contractItem = contractItem;
        return (T) this;
    }

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

    public String getContractItemId() {
        return contractItemId;
    }

    public <T extends PaymentCreate> T setContractItemId(String contractItemId) {
        this.contractItemId = contractItemId;
        return (T) this;
    }

    public Long getPrice() {
        return price;
    }

    public <T extends PaymentCreate> T setPrice(Long price) {
        this.price = price;
        return (T) this;
    }

    @JsonIgnore
    public Currency getCurrency() {
        return currency;
    }

    public <T extends PaymentCreate> T setCurrency(Currency currency) {
        this.currency = currency;
        return (T) this;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public <T extends PaymentCreate> T setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
        return (T) this;
    }
}
