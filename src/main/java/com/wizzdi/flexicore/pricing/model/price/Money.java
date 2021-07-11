package com.wizzdi.flexicore.pricing.model.price;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Money extends SecuredBasic {

    @ManyToOne(targetEntity = Currency.class)
    private Currency currency;
    private long cents;

    public Currency getCurrency() {
        return currency;
    }

    public <T extends Money> T setCurrency(Currency currency) {
        this.currency = currency;
        return (T) this;
    }

    public long getCents() {
        return cents;
    }

    public <T extends Money> T setCents(long value) {
        this.cents = value;
        return (T) this;
    }
}
