package com.wizzdi.basic.iot.client;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class ChangeState extends IOTMessage{
    private String deviceId;
    private Map<String,Object> otherProperties =new HashMap<>();


    public String getDeviceId() {
        return deviceId;
    }

    public <T extends ChangeState> T setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return (T) this;
    }

    @JsonAnyGetter
    public Map<String,Object> getValues(){
        return otherProperties;
    }

    @JsonAnySetter
    public <T extends ChangeState> T setValue(String key,Object value){
        otherProperties.put(key,value);
        return (T) this;
    }

    public <T extends ChangeState> T setValues(Map<String,Object> otherProperties){
       this.otherProperties=otherProperties;
        return (T) this;
    }

    @Override
    public String toString() {
        return "ChangeState{" +
                "deviceId='" + deviceId + '\'' +
                ", otherProperties=" + otherProperties +
                "} " + super.toString();
    }
}
