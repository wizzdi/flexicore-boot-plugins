package com.wizzdi.maps.service.response;

import com.flexicore.model.Basic;
import com.wizzdi.maps.model.MappedPOI;

public class MappedPOIRelated {

    private MappedPOI mappedPOI;
    private Basic related;

    public MappedPOIRelated(MappedPOI mappedPOI, Basic related) {
        this.mappedPOI = mappedPOI;
        this.related = related;
    }

    public MappedPOIRelated() {
    }

    public MappedPOI getMappedPOI() {
        return mappedPOI;
    }

    public <T extends MappedPOIRelated> T setMappedPOI(MappedPOI mappedPOI) {
        this.mappedPOI = mappedPOI;
        return (T) this;
    }

    public Basic getRelated() {
        return related;
    }

    public <T extends MappedPOIRelated> T setRelated(Basic related) {
        this.related = related;
        return (T) this;
    }
}
