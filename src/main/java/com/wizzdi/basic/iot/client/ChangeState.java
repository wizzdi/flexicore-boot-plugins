package com.wizzdi.basic.iot.client;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class ChangeState extends IOTMessage{
    private String deviceId;
    private final Map<String,Object> otherProperties =new HashMap<>();


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
    public void setValue(String key,Object value){
        otherProperties.put(key,value);
    }
}
