package com.wizzdi.basic.iot.service.response;

public class GatewayLocationResolution {

    private Double lat;
    private Double lon;
    private Double accuracyMeters;
    private String source;

    public Double getLat() {
        return lat;
    }

    public GatewayLocationResolution setLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public Double getLon() {
        return lon;
    }

    public GatewayLocationResolution setLon(Double lon) {
        this.lon = lon;
        return this;
    }

    public Double getAccuracyMeters() {
        return accuracyMeters;
    }

    public GatewayLocationResolution setAccuracyMeters(Double accuracyMeters) {
        this.accuracyMeters = accuracyMeters;
        return this;
    }

    public String getSource() {
        return source;
    }

    public GatewayLocationResolution setSource(String source) {
        this.source = source;
        return this;
    }
}
