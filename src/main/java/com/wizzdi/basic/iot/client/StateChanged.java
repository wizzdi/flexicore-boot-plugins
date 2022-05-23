package com.wizzdi.basic.iot.client;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class StateChanged extends IOTMessage{
    private String deviceId;
    private Map<String,Object> otherProperties =new HashMap<>();
    private String deviceType;


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


    @Override
    public String toString() {
        return "StateChanged{" +
                "deviceId='" + deviceId + '\'' +
                ", otherProperties=" + otherProperties +
                ", deviceType='" + deviceType + '\'' +
                "} " + super.toString();
    }
}
