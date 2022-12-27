package com.wizzdi.basic.iot.client;

import java.util.HashSet;
import java.util.Set;

public class KeepAlive extends IOTMessage{

    private Set<String> deviceIds=new HashSet<>();


    public Set<String> getDeviceIds() {
        return deviceIds;
    }

    public <T extends KeepAlive> T setDeviceIds(Set<String> deviceIds) {
        this.deviceIds = deviceIds;
        return (T) this;
    }

    @Override
    public String toString() {
        return "KeepAlive{" +
                "gatewayId=" + getGatewayId()+
                "deviceIds=" + deviceIds +
                "} " ;
    }
}
