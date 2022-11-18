package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.StateSchema;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.HashMap;
import java.util.Map;

public class RemoteCreate extends BasicCreate {

    @JsonIgnore
    private Map<String, Object> other = new HashMap<>();
    private String remoteId;
    private String version;

    @JsonIgnore
    private StateSchema currentSchema;

    public String getRemoteId() {
        return remoteId;
    }

    public <T extends RemoteCreate> T setRemoteId(String remoteId) {
        this.remoteId = remoteId;
        return (T) this;
    }

    @JsonAnyGetter
    public Map<String, Object> getOther() {
        return other;
    }
    public <T extends RemoteCreate> T setOther(Map<String, Object> other) {
        this.other = other;
        return (T) this;
    }
    @JsonAnySetter
    public void set(String key, Object val) {
        other.put(key, val);
    }

    public String getVersion() {
        return version;
    }

    public <T extends RemoteCreate> T setVersion(String version) {
        this.version = version;
        return (T) this;
    }

    @JsonIgnore
    public StateSchema getCurrentSchema() {
        return currentSchema;
    }

    public <T extends RemoteCreate> T setCurrentSchema(StateSchema currentSchema) {
        this.currentSchema = currentSchema;
        return (T) this;
    }
}
