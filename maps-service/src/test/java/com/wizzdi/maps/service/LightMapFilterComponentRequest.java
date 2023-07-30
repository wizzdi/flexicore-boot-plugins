package com.wizzdi.maps.service;

import com.wizzdi.maps.service.request.MapFilterComponentRequest;

public class LightMapFilterComponentRequest extends MapFilterComponentRequest {

    private LightFilter lightFilter;

    public LightFilter getLightFilter() {
        return lightFilter;
    }

    public <T extends LightMapFilterComponentRequest> T setLightFilter(LightFilter lightFilter) {
        this.lightFilter = lightFilter;
        return (T) this;
    }
}
