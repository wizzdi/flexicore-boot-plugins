package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Remote;
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
    private Boolean lockName;
    @JsonIgnore
    private OffsetDateTime lastSeen;

    private Boolean keepStateHistory;

    private Double reportedLat;
    private Double reportedLon;

    public RemoteCreate() {
    }

    public RemoteCreate(Remote other) {
        setName(other.getName());
        setDescription(other.getDescription());
        setUpdateDate(other.getUpdateDate());
        setSoftDelete(other.isSoftDelete());
        this.deviceProperties = deepCopy(other.getDeviceProperties());
        this.userAddedProperties = deepCopy(other.getUserAddedProperties());
        this.remoteId = other.getRemoteId();
        this.version = other.getVersion();
        this.currentSchema = other.getCurrentSchema();
        this.mappedPOI = other.getMappedPOI();
        this.lockLocation = other.isLockLocation();
        this.lockName = other.isLockName();
        this.lastSeen = other.getLastSeen();
        this.keepStateHistory = other.isKeepStateHistory();
        this.reportedLat = other.getReportedLat();
        this.reportedLon = other.getReportedLon();
    }

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

    public Boolean getLockName() {
        return lockName;
    }

    public <T extends RemoteCreate> T setLockName(Boolean lockName) {
        this.lockName = lockName;
        return (T) this;
    }

    public Boolean getKeepStateHistory() {
        return keepStateHistory;
    }

    public <T extends RemoteCreate> T setKeepStateHistory(Boolean keepStateHistory) {
        this.keepStateHistory = keepStateHistory;
        return (T) this;
    }

    public Double getReportedLat() {
        return reportedLat;
    }

    public <T extends RemoteCreate> T setReportedLat(Double reportedLat) {
        this.reportedLat = reportedLat;
        return (T) this;
    }

    public Double getReportedLon() {
        return reportedLon;
    }

    public <T extends RemoteCreate> T setReportedLon(Double reportedLon) {
        this.reportedLon = reportedLon;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> deepCopy(Map<String, Object> originalMap) {
        if(originalMap==null){
            return null;
        }
        Map<String, Object> copiedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                // Recursive deep copy if it's a nested map
                value = deepCopy((Map<String, Object>) value);
            }
            // For immutable types and primitives, the reference can be shared
            copiedMap.put(entry.getKey(), value);
        }
        return copiedMap;
    }
}
