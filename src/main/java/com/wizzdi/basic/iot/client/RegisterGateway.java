package com.wizzdi.basic.iot.client;

public class RegisterGateway extends IOTMessage{

    private String publicKey;
    private Boolean noSignatureCapabilities;

    public String getPublicKey() {
        return publicKey;
    }

    public <T extends RegisterGateway> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    public Boolean getNoSignatureCapabilities() {
        return noSignatureCapabilities;
    }

    public <T extends RegisterGateway> T setNoSignatureCapabilities(Boolean noSignatureCapabilities) {
        this.noSignatureCapabilities = noSignatureCapabilities;
        return (T) this;
    }

    @Override
    public boolean isRequireAuthentication() {
        return false;
    }


}
