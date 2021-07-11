package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Currency;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class MoneyCreate extends BasicCreate {

    private String currencyId;
    @JsonIgnore
    private Currency currency;
    private Long cents;

    public String getCurrencyId() {
        return currencyId;
    }

    public <T extends MoneyCreate> T setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
        return (T) this;
    }

    @JsonIgnore
    public Currency getCurrency() {
        return currency;
    }

    public <T extends MoneyCreate> T setCurrency(Currency currency) {
        this.currency = currency;
        return (T) this;
    }

    public Long getCents() {
        return cents;
    }

    public <T extends MoneyCreate> T setCents(Long cents) {
        this.cents = cents;
        return (T) this;
    }
}
