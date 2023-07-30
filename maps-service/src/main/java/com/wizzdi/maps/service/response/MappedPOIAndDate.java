package com.wizzdi.maps.service.response;

import com.wizzdi.maps.model.MappedPOI;

import java.time.OffsetDateTime;

public class MappedPOIAndDate {

    private MappedPOI mappedPOI;
    private OffsetDateTime date;


    public MappedPOI getMappedPOI() {
        return mappedPOI;
    }

    public <T extends MappedPOIAndDate> T setMappedPOI(MappedPOI mappedPOI) {
        this.mappedPOI = mappedPOI;
        return (T) this;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public <T extends MappedPOIAndDate> T setDate(OffsetDateTime date) {
        this.date = date;
        return (T) this;
    }
}
