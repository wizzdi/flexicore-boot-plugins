package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecurityUser;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Gateway extends Remote {

    @Lob
    private String publicKey;

    @JsonIgnore
    @ManyToOne(targetEntity = SecurityUser.class)
    private SecurityUser approvingUser;
    @JsonIgnore
    @ManyToOne(targetEntity = SecurityUser.class)
    private SecurityUser gatewayUser;
    private boolean noSignatureCapabilities;


    @Lob
    public String getPublicKey() {
        return publicKey;
    }

    public <T extends Gateway> T setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }


    @JsonIgnore
    @ManyToOne(targetEntity = SecurityUser.class)
    public SecurityUser getApprovingUser() {
        return approvingUser;
    }

    public <T extends Gateway> T setApprovingUser(SecurityUser approvingUser) {
        this.approvingUser = approvingUser;
        return (T) this;
    }

    @JsonIgnore
    @ManyToOne(targetEntity = SecurityUser.class)
    public SecurityUser getGatewayUser() {
        return gatewayUser;
    }

    public <T extends Gateway> T setGatewayUser(SecurityUser gatewayUser) {
        this.gatewayUser = gatewayUser;
        return (T) this;
    }

    public boolean isNoSignatureCapabilities() {
        return noSignatureCapabilities;
    }

    public <T extends Gateway> T setNoSignatureCapabilities(boolean noSignatureCapabilities) {
        this.noSignatureCapabilities = noSignatureCapabilities;
        return (T) this;
    }
}
