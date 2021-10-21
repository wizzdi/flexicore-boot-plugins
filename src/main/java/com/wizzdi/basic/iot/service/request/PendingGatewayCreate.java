package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class PendingGatewayCreate extends BasicCreate {

    private String gatewayId;
    private String publicKey;
    private String registeredGatewayId;
    @JsonIgnore
    private Gateway registeredGateway;

    public String getGatewayId() {
        return gatewayId;
    }

    public <T extends PendingGatewayCreate> T setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
        return (T) this;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public <T extends PendingGatewayCreate> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    public String getRegisteredGatewayId() {
        return registeredGatewayId;
    }

    public <T extends PendingGatewayCreate> T setRegisteredGatewayId(String registeredGatewayId) {
        this.registeredGatewayId = registeredGatewayId;
        return (T) this;
    }

    @JsonIgnore
    public Gateway getRegisteredGateway() {
        return registeredGateway;
    }

    public <T extends PendingGatewayCreate> T setRegisteredGateway(Gateway registeredGateway) {
        this.registeredGateway = registeredGateway;
        return (T) this;
    }
}
