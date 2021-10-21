package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class RemoteCreate extends BasicCreate {

   private String remoteId;

    public String getRemoteId() {
        return remoteId;
    }

    public <T extends RemoteCreate> T setRemoteId(String remoteId) {
        this.remoteId = remoteId;
        return (T) this;
    }
}
