package com.wizzdi.maps.service.ws.messages;

public class MapIconChangedNotification extends MappedPOINotification {

    private String mappedIconId;

    public String getMappedIconId() {
        return mappedIconId;
    }

    public <T extends MapIconChangedNotification> T setMappedIconId(String mappedIconId) {
        this.mappedIconId = mappedIconId;
        return (T) this;
    }
}
