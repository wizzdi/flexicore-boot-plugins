package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Device;

import java.util.HashMap;
import java.util.Map;

public class ChangeStateRequest {

    private DeviceFilter deviceFilter;
    private long retries;
    private boolean async;

    private final Map<String,Object> otherProperties =new HashMap<>();



    @JsonAnyGetter
    public Map<String,Object> getValues(){
        return otherProperties;
    }

    @JsonAnySetter
    public <T extends ChangeStateRequest> T setValue(String key,Object value){
        otherProperties.put(key,value);
        return (T) this;
    }

    public DeviceFilter getDeviceFilter() {
        return deviceFilter;
    }

    public <T extends ChangeStateRequest> T setDeviceFilter(DeviceFilter deviceFilter) {
        this.deviceFilter = deviceFilter;
        return (T) this;
    }

    public long getRetries() {
        return retries;
    }

    public <T extends ChangeStateRequest> T setRetries(long retries) {
        this.retries = retries;
        return (T) this;
    }

    public boolean isAsync() {
        return async;
    }

    public <T extends ChangeStateRequest> T setAsync(boolean async) {
        this.async = async;
        return (T) this;
    }
}
