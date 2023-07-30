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
    private String version;
    @JsonAlias({"Longitude","lon","Lon"})
    private Double longitude;
    @JsonAlias({"Latitude","lat","Lat"})

    private Double latitude;
    private String status;


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
