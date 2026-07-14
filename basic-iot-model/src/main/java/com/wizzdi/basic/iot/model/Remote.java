package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(indexes = {
        @Index(name = "remote_idx",columnList = "remoteId,lastSeen,gateway_id,dtype"),
        @Index(name = "remote_health_deadline_idx", columnList = "nextHealthEvaluationAt")
})
public class Remote extends Baseclass {

    @ManyToOne(targetEntity = ConnectivityChange.class)
    private ConnectivityChange lastConnectivityChange;

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime lastSeen;

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

    private boolean keepConnectivityHistory;

    private Double reportedLat;
    private Double reportedLon;

    @ManyToOne(targetEntity = RemoteHealthProfile.class)
    private RemoteHealthProfile healthProfile;

    private String currentSeverityName;
    private Integer currentSeverityValue;
    private String currentSeverityRuleId;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime severitySince;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime healthCalculatedAt;
    private boolean humanInterventionRequired;
    @Column(columnDefinition = "text")
    private String healthSummary;
    private String mitigationStatus;
    @Column(columnDefinition = "text")
    private String mitigationInstructions;

    /** ID and server-managed version of the effective health profile used for the current projection. */
    private String evaluatedHealthProfileId;
    private Integer healthEvaluationVersion;

    private boolean healthTransitionPending;
    private String pendingSeverityName;
    private Integer pendingSeverityValue;
    private String pendingSeverityRuleId;
    private boolean pendingHumanInterventionRequired;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime healthTransitionPendingSince;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime nextHealthEvaluationAt;


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

    @ManyToOne(targetEntity = RemoteHealthProfile.class)
    public RemoteHealthProfile getHealthProfile() { return healthProfile; }
    public <T extends Remote> T setHealthProfile(RemoteHealthProfile healthProfile) { this.healthProfile = healthProfile; return (T) this; }

    public String getCurrentSeverityName() { return currentSeverityName; }
    public <T extends Remote> T setCurrentSeverityName(String currentSeverityName) { this.currentSeverityName = currentSeverityName; return (T) this; }
    public Integer getCurrentSeverityValue() { return currentSeverityValue; }
    public <T extends Remote> T setCurrentSeverityValue(Integer currentSeverityValue) { this.currentSeverityValue = currentSeverityValue; return (T) this; }
    public String getCurrentSeverityRuleId() { return currentSeverityRuleId; }
    public <T extends Remote> T setCurrentSeverityRuleId(String currentSeverityRuleId) { this.currentSeverityRuleId = currentSeverityRuleId; return (T) this; }
    public OffsetDateTime getSeveritySince() { return severitySince; }
    public <T extends Remote> T setSeveritySince(OffsetDateTime severitySince) { this.severitySince = severitySince; return (T) this; }
    public OffsetDateTime getHealthCalculatedAt() { return healthCalculatedAt; }
    public <T extends Remote> T setHealthCalculatedAt(OffsetDateTime healthCalculatedAt) { this.healthCalculatedAt = healthCalculatedAt; return (T) this; }
    public boolean isHumanInterventionRequired() { return humanInterventionRequired; }
    public <T extends Remote> T setHumanInterventionRequired(boolean humanInterventionRequired) { this.humanInterventionRequired = humanInterventionRequired; return (T) this; }
    public String getHealthSummary() { return healthSummary; }
    public <T extends Remote> T setHealthSummary(String healthSummary) { this.healthSummary = healthSummary; return (T) this; }
    public String getMitigationStatus() { return mitigationStatus; }
    public <T extends Remote> T setMitigationStatus(String mitigationStatus) { this.mitigationStatus = mitigationStatus; return (T) this; }
    public String getMitigationInstructions() { return mitigationInstructions; }
    public <T extends Remote> T setMitigationInstructions(String mitigationInstructions) { this.mitigationInstructions = mitigationInstructions; return (T) this; }
    public String getEvaluatedHealthProfileId() { return evaluatedHealthProfileId; }
    public <T extends Remote> T setEvaluatedHealthProfileId(String evaluatedHealthProfileId) { this.evaluatedHealthProfileId = evaluatedHealthProfileId; return (T) this; }
    public Integer getHealthEvaluationVersion() { return healthEvaluationVersion; }
    public <T extends Remote> T setHealthEvaluationVersion(Integer healthEvaluationVersion) { this.healthEvaluationVersion = healthEvaluationVersion; return (T) this; }

