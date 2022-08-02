package com.wizzdi.basic.iot.service;

public class ClientIdHolder {

    private final String gatewayId;

    public ClientIdHolder(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public String getDeviceId(){
        return gatewayId +"DEVICE";
    }
}
