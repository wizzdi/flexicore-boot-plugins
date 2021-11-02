package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Remote extends SecuredBasic {

    @ManyToOne(targetEntity = ConnectivityChange.class)
    private ConnectivityChange lastConnectivityChange;
    private String remoteId;

    @ManyToOne(targetEntity = ConnectivityChange.class)
    public ConnectivityChange getLastConnectivityChange() {
        return lastConnectivityChange;
    }

    public <T extends Remote> T setLastConnectivityChange(ConnectivityChange lastConnectivityChange) {
        this.lastConnectivityChange = lastConnectivityChange;
        return (T) this;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public <T extends Remote> T setRemoteId(String remoteId) {
        this.remoteId = remoteId;
        return (T) this;
    }
}
