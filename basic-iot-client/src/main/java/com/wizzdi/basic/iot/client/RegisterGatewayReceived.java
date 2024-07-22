package com.wizzdi.basic.iot.client;

public class RegisterGatewayReceived extends IOTMessage {

    private String registerGatewayId;

    public String getRegisterGatewayId() {
        return registerGatewayId;
    }

    public <T extends RegisterGatewayReceived> T setRegisterGatewayId(String registerGatewayId) {
        this.registerGatewayId = registerGatewayId;
        return (T) this;
    }

    @Override
    public boolean isRetained() {
        return false;
    }
}
