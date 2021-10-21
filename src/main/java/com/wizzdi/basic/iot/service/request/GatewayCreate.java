package com.wizzdi.basic.iot.service.request;

public class GatewayCreate extends RemoteCreate {

    private String publicKey;

    public String getPublicKey() {
        return publicKey;
    }

    public <T extends GatewayCreate> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }
}
