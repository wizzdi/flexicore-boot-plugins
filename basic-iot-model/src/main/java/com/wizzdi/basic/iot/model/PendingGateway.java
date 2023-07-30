package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;

import jakarta.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "pending_gateway_idx",columnList = "gatewayId,registeredGateway_id")
})
public class PendingGateway extends SecuredBasic {

    @Lob
    private String gatewayId;
    @Lob
    private String publicKey;
    @OneToOne(targetEntity = Gateway.class)
    private Gateway registeredGateway;
    private boolean noSignatureCapabilities;

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

    public boolean isNoSignatureCapabilities() {
        return noSignatureCapabilities;
    }

    public <T extends PendingGateway> T setNoSignatureCapabilities(boolean noSignatureCapabilities) {
        this.noSignatureCapabilities = noSignatureCapabilities;
        return (T) this;
    }
}
