package com.wizzdi.basic.iot.client;

public class RegisterGateway extends IOTMessage{

    private String gatewayId;
    private String publicKey;

    public String getPublicKey() {
        return publicKey;
    }

    public <T extends RegisterGateway> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public <T extends RegisterGateway> T setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
        return (T) this;
    }
}
