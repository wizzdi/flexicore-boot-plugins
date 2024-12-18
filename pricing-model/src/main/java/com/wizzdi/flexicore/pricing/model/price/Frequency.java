package com.wizzdi.flexicore.pricing.model.price;

import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;

@Entity
public class Frequency extends Baseclass {
    private IntervalUnit intervalUnit;
    private int intervalCount;


    public IntervalUnit getIntervalUnit() {
        return intervalUnit;
    }

    public <T extends Frequency> T setIntervalUnit(IntervalUnit intervalUnit) {
        this.intervalUnit = intervalUnit;
        return (T) this;
    }

    public int getIntervalCount() {
        return intervalCount;
    }

    public <T extends Frequency> T setIntervalCount(int intervalCount) {
        this.intervalCount = intervalCount;
        return (T) this;
    }
}
