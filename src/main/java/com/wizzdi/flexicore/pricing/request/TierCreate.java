package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Money;
import com.wizzdi.flexicore.pricing.model.price.PricingScheme;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class TierCreate extends BasicCreate {

    private Integer startingQuantity;
    private Integer endingQuantity;
    private String moneyId;
    @JsonIgnore
    private Money money;
    private String pricingSchemeId;
    @JsonIgnore
    private PricingScheme pricingScheme;

    public Integer getStartingQuantity() {
        return startingQuantity;
    }

    public <T extends TierCreate> T setStartingQuantity(Integer startingQuantity) {
        this.startingQuantity = startingQuantity;
        return (T) this;
    }

    public Integer getEndingQuantity() {
        return endingQuantity;
    }

    public <T extends TierCreate> T setEndingQuantity(Integer endingQuantity) {
        this.endingQuantity = endingQuantity;
        return (T) this;
    }

    public String getMoneyId() {
        return moneyId;
    }

    public <T extends TierCreate> T setMoneyId(String moneyId) {
        this.moneyId = moneyId;
        return (T) this;
    }

    @JsonIgnore
    public Money getMoney() {
        return money;
    }

    public <T extends TierCreate> T setMoney(Money money) {
        this.money = money;
        return (T) this;
    }

    public String getPricingSchemeId() {
        return pricingSchemeId;
    }

    public <T extends TierCreate> T setPricingSchemeId(String pricingSchemeId) {
        this.pricingSchemeId = pricingSchemeId;
        return (T) this;
    }

    @JsonIgnore
    public PricingScheme getPricingScheme() {
        return pricingScheme;
    }

    public <T extends TierCreate> T setPricingScheme(PricingScheme pricingScheme) {
        this.pricingScheme = pricingScheme;
        return (T) this;
    }
}
