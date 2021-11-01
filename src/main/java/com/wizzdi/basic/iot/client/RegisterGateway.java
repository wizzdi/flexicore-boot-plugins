package com.wizzdi.basic.iot.client;

public class RegisterGateway extends IOTMessage{

    private String publicKey;

    public String getPublicKey() {
        return publicKey;
    }

    public <T extends RegisterGateway> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    @Override
    public boolean isRequireAuthentication() {
        return false;
    }
}
