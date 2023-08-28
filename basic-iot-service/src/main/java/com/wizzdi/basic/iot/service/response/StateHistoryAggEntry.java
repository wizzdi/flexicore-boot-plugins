package com.wizzdi.basic.iot.service.response;

import com.wizzdi.basic.iot.model.Remote;
import jakarta.persistence.Tuple;

import java.time.OffsetDateTime;

public class StateHistoryAggEntry {

    private final OffsetDateTime time;
    private final Double value;

    private final Remote remote;


    public StateHistoryAggEntry(Tuple o) {
        this.time = o.get(0, OffsetDateTime.class);
        Number num = o.get(1, Number.class);
        this.value = num!=null?num.doubleValue():null;
        this.remote=o.getElements().size()>2?o.get(2, Remote.class):null;
    }


    public OffsetDateTime getTime() {
        return time;
    }

    public Double getValue() {
        return value;
    }

    public Remote getRemote() {
        return remote;
    }
}
