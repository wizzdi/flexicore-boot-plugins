package com.wizzdi.flexicore.pricing.model.price;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RecurringPrice extends Price {

    @JsonIgnore
    @OneToMany(targetEntity = BillingCycle.class,mappedBy = "recurringPrice")
    private List<BillingCycle> billingCycles=new ArrayList<>();

    @JsonIgnore
    @OneToMany(targetEntity = BillingCycle.class,mappedBy = "recurringPrice")
    public List<BillingCycle> getBillingCycles() {
        return billingCycles;
    }

    public <T extends RecurringPrice> T setBillingCycles(List<BillingCycle> billingCycles) {
        this.billingCycles = billingCycles;
        return (T) this;
    }
}
