package com.wizzdi.basic.iot.client;

public class ConnectMessage extends IOTMessage{
    private String gatewayId;


    public String getGatewayId() {
        return gatewayId;
    }

    public <T extends ConnectMessage> T setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
        return (T) this;
    }
}
