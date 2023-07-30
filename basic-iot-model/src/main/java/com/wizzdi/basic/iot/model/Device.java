package com.wizzdi.basic.iot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

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
