package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Connectivity;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

public class ConnectivityChangeCreate extends BasicCreate {

    private String remoteId;
    @JsonIgnore
    private Remote remote;
    private Connectivity connectivity;
    private OffsetDateTime date;

    public String getRemoteId() {
        return remoteId;
    }

    public <T extends ConnectivityChangeCreate> T setRemoteId(String remoteId) {
        this.remoteId = remoteId;
        return (T) this;
    }

    @JsonIgnore
    public Remote getRemote() {
        return remote;
    }

    public <T extends ConnectivityChangeCreate> T setRemote(Remote remote) {
        this.remote = remote;
        return (T) this;
    }

    public Connectivity getConnectivity() {
        return connectivity;
    }

    public <T extends ConnectivityChangeCreate> T setConnectivity(Connectivity connectivity) {
        this.connectivity = connectivity;
        return (T) this;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public <T extends ConnectivityChangeCreate> T setDate(OffsetDateTime date) {
        this.date = date;
        return (T) this;
    }
}
