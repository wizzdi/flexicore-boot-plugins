package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.RemoteHealthProfile;

public class RemoteHealthProfileUpdate extends RemoteHealthProfileCreate {
    private String id;
    @JsonIgnore
    private RemoteHealthProfile remoteHealthProfile;

    public String getId() { return id; }
    public <T extends RemoteHealthProfileUpdate> T setId(String id) { this.id = id; return (T) this; }
    public RemoteHealthProfile getRemoteHealthProfile() { return remoteHealthProfile; }
    public <T extends RemoteHealthProfileUpdate> T setRemoteHealthProfile(RemoteHealthProfile remoteHealthProfile) { this.remoteHealthProfile = remoteHealthProfile; return (T) this; }
}
