package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Remote;

public class EvaluateRemoteHealthRequest {
    private String remoteId;
    @JsonIgnore
    private Remote remote;

    public String getRemoteId() { return remoteId; }
    public <T extends EvaluateRemoteHealthRequest> T setRemoteId(String remoteId) { this.remoteId = remoteId; return (T) this; }
    public Remote getRemote() { return remote; }
    public <T extends EvaluateRemoteHealthRequest> T setRemote(Remote remote) { this.remote = remote; return (T) this; }
}
