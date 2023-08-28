package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class StateHistoryCreate extends BasicCreate {

    @JsonIgnore
    private Map<String, Object> deviceProperties = new HashMap<>();
    @JsonIgnore
    private Map<String, Object> userAddedProperties = new HashMap<>();
    @JsonIgnore
    private Remote remote;
    @JsonIgnore
    private OffsetDateTime timeAtState;


    @JsonIgnore
    public Map<String, Object> getDeviceProperties() {
        return deviceProperties;
    }

    public <T extends StateHistoryCreate> T setDeviceProperties(Map<String, Object> deviceProperties) {
        this.deviceProperties = deviceProperties;
        return (T) this;
    }

    @JsonIgnore
    public Map<String, Object> getUserAddedProperties() {
        return userAddedProperties;
    }

    public <T extends StateHistoryCreate> T setUserAddedProperties(Map<String, Object> userAddedProperties) {
        this.userAddedProperties = userAddedProperties;
        return (T) this;
    }

    @JsonIgnore
    public Remote getRemote() {
        return remote;
    }

    public <T extends StateHistoryCreate> T setRemote(Remote remote) {
        this.remote = remote;
        return (T) this;
    }

    public OffsetDateTime getTimeAtState() {
        return timeAtState;
    }

    public <T extends StateHistoryCreate> T setTimeAtState(OffsetDateTime timeAtState) {
        this.timeAtState = timeAtState;
        return (T) this;
    }
}
