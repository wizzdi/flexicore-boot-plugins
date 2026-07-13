package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote;

public class RemoteGroupToRemoteUpdate extends RemoteGroupToRemoteCreate {
    private String id;
    @JsonIgnore
    private RemoteGroupToRemote remoteGroupToRemote;

    public String getId() { return id; }
    public <T extends RemoteGroupToRemoteUpdate> T setId(String id) { this.id = id; return (T) this; }
    public RemoteGroupToRemote getRemoteGroupToRemote() { return remoteGroupToRemote; }
    public <T extends RemoteGroupToRemoteUpdate> T setRemoteGroupToRemote(RemoteGroupToRemote remoteGroupToRemote) { this.remoteGroupToRemote = remoteGroupToRemote; return (T) this; }
}
