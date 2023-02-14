package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.StateSchema;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.maps.model.MappedPOI;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class RemoteCreate extends BasicCreate {

    @JsonIgnore
    private Map<String, Object> deviceProperties = new HashMap<>();
    private Map<String, Object> userAddedProperties = new HashMap<>();

    private String remoteId;
    private String version;

    @JsonIgnore
    private StateSchema currentSchema;
    @JsonIgnore
    private MappedPOI mappedPOI;
    private Boolean lockLocation;
    @JsonIgnore
    private OffsetDateTime lastSeen;

    public String getRemoteId() {
        return remoteId;
    }

    public <T extends RemoteCreate> T setRemoteId(String remoteId) {
        this.remoteId = remoteId;
        return (T) this;
    }

    @JsonIgnore
    public Map<String, Object> getDeviceProperties() {
        return deviceProperties;
    }
    public <T extends RemoteCreate> T setDeviceProperties(Map<String, Object> deviceProperties) {
        this.deviceProperties = deviceProperties;
        return (T) this;
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

    @JsonIgnore
    public MappedPOI getMappedPOI() {
        return mappedPOI;
    }

    public <T extends RemoteCreate> T setMappedPOI(MappedPOI mappedPOI) {
        this.mappedPOI = mappedPOI;
        return (T) this;
    }

    public Boolean getLockLocation() {
        return lockLocation;
    }

    public <T extends RemoteCreate> T setLockLocation(Boolean lockLocation) {
        this.lockLocation = lockLocation;
        return (T) this;
    }

    public Map<String, Object> getUserAddedProperties() {
        return userAddedProperties;
    }

    public <T extends RemoteCreate> T setUserAddedProperties(Map<String, Object> userAddedProperties) {
        this.userAddedProperties = userAddedProperties;
        return (T) this;
    }

    @JsonIgnore
    public OffsetDateTime getLastSeen() {
        return lastSeen;
    }

    public <T extends RemoteCreate> T setLastSeen(OffsetDateTime lastSeen) {
        this.lastSeen = lastSeen;
        return (T) this;
    }
}
