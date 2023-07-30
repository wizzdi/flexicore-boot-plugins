package com.wizzdi.maps.service;

import com.wizzdi.maps.service.request.GeoHashRequest;

public class ExtendedGeoHashRequest extends GeoHashRequest {

    private LightFilter lightFilter;

    public LightFilter getLightFilter() {
        return lightFilter;
    }

    public <T extends ExtendedGeoHashRequest> T setLightFilter(LightFilter lightFilter) {
        this.lightFilter = lightFilter;
        return (T) this;
    }
}
