package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Gateway;

import java.util.HashMap;
import java.util.Map;

public class DeviceCreate extends RemoteCreate {

    private String gatewayId;
    @JsonIgnore
    private Gateway gateway;
    private String deviceTypeId;
    @JsonIgnore
    private DeviceType deviceType;
    @JsonIgnore
    private Map<String, Object> other = new HashMap<>();

    public String getGatewayId() {
        return gatewayId;
    }

    public <T extends DeviceCreate> T setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
        return (T) this;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public <T extends DeviceCreate> T setGateway(Gateway gateway) {
        this.gateway = gateway;
        return (T) this;
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public <T extends DeviceCreate> T setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
        return (T) this;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public <T extends DeviceCreate> T setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }

    @JsonAnyGetter
    public Map<String, Object> getOther() {
        return other;
    }
    public <T extends DeviceCreate> T setOther(Map<String, Object> other) {
        this.other = other;
        return (T) this;
    }
    @JsonAnySetter
    public void set(String key, Object val) {
        other.put(key, val);
    }
}
