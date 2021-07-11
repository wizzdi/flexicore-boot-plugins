package com.wizzdi.flexicore.pricing.model.price;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class BillingCycle extends SecuredBasic {

    @ManyToOne(targetEntity = PricingScheme.class)
    private PricingScheme pricingScheme;
    private int sequence;
    private int totalCycles;
    private TenureType tenureType;
    @ManyToOne(targetEntity = Frequency.class)
    private Frequency frequency;
    @ManyToOne(targetEntity = RecurringPrice.class)
    private RecurringPrice recurringPrice;

    @ManyToOne(targetEntity = PricingScheme.class)
    public PricingScheme getPricingScheme() {
        return pricingScheme;
    }

    public <T extends BillingCycle> T setPricingScheme(PricingScheme pricingScheme) {
        this.pricingScheme = pricingScheme;
        return (T) this;
    }

    public int getSequence() {
        return sequence;
    }

    public <T extends BillingCycle> T setSequence(int sequence) {
        this.sequence = sequence;
        return (T) this;
    }

    public int getTotalCycles() {
        return totalCycles;
    }

    public <T extends BillingCycle> T setTotalCycles(int totalCycles) {
        this.totalCycles = totalCycles;
        return (T) this;
    }

    public TenureType getTenureType() {
        return tenureType;
    }

    public <T extends BillingCycle> T setTenureType(TenureType tenureType) {
        this.tenureType = tenureType;
        return (T) this;
    }

    @ManyToOne(targetEntity = Frequency.class)
    public Frequency getFrequency() {
        return frequency;
    }

    public <T extends BillingCycle> T setFrequency(Frequency frequency) {
        this.frequency = frequency;
        return (T) this;
    }

    @ManyToOne(targetEntity = RecurringPrice.class)
    public RecurringPrice getRecurringPrice() {
        return recurringPrice;
    }

    public <T extends BillingCycle> T setRecurringPrice(RecurringPrice recurringPrice) {
        this.recurringPrice = recurringPrice;
        return (T) this;
    }
}
