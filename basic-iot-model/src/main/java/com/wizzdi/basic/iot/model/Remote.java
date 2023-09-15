package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(indexes = {
        @Index(name = "remote_idx",columnList = "remoteId,lastSeen,gateway_id,dtype")
})
public class Remote extends SecuredBasic {

    @ManyToOne(targetEntity = ConnectivityChange.class)
    private ConnectivityChange lastConnectivityChange;

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime lastSeen;

    @Column(unique = true)
    private String remoteId;
    private String version;
    @ManyToOne(targetEntity = MappedPOI.class)
    private MappedPOI mappedPOI;

    @ManyToOne(targetEntity = MapIcon.class)
    @JsonIgnore
    private MapIcon preConnectivityLossIcon;

    @ManyToOne(targetEntity = StateSchema.class)
    private StateSchema currentSchema;

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> deviceProperties = new HashMap<>();

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> userAddedProperties = new HashMap<>();
    private boolean lockLocation;

    private boolean lockName;

    private boolean keepStateHistory;

    private Double reportedLat;
    private Double reportedLon;



    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    public Map<String, Object> getDeviceProperties() {
        return deviceProperties;
    }



    public <T extends Remote> T setDeviceProperties(Map<String, Object> other) {
        this.deviceProperties = other;
        return (T) this;
    }

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    public Map<String, Object> getUserAddedProperties() {
        return userAddedProperties;
    }

    public <T extends Remote> T setUserAddedProperties(Map<String, Object> userAddedProperties) {
        this.userAddedProperties = userAddedProperties;
        return (T) this;
    }

    @ManyToOne(targetEntity = ConnectivityChange.class)
    public ConnectivityChange getLastConnectivityChange() {
        return lastConnectivityChange;
    }

    public <T extends Remote> T setLastConnectivityChange(ConnectivityChange lastConnectivityChange) {
        this.lastConnectivityChange = lastConnectivityChange;
        return (T) this;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public <T extends Remote> T setRemoteId(String remoteId) {
        this.remoteId = remoteId;
        return (T) this;
    }

    public String getVersion() {
        return version;
    }

    public <T extends Remote> T setVersion(String version) {
        this.version = version;
        return (T) this;
    }

    @ManyToOne(targetEntity = StateSchema.class)
    public StateSchema getCurrentSchema() {
        return currentSchema;
    }

    public <T extends Remote> T setCurrentSchema(StateSchema stateSchema) {
        this.currentSchema = stateSchema;
        return (T) this;
    }

    @ManyToOne(targetEntity = MappedPOI.class)
    public MappedPOI getMappedPOI() {
        return mappedPOI;
    }

    public <T extends Remote> T setMappedPOI(MappedPOI mappedPOI) {
        this.mappedPOI = mappedPOI;
        return (T) this;
    }

    public boolean isLockLocation() {
        return lockLocation;
    }

    public <T extends Remote> T setLockLocation(boolean lockLocation) {
        this.lockLocation = lockLocation;
        return (T) this;
    }
    @Column(columnDefinition = "timestamp with time zone")

    public OffsetDateTime getLastSeen() {
        return lastSeen;
    }

    public <T extends Remote> T setLastSeen(OffsetDateTime lastKeepAlive) {
        this.lastSeen = lastKeepAlive;
        return (T) this;
    }

    @ManyToOne(targetEntity = MapIcon.class)
    @JsonIgnore
    public MapIcon getPreConnectivityLossIcon() {
        return preConnectivityLossIcon;
    }

    public <T extends Remote> T setPreConnectivityLossIcon(MapIcon preConnectivityLossIcon) {
        this.preConnectivityLossIcon = preConnectivityLossIcon;
        return (T) this;
    }

    public boolean isLockName() {
        return lockName;
    }

    public <T extends Remote> T setLockName(boolean lockName) {
        this.lockName = lockName;
        return (T) this;
    }

    public boolean isKeepStateHistory() {
        return keepStateHistory;
    }

    public <T extends Remote> T setKeepStateHistory(boolean keepStateHistory) {
        this.keepStateHistory = keepStateHistory;
        return (T) this;
    }

    public Double getReportedLat() {
        return reportedLat;
    }

    public <T extends Remote> T setReportedLat(Double reportedLat) {
        this.reportedLat = reportedLat;
        return (T) this;
    }

    public Double getReportedLon() {
        return reportedLon;
    }

    public <T extends Remote> T setReportedLon(Double reportedLon) {
        this.reportedLon = reportedLon;
        return (T) this;
    }
}
