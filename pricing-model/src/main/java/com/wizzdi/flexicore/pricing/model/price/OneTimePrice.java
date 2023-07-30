package com.wizzdi.flexicore.pricing.model.price;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class OneTimePrice extends Price {

    @ManyToOne(targetEntity = Frequency.class)
    private Frequency frequency;
    @ManyToOne(targetEntity = PricingScheme.class)
    private PricingScheme pricingScheme;


    @ManyToOne(targetEntity = Frequency.class)

    public Frequency getFrequency() {
        return frequency;
    }

    public <T extends OneTimePrice> T setFrequency(Frequency frequency) {
        this.frequency = frequency;
        return (T) this;
    }

    @ManyToOne(targetEntity = PricingScheme.class)
    public PricingScheme getPricingScheme() {
        return pricingScheme;
    }

    public <T extends OneTimePrice> T setPricingScheme(PricingScheme pricingScheme) {
        this.pricingScheme = pricingScheme;
        return (T) this;
    }
}
