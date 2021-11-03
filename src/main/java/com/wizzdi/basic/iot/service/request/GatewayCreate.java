package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecurityUser;

public class GatewayCreate extends RemoteCreate {

    private String publicKey;
    @JsonIgnore
    private SecurityUser approvingUser;
    @JsonIgnore
    private SecurityUser gatewayUser;

    public String getPublicKey() {
        return publicKey;
    }

    public <T extends GatewayCreate> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    @JsonIgnore
    public SecurityUser getApprovingUser() {
        return approvingUser;
    }

    public <T extends GatewayCreate> T setApprovingUser(SecurityUser approvingUser) {
        this.approvingUser = approvingUser;
        return (T) this;
    }

    @JsonIgnore
    public SecurityUser getGatewayUser() {
        return gatewayUser;
    }

    public <T extends GatewayCreate> T setGatewayUser(SecurityUser gatewayUser) {
        this.gatewayUser = gatewayUser;
        return (T) this;
    }
}
