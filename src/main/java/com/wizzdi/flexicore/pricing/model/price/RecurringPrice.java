package com.wizzdi.flexicore.pricing.model.price;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RecurringPrice extends Price {

    @JsonIgnore
    @OneToMany(targetEntity = RecurringPriceEntry.class,mappedBy = "recurringPrice")
    private List<RecurringPriceEntry> recurringPriceEntries =new ArrayList<>();


    @JsonIgnore
    @OneToMany(targetEntity = RecurringPriceEntry.class,mappedBy = "recurringPrice")
    public List<RecurringPriceEntry> getRecurringPriceEntries() {
        return recurringPriceEntries;
    }

    public <T extends RecurringPrice> T setRecurringPriceEntries(List<RecurringPriceEntry> recurringPriceEntries) {
        this.recurringPriceEntries = recurringPriceEntries;
        return (T) this;
    }
}
