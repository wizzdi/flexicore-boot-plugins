package com.wizzdi.maps.service.request;

public class GeoHashRequest {
    private MappedPOIFilter mappedPOIFilter;
    private Integer precision;

    public MappedPOIFilter getMappedPOIFilter() {
        return mappedPOIFilter;
    }

    public <T extends GeoHashRequest> T setMappedPOIFilter(MappedPOIFilter mappedPOIFilter) {
        this.mappedPOIFilter = mappedPOIFilter;
        return (T) this;
    }

    public Integer getPrecision() {
        return precision;
    }

    public <T extends GeoHashRequest> T setPrecision(Integer precision) {
        this.precision = precision;
        return (T) this;
    }
}
