package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "remote_group_external_id_idx", columnList = "externalId"),
        @Index(name = "remote_group_policy_idx", columnList = "fleetHealthPolicy_id")
})
public class RemoteGroup extends Baseclass {

    private String externalId;

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

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime healthCalculatedAt;

    public String getExternalId() {
        return externalId;
    }

    public <T extends RemoteGroup> T setExternalId(String externalId) {
        this.externalId = externalId;
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

    public OffsetDateTime getHealthCalculatedAt() {
        return healthCalculatedAt;
    }

    public <T extends RemoteGroup> T setHealthCalculatedAt(OffsetDateTime healthCalculatedAt) {
        this.healthCalculatedAt = healthCalculatedAt;
        return (T) this;
    }
}
