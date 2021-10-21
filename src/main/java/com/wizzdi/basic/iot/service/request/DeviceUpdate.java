package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Device;


public class DeviceUpdate extends DeviceCreate{

    private String id;
    @JsonIgnore
    private Device device;


    public String getId() {
        return id;
    }

    public <T extends DeviceUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public Device getDevice() {
        return device;
    }

    public <T extends DeviceUpdate> T setDevice(Device device) {
        this.device = device;
        return (T) this;
    }
}
