package com.wizzdi.basic.iot.client;

import java.util.HashSet;
import java.util.Set;

public class KeepAlive extends IOTMessage{

    private String gatewayId;
    private Set<String> deviceIds=new HashSet<>();

    public String getGatewayId() {
        return gatewayId;
    }

    public <T extends KeepAlive> T setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
        return (T) this;
    }

    public Set<String> getDeviceIds() {
        return deviceIds;
    }

    public <T extends KeepAlive> T setDeviceIds(Set<String> deviceIds) {
        this.deviceIds = deviceIds;
        return (T) this;
    }
}
