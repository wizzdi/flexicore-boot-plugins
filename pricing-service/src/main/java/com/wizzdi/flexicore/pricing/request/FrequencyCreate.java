package com.wizzdi.flexicore.pricing.request;

import com.wizzdi.flexicore.pricing.model.price.IntervalUnit;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class FrequencyCreate extends BasicCreate {

    private IntervalUnit intervalUnit;
    private Integer intervalCount;

    public IntervalUnit getIntervalUnit() {
        return intervalUnit;
    }

    public <T extends FrequencyCreate> T setIntervalUnit(IntervalUnit intervalUnit) {
        this.intervalUnit = intervalUnit;
        return (T) this;
    }

    public Integer getIntervalCount() {
        return intervalCount;
    }

    public <T extends FrequencyCreate> T setIntervalCount(Integer intervalCount) {
        this.intervalCount = intervalCount;
        return (T) this;
    }
}
