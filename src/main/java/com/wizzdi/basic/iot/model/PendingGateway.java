package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

@Entity
public class PendingGateway extends SecuredBasic {

    @Lob
    private String gatewayId;
    @Lob
    private String publicKey;
    @OneToOne(targetEntity = Gateway.class)
    private Gateway registeredGateway;

    public String getGatewayId() {
        return gatewayId;
    }

    public <T extends PendingGateway> T setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
        return (T) this;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public <T extends PendingGateway> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    @OneToOne(targetEntity = Gateway.class)
    public Gateway getRegisteredGateway() {
        return registeredGateway;
    }

    public <T extends PendingGateway> T setRegisteredGateway(Gateway registeredGateway) {
        this.registeredGateway = registeredGateway;
        return (T) this;
    }
}
