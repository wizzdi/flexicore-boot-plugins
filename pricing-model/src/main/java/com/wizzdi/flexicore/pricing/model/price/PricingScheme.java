package com.wizzdi.flexicore.pricing.model.price;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PricingScheme extends SecuredBasic {

    private PricingModel pricingModel;
    @ManyToOne(targetEntity = Money.class)
    private Money fixedPrice;
    @JsonIgnore
    @OneToMany(targetEntity = Tier.class, mappedBy = "pricingScheme")
    private List<Tier> tiers = new ArrayList<>();

    public PricingScheme() {
    }

    public PricingModel getPricingModel() {
        return pricingModel;
    }

    public <T extends PricingScheme> T setPricingModel(PricingModel pricingModel) {
        this.pricingModel = pricingModel;
        return (T) this;
    }

    @OneToMany(targetEntity = Tier.class, mappedBy = "pricingScheme")
    @JsonIgnore
    public List<Tier> getTiers() {
        return tiers;
    }

    public <T extends PricingScheme> T setTiers(List<Tier> tiers) {
        this.tiers = tiers;
        return (T) this;
    }

    public Money getFixedPrice() {
        return fixedPrice;
    }

    public <T extends PricingScheme> T setFixedPrice(Money fixedPrice) {
        this.fixedPrice = fixedPrice;
        return (T) this;
    }
}
