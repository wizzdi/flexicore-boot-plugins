package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Frequency;
import com.wizzdi.flexicore.pricing.model.price.PricingScheme;

public class OneTimePriceCreate extends PriceCreate {

    private String frequencyId;
    @JsonIgnore
    private Frequency frequency;
    private String pricingSchemeId;
    @JsonIgnore
    private PricingScheme pricingScheme;

    public String getFrequencyId() {
        return frequencyId;
    }

    public <T extends OneTimePriceCreate> T setFrequencyId(String frequencyId) {
        this.frequencyId = frequencyId;
        return (T) this;
    }

    @JsonIgnore
    public Frequency getFrequency() {
        return frequency;
    }

    public <T extends OneTimePriceCreate> T setFrequency(Frequency frequency) {
        this.frequency = frequency;
        return (T) this;
    }

    public String getPricingSchemeId() {
        return pricingSchemeId;
    }

    public <T extends OneTimePriceCreate> T setPricingSchemeId(String pricingSchemeId) {
        this.pricingSchemeId = pricingSchemeId;
        return (T) this;
    }

    @JsonIgnore
    public PricingScheme getPricingScheme() {
        return pricingScheme;
    }

    public <T extends OneTimePriceCreate> T setPricingScheme(PricingScheme pricingScheme) {
        this.pricingScheme = pricingScheme;
        return (T) this;
    }
}
