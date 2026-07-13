package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.RemoteGroup;

public class RemoteGroupUpdate extends RemoteGroupCreate {
    private String id;
    @JsonIgnore
    private RemoteGroup remoteGroup;

    public String getId() { return id; }
    public <T extends RemoteGroupUpdate> T setId(String id) { this.id = id; return (T) this; }
    public RemoteGroup getRemoteGroup() { return remoteGroup; }
    public <T extends RemoteGroupUpdate> T setRemoteGroup(RemoteGroup remoteGroup) { this.remoteGroup = remoteGroup; return (T) this; }
}
