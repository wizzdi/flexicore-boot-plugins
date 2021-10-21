package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Remote;


public class RemoteUpdate extends RemoteCreate{

    private String id;
    @JsonIgnore
    private Remote remote;


    public String getId() {
        return id;
    }

    public <T extends RemoteUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public Remote getRemote() {
        return remote;
    }

    public <T extends RemoteUpdate> T setRemote(Remote remote) {
        this.remote = remote;
        return (T) this;
    }
}
