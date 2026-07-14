package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "remote_group_external_id_idx", columnList = "externalId"),
        @Index(name = "remote_group_policy_idx", columnList = "fleetHealthPolicy_id"),
        @Index(name = "remote_group_population_idx", columnList = "populationType,sourceDeviceType_id,softDelete"),
        @Index(name = "remote_group_health_deadline_idx", columnList = "nextHealthEvaluationAt")
})
public class RemoteGroup extends Baseclass {

    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RemoteGroupPopulationType populationType = RemoteGroupPopulationType.STATIC;

    @ManyToOne(targetEntity = DeviceType.class)
    @JoinColumn(name = "sourceDeviceType_id", unique = true)
    private DeviceType sourceDeviceType;

    @Column(nullable = false)
    private boolean systemManaged;

    @ManyToOne(targetEntity = FleetHealthPolicy.class)
    private FleetHealthPolicy fleetHealthPolicy;

    private boolean healthEnabled;
    private String currentSeverityName;
    private Integer currentSeverityValue;
    private String currentSeverityRuleId;
    private boolean humanInterventionRequired;
    private Integer currentPopulationCount;
    private Integer currentUnknownCount;
    private Integer currentOfflineCount;
    private Integer currentHumanInterventionCount;
    private String evaluatedFleetHealthPolicyId;
    private Integer fleetHealthEvaluationVersion;
    private long healthInputVersion = 1;
    private Long evaluatedHealthInputVersion;

    private boolean healthTransitionPending;
    private String pendingSeverityName;
    private Integer pendingSeverityValue;
    private String pendingSeverityRuleId;
    private boolean pendingHumanInterventionRequired;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime healthTransitionPendingSince;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime nextHealthEvaluationAt;

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime healthCalculatedAt;

    public String getExternalId() {
        return externalId;
    }

    public <T extends RemoteGroup> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    public RemoteGroupPopulationType getPopulationType() {
        return populationType;
    }

    public <T extends RemoteGroup> T setPopulationType(RemoteGroupPopulationType populationType) {
        this.populationType = populationType;
        return (T) this;
    }

    public DeviceType getSourceDeviceType() {
        return sourceDeviceType;
    }

    public <T extends RemoteGroup> T setSourceDeviceType(DeviceType sourceDeviceType) {
        this.sourceDeviceType = sourceDeviceType;
        return (T) this;
    }

    public boolean isSystemManaged() {
        return systemManaged;
    }

    public <T extends RemoteGroup> T setSystemManaged(boolean systemManaged) {
        this.systemManaged = systemManaged;
        return (T) this;
    }

    public FleetHealthPolicy getFleetHealthPolicy() {
        return fleetHealthPolicy;
    }

    public <T extends RemoteGroup> T setFleetHealthPolicy(FleetHealthPolicy fleetHealthPolicy) {
        this.fleetHealthPolicy = fleetHealthPolicy;
        return (T) this;
    }

    public boolean isHealthEnabled() {
        return healthEnabled;
    }

    public <T extends RemoteGroup> T setHealthEnabled(boolean healthEnabled) {
        this.healthEnabled = healthEnabled;
        return (T) this;
    }

    public String getCurrentSeverityName() {
        return currentSeverityName;
    }

    public <T extends RemoteGroup> T setCurrentSeverityName(String currentSeverityName) {
        this.currentSeverityName = currentSeverityName;
        return (T) this;
    }

    public Integer getCurrentSeverityValue() {
        return currentSeverityValue;
    }

    public <T extends RemoteGroup> T setCurrentSeverityValue(Integer currentSeverityValue) {
        this.currentSeverityValue = currentSeverityValue;
        return (T) this;
    }

    public String getCurrentSeverityRuleId() {
        return currentSeverityRuleId;
    }

    public <T extends RemoteGroup> T setCurrentSeverityRuleId(String currentSeverityRuleId) {
        this.currentSeverityRuleId = currentSeverityRuleId;
        return (T) this;
    }

    public boolean isHumanInterventionRequired() {
        return humanInterventionRequired;
    }

    public <T extends RemoteGroup> T setHumanInterventionRequired(boolean humanInterventionRequired) {
        this.humanInterventionRequired = humanInterventionRequired;
        return (T) this;
    }

    public Integer getCurrentPopulationCount() {
        return currentPopulationCount;
    }

    public <T extends RemoteGroup> T setCurrentPopulationCount(Integer currentPopulationCount) {
        this.currentPopulationCount = currentPopulationCount;
        return (T) this;
    }

