package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.converters.JsonConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Device extends Remote {

    @ManyToOne(targetEntity = Gateway.class)
    private Gateway gateway;

    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType deviceType;



    @ManyToOne(targetEntity = Gateway.class)
    public Gateway getGateway() {
        return gateway;
    }

    public <T extends Device> T setGateway(Gateway gateway) {
        this.gateway = gateway;
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
