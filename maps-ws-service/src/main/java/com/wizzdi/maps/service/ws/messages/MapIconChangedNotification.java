package com.wizzdi.maps.service.ws.messages;

public class MapIconChangedNotification extends MappedPOINotification {

    private String mappedIconId;
    private Double lat;
    private Double lon;
    private String roomId;
    private String buildingFloorId;
    private String buildingId;
    private Double x;
    private Double y;

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

    public String getRoomId() {
        return roomId;
    }

    public <T extends MapIconChangedNotification> T setRoomId(String roomId) {
        this.roomId = roomId;
        return (T) this;
    }

    public String getBuildingFloorId() {
        return buildingFloorId;
    }

    public <T extends MapIconChangedNotification> T setBuildingFloorId(String buildingFloorId) {
        this.buildingFloorId = buildingFloorId;
        return (T) this;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public <T extends MapIconChangedNotification> T setBuildingId(String buildingId) {
        this.buildingId = buildingId;
        return (T) this;
    }

    public Double getX() {
        return x;
    }

    public <T extends MapIconChangedNotification> T setX(Double x) {
        this.x = x;
        return (T) this;
    }

    public Double getY() {
        return y;
    }

    public <T extends MapIconChangedNotification> T setY(Double y) {
        this.y = y;
        return (T) this;
    }
}
