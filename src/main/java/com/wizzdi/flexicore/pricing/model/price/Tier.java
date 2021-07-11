package com.wizzdi.flexicore.pricing.model.price;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Tier extends SecuredBasic {
    private int startingQuantity;
    private Integer endingQuantity;
    @ManyToOne(targetEntity = Money.class)
    private Money amount;
    @ManyToOne(targetEntity = PricingScheme.class)
    private PricingScheme pricingScheme;

    public int getStartingQuantity() {
        return startingQuantity;
    }

    public <T extends Tier> T setStartingQuantity(int startingQuantity) {
        this.startingQuantity = startingQuantity;
        return (T) this;
    }

    public Integer getEndingQuantity() {
        return endingQuantity;
    }

    public <T extends Tier> T setEndingQuantity(Integer endingQuantity) {
        this.endingQuantity = endingQuantity;
        return (T) this;
    }

    @ManyToOne(targetEntity = Money.class)
    public Money getAmount() {
        return amount;
    }

    public <T extends Tier> T setAmount(Money amount) {
        this.amount = amount;
        return (T) this;
    }

    @ManyToOne(targetEntity = PricingScheme.class)
    public PricingScheme getPriceListToService() {
        return pricingScheme;
    }

    public <T extends Tier> T setPriceListToService(PricingScheme pricingScheme) {
        this.pricingScheme = pricingScheme;
        return (T) this;
    }
}
