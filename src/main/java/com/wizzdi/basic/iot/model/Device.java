package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.dynamic.properties.converter.DynamicColumnDefinition;
import com.wizzdi.dynamic.properties.converter.JsonConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Device extends Remote {

    @ManyToOne(targetEntity = Gateway.class)
    private Gateway gateway;
    @Convert(converter = JsonConverter.class)
    @JsonIgnore
    private Map<String, Object> other = new HashMap<>();
    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType deviceType;

    @JsonAnySetter
    public void set(String key, Object val) {
        other.put(key, val);
    }


    @DynamicColumnDefinition
    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    @JsonIgnore
    public Map<String, Object> getOther() {
        return other;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return other;
    }

    @ManyToOne(targetEntity = Gateway.class)
    public Gateway getGateway() {
        return gateway;
    }

    public <T extends Device> T setGateway(Gateway gateway) {
        this.gateway = gateway;
        return (T) this;
    }

    public <T extends Device> T setOther(Map<String, Object> other) {
        this.other = other;
        return (T) this;
    }

    @ManyToOne(targetEntity = DeviceType.class)
    public DeviceType getDeviceType() {
        return deviceType;
    }

    public <T extends Device> T setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }
}
