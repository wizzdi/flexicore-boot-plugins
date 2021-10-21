package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.DeviceType;


public class DeviceTypeUpdate extends DeviceTypeCreate{

    private String id;
    @JsonIgnore
    private DeviceType deviceType;


    public String getId() {
        return id;
    }

    public <T extends DeviceTypeUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public DeviceType getDeviceType() {
        return deviceType;
    }

    public <T extends DeviceTypeUpdate> T setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }
}
