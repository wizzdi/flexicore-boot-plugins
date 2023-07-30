package com.wizzdi.maps.service;

import com.wizzdi.maps.service.request.MappedPOIFilter;

public class ExtendedMappedPOIFilter extends MappedPOIFilter {


    private LightFilter lightFilter;

    public LightFilter getLightFilter() {
        return lightFilter;
    }

    public <T extends ExtendedMappedPOIFilter> T setLightFilter(LightFilter lightFilter) {
        this.lightFilter = lightFilter;
        return (T) this;
    }
}
