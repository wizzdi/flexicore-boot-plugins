package com.wizzdi.basic.iot.model;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class Gateway extends Remote {

    private String gatewayId;
    @Lob
    private String publicKey;

    public String getGatewayId() {
        return gatewayId;
    }

    public <T extends Gateway> T setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
        return (T) this;
    }

    @Lob
    public String getPublicKey() {
        return publicKey;
    }

    public <T extends Gateway> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }
}
