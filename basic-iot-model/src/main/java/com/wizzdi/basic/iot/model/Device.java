package com.wizzdi.basic.iot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.time.OffsetDateTime;

@Entity
public class Device extends Remote {

    @ManyToOne(targetEntity = Gateway.class)
    private Gateway gateway;

    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType deviceType;

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime verifiedAt;



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

    public OffsetDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public <T extends Device> T setVerifiedAt(OffsetDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
        return (T) this;
    }
}
