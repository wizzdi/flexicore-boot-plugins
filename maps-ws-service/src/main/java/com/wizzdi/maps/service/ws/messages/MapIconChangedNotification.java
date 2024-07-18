package com.wizzdi.maps.service.ws.messages;

public class MapIconChangedNotification extends MappedPOINotification {

    private String mappedIconId;
    private Double lat;
    private Double lon;

    public String getMappedIconId() {
        return mappedIconId;
    }

    public <T extends MapIconChangedNotification> T setMappedIconId(String mappedIconId) {
        this.mappedIconId = mappedIconId;
        return (T) this;
    }

    public Double getLat() {
        return lat;
    }

    public <T extends MapIconChangedNotification> T setLat(Double lat) {
        this.lat = lat;
        return (T) this;
    }

    public Double getLon() {
        return lon;
    }

    public <T extends MapIconChangedNotification> T setLon(Double lon) {
        this.lon = lon;
        return (T) this;
    }
}