    public boolean isKeepConnectivityHistory() {
        return keepConnectivityHistory;
    }

    public <T extends Remote> T setKeepConnectivityHistory(boolean keepConnectivityHistory) {
        this.keepConnectivityHistory = keepConnectivityHistory;
        return (T) this;
    }

    public boolean isKeepStateHistory() {
        return keepStateHistory;
    }

    public <T extends Remote> T setKeepStateHistory(boolean keepStateHistory) {
        this.keepStateHistory = keepStateHistory;
        return (T) this;
    }

    public boolean isHealthTransitionPending() {
        return healthTransitionPending;
    }

    public <T extends Remote> T setHealthTransitionPending(boolean healthTransitionPending) {
        this.healthTransitionPending = healthTransitionPending;
        return (T) this;
    }

    public String getPendingSeverityName() {
        return pendingSeverityName;
    }

    public <T extends Remote> T setPendingSeverityName(String pendingSeverityName) {
        this.pendingSeverityName = pendingSeverityName;
        return (T) this;
    }

    public Integer getPendingSeverityValue() {
        return pendingSeverityValue;
    }

    public <T extends Remote> T setPendingSeverityValue(Integer pendingSeverityValue) {
        this.pendingSeverityValue = pendingSeverityValue;
        return (T) this;
    }

    public String getPendingSeverityRuleId() {
        return pendingSeverityRuleId;
    }

    public <T extends Remote> T setPendingSeverityRuleId(String pendingSeverityRuleId) {
        this.pendingSeverityRuleId = pendingSeverityRuleId;
        return (T) this;
    }

    public boolean isPendingHumanInterventionRequired() {
        return pendingHumanInterventionRequired;
    }

    public <T extends Remote> T setPendingHumanInterventionRequired(boolean pendingHumanInterventionRequired) {
        this.pendingHumanInterventionRequired = pendingHumanInterventionRequired;
        return (T) this;
    }

    public OffsetDateTime getHealthTransitionPendingSince() {
        return healthTransitionPendingSince;
    }

    public <T extends Remote> T setHealthTransitionPendingSince(OffsetDateTime healthTransitionPendingSince) {
        this.healthTransitionPendingSince = healthTransitionPendingSince;
        return (T) this;
    }

    public OffsetDateTime getNextHealthEvaluationAt() {
        return nextHealthEvaluationAt;
    }

    public <T extends Remote> T setNextHealthEvaluationAt(OffsetDateTime nextHealthEvaluationAt) {
        this.nextHealthEvaluationAt = nextHealthEvaluationAt;
        return (T) this;
    }

    @Override
    public String toString() {
        return "Remote{" +
                "lastConnectivityChange=" + lastConnectivityChange +
                ", lastSeen=" + lastSeen +
                ", remoteId='" + remoteId + '\'' +
                ", version='" + version + '\'' +
                ", mappedPOI=" + mappedPOI +
                ", preConnectivityLossIcon=" + preConnectivityLossIcon +
                ", currentSchema=" + currentSchema +
                ", deviceProperties=" + deviceProperties +
                ", userAddedProperties=" + userAddedProperties +
                ", lockLocation=" + lockLocation +
                ", lockName=" + lockName +
                ", keepStateHistory=" + keepStateHistory +
                ", keepConnectivityHistory=" + keepConnectivityHistory +
                ", reportedLat=" + reportedLat +
                ", reportedLon=" + reportedLon +
                '}';
    }
}
