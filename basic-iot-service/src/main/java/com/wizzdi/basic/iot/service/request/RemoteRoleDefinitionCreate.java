package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class RemoteRoleDefinitionCreate extends BasicCreate {
    private String externalId;

    public String getExternalId() { return externalId; }
    public <T extends RemoteRoleDefinitionCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }
}
