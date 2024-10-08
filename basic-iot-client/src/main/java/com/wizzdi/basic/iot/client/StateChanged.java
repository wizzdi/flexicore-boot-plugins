package com.wizzdi.basic.iot.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class StateChanged extends IOTMessage{
    private String deviceId;
    private Map<String,Object> otherProperties =new HashMap<>();
    private String deviceType;
    @JsonAlias({"FWversion"})
    private String version;
    @JsonAlias({"Longitude","lon","Lon"})
    private Double longitude;
    @JsonAlias({"Latitude","lat","Lat"})
    private Double latitude;
    private String buildingId;
    private String floorId;
    private String roomId;
    private Double roomXOffset;
    private Double roomYOffset;
    private Double roomZOffset;
    private String status;

    public StateChanged() {
    }

    public StateChanged(StateChanged other) {
        super(other);
        this.deviceId = other.deviceId;
        this.otherProperties = other.otherProperties;
        this.deviceType = other.deviceType;
        this.version = other.version;
        this.longitude = other.longitude;
        this.latitude = other.latitude;
        this.buildingId = other.buildingId;
        this.floorId = other.floorId;
        this.roomId = other.roomId;
        this.roomXOffset = other.roomXOffset;
        this.roomYOffset = other.roomYOffset;
        this.roomZOffset = other.roomZOffset;
        this.status = other.status;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public <T extends StateChanged> T setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return (T) this;
    }

    @JsonAnyGetter
    public Map<String,Object> getValues(){
        return otherProperties;
    }


    @JsonAnySetter
    public <T extends StateChanged> T setValue(String key,Object value){
        otherProperties.put(key,value);
        return (T) this;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public <T extends StateChanged> T setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }

    public <T extends StateChanged> T setOtherProperties(Map<String, Object> otherProperties) {
        this.otherProperties = otherProperties;
        return (T) this;
    }

    public String getVersion() {
        return version;
    }

    public <T extends StateChanged> T setVersion(String version) {
        this.version = version;
        return (T) this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public <T extends StateChanged> T setLongitude(Double longitude) {
        this.longitude = longitude;
        return (T) this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public <T extends StateChanged> T setLatitude(Double latitude) {
        this.latitude = latitude;
        return (T) this;
    }

    public String getStatus() {
        return status;
    }

    public <T extends StateChanged> T setStatus(String status) {
        this.status = status;
        return (T) this;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public <T extends StateChanged> T setBuildingId(String buildingId) {
        this.buildingId = buildingId;
        return (T) this;
    }

    public String getFloorId() {
        return floorId;
    }

    public <T extends StateChanged> T setFloorId(String floorId) {
        this.floorId = floorId;
        return (T) this;
    }

    public String getRoomId() {
        return roomId;
    }

    public <T extends StateChanged> T setRoomId(String roomId) {
        this.roomId = roomId;
        return (T) this;
    }

    public Double getRoomXOffset() {
        return roomXOffset;
    }

    public <T extends StateChanged> T setRoomXOffset(Double roomXOffset) {
        this.roomXOffset = roomXOffset;
        return (T) this;
    }

    public Double getRoomYOffset() {
        return roomYOffset;
    }

    public <T extends StateChanged> T setRoomYOffset(Double roomYOffset) {
        this.roomYOffset = roomYOffset;
        return (T) this;
    }

    public Double getRoomZOffset() {
        return roomZOffset;
    }

    public <T extends StateChanged> T setRoomZOffset(Double roomZOffset) {
        this.roomZOffset = roomZOffset;
        return (T) this;
    }

    @Override
    public String toString() {
        return "StateChanged{" +
                "deviceId='" + deviceId + '\'' +
                ", otherProperties=" + otherProperties +
                ", version=" + version +
                ", deviceType='" + deviceType + '\'' +
                "} " + super.toString();
    }
}