    public Integer getCurrentUnknownCount() {
        return currentUnknownCount;
    }

    public <T extends RemoteGroup> T setCurrentUnknownCount(Integer currentUnknownCount) {
        this.currentUnknownCount = currentUnknownCount;
        return (T) this;
    }

    public Integer getCurrentOfflineCount() {
        return currentOfflineCount;
    }

    public <T extends RemoteGroup> T setCurrentOfflineCount(Integer currentOfflineCount) {
        this.currentOfflineCount = currentOfflineCount;
        return (T) this;
    }

    public Integer getCurrentHumanInterventionCount() {
        return currentHumanInterventionCount;
    }

    public <T extends RemoteGroup> T setCurrentHumanInterventionCount(Integer currentHumanInterventionCount) {
        this.currentHumanInterventionCount = currentHumanInterventionCount;
        return (T) this;
    }

    public String getEvaluatedFleetHealthPolicyId() {
        return evaluatedFleetHealthPolicyId;
    }

    public <T extends RemoteGroup> T setEvaluatedFleetHealthPolicyId(String evaluatedFleetHealthPolicyId) {
        this.evaluatedFleetHealthPolicyId = evaluatedFleetHealthPolicyId;
        return (T) this;
    }

    public Integer getFleetHealthEvaluationVersion() {
        return fleetHealthEvaluationVersion;
    }

    public <T extends RemoteGroup> T setFleetHealthEvaluationVersion(Integer fleetHealthEvaluationVersion) {
        this.fleetHealthEvaluationVersion = fleetHealthEvaluationVersion;
        return (T) this;
    }

    public long getHealthInputVersion() {
        return healthInputVersion;
    }

    public <T extends RemoteGroup> T setHealthInputVersion(long healthInputVersion) {
        this.healthInputVersion = healthInputVersion;
        return (T) this;
    }

    public Long getEvaluatedHealthInputVersion() {
        return evaluatedHealthInputVersion;
    }

    public <T extends RemoteGroup> T setEvaluatedHealthInputVersion(Long evaluatedHealthInputVersion) {
        this.evaluatedHealthInputVersion = evaluatedHealthInputVersion;
        return (T) this;
    }

    public boolean isHealthTransitionPending() {
        return healthTransitionPending;
    }

    public <T extends RemoteGroup> T setHealthTransitionPending(boolean healthTransitionPending) {
        this.healthTransitionPending = healthTransitionPending;
        return (T) this;
    }

    public String getPendingSeverityName() {
        return pendingSeverityName;
    }

    public <T extends RemoteGroup> T setPendingSeverityName(String pendingSeverityName) {
        this.pendingSeverityName = pendingSeverityName;
        return (T) this;
    }

    public Integer getPendingSeverityValue() {
        return pendingSeverityValue;
    }

    public <T extends RemoteGroup> T setPendingSeverityValue(Integer pendingSeverityValue) {
        this.pendingSeverityValue = pendingSeverityValue;
        return (T) this;
    }

    public String getPendingSeverityRuleId() {
        return pendingSeverityRuleId;
    }

    public <T extends RemoteGroup> T setPendingSeverityRuleId(String pendingSeverityRuleId) {
        this.pendingSeverityRuleId = pendingSeverityRuleId;
        return (T) this;
    }

    public boolean isPendingHumanInterventionRequired() {
        return pendingHumanInterventionRequired;
    }

    public <T extends RemoteGroup> T setPendingHumanInterventionRequired(boolean pendingHumanInterventionRequired) {
        this.pendingHumanInterventionRequired = pendingHumanInterventionRequired;
        return (T) this;
    }

    public OffsetDateTime getHealthTransitionPendingSince() {
        return healthTransitionPendingSince;
    }

    public <T extends RemoteGroup> T setHealthTransitionPendingSince(OffsetDateTime healthTransitionPendingSince) {
        this.healthTransitionPendingSince = healthTransitionPendingSince;
        return (T) this;
    }

    public OffsetDateTime getNextHealthEvaluationAt() {
        return nextHealthEvaluationAt;
    }

    public <T extends RemoteGroup> T setNextHealthEvaluationAt(OffsetDateTime nextHealthEvaluationAt) {
        this.nextHealthEvaluationAt = nextHealthEvaluationAt;
        return (T) this;
    }

    public OffsetDateTime getHealthCalculatedAt() {
        return healthCalculatedAt;
    }

    public <T extends RemoteGroup> T setHealthCalculatedAt(OffsetDateTime healthCalculatedAt) {
        this.healthCalculatedAt = healthCalculatedAt;
        return (T) this;
    }
}
