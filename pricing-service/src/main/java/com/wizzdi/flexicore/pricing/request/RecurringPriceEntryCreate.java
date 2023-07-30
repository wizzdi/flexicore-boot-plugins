package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Frequency;
import com.wizzdi.flexicore.pricing.model.price.PricingScheme;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice;
import com.wizzdi.flexicore.pricing.model.price.TenureType;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class RecurringPriceEntryCreate extends BasicCreate {

    @JsonIgnore
    private PricingScheme pricingScheme;
    private String pricingSchemeId;
    private Integer sequence;
    private Integer totalCycles;
    private TenureType tenureType;
    @JsonIgnore
    private Frequency frequency;
    private String frequencyId;
    @JsonIgnore
    private RecurringPrice recurringPrice;
    private String recurringPriceId;


    @JsonIgnore
    public PricingScheme getPricingScheme() {
        return pricingScheme;
    }

    public <T extends RecurringPriceEntryCreate> T setPricingScheme(PricingScheme pricingScheme) {
        this.pricingScheme = pricingScheme;
        return (T) this;
    }

    public String getPricingSchemeId() {
        return pricingSchemeId;
    }

    public <T extends RecurringPriceEntryCreate> T setPricingSchemeId(String pricingSchemeId) {
        this.pricingSchemeId = pricingSchemeId;
        return (T) this;
    }

    public Integer getSequence() {
        return sequence;
    }

    public <T extends RecurringPriceEntryCreate> T setSequence(Integer sequence) {
        this.sequence = sequence;
        return (T) this;
    }

    public Integer getTotalCycles() {
        return totalCycles;
    }

    public <T extends RecurringPriceEntryCreate> T setTotalCycles(Integer totalCycles) {
        this.totalCycles = totalCycles;
        return (T) this;
    }

    public TenureType getTenureType() {
        return tenureType;
    }

    public <T extends RecurringPriceEntryCreate> T setTenureType(TenureType tenureType) {
        this.tenureType = tenureType;
        return (T) this;
    }

    @JsonIgnore
    public Frequency getFrequency() {
        return frequency;
    }

    public <T extends RecurringPriceEntryCreate> T setFrequency(Frequency frequency) {
        this.frequency = frequency;
        return (T) this;
    }

    public String getFrequencyId() {
        return frequencyId;
    }

    public <T extends RecurringPriceEntryCreate> T setFrequencyId(String frequencyId) {
        this.frequencyId = frequencyId;
        return (T) this;
    }

    @JsonIgnore
    public RecurringPrice getRecurringPrice() {
        return recurringPrice;
    }

    public <T extends RecurringPriceEntryCreate> T setRecurringPrice(RecurringPrice recurringPrice) {
        this.recurringPrice = recurringPrice;
        return (T) this;
    }

    public String getRecurringPriceId() {
        return recurringPriceId;
    }

    public <T extends RecurringPriceEntryCreate> T setRecurringPriceId(String recurringPriceId) {
        this.recurringPriceId = recurringPriceId;
        return (T) this;
    }
}
