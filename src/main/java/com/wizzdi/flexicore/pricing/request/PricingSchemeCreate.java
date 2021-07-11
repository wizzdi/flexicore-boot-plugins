package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Money;
import com.wizzdi.flexicore.pricing.model.price.PricingModel;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class PricingSchemeCreate extends BasicCreate {

    private PricingModel pricingModel;
    private String fixedPriceId;
    @JsonIgnore
    private Money fixedPrice;

    public PricingModel getPricingModel() {
        return pricingModel;
    }

    public <T extends PricingSchemeCreate> T setPricingModel(PricingModel pricingModel) {
        this.pricingModel = pricingModel;
        return (T) this;
    }

    public String getFixedPriceId() {
        return fixedPriceId;
    }

    public <T extends PricingSchemeCreate> T setFixedPriceId(String fixedPriceId) {
        this.fixedPriceId = fixedPriceId;
        return (T) this;
    }

    @JsonIgnore
    public Money getFixedPrice() {
        return fixedPrice;
    }

    public <T extends PricingSchemeCreate> T setFixedPrice(Money fixedPrice) {
        this.fixedPrice = fixedPrice;
        return (T) this;
    }
}
