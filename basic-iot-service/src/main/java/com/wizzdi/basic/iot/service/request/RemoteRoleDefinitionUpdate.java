package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.RemoteRoleDefinition;

public class RemoteRoleDefinitionUpdate extends RemoteRoleDefinitionCreate {
    private String id;
    @JsonIgnore
    private RemoteRoleDefinition remoteRoleDefinition;

    public String getId() { return id; }
    public <T extends RemoteRoleDefinitionUpdate> T setId(String id) { this.id = id; return (T) this; }
    public RemoteRoleDefinition getRemoteRoleDefinition() { return remoteRoleDefinition; }
    public <T extends RemoteRoleDefinitionUpdate> T setRemoteRoleDefinition(RemoteRoleDefinition remoteRoleDefinition) { this.remoteRoleDefinition = remoteRoleDefinition; return (T) this; }
}
