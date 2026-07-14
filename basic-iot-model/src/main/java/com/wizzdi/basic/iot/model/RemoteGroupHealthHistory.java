package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "remote_group_health_history_group_interval_idx", columnList = "remoteGroup_id,validFrom,validUntil"),
        @Index(name = "remote_group_health_history_severity_idx", columnList = "severityValue,validFrom")
})
public class RemoteGroupHealthHistory extends Baseclass {

    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup remoteGroup;
    @ManyToOne(targetEntity = FleetHealthPolicy.class)
    private FleetHealthPolicy fleetHealthPolicy;
    @ManyToOne(targetEntity = FleetHealthRule.class)
    private FleetHealthRule matchedRule;
    private String severityName;
    private Integer severityValue;
    private boolean humanInterventionRequired;
    private Integer populationCount;
    private Integer unknownCount;
    private Integer offlineCount;
    private Integer humanInterventionCount;
    private Double weightedSeverityAverage;
    private Integer policyEvaluationVersion;
    private Long healthInputVersion;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime validFrom;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime validUntil;

    @Transient
    private List<RemoteGroupHealthMetricHistory> metrics = new ArrayList<>();

    public RemoteGroup getRemoteGroup() { return remoteGroup; }
    public <T extends RemoteGroupHealthHistory> T setRemoteGroup(RemoteGroup remoteGroup) { this.remoteGroup = remoteGroup; return (T) this; }
    public FleetHealthPolicy getFleetHealthPolicy() { return fleetHealthPolicy; }
    public <T extends RemoteGroupHealthHistory> T setFleetHealthPolicy(FleetHealthPolicy fleetHealthPolicy) { this.fleetHealthPolicy = fleetHealthPolicy; return (T) this; }
    public FleetHealthRule getMatchedRule() { return matchedRule; }
    public <T extends RemoteGroupHealthHistory> T setMatchedRule(FleetHealthRule matchedRule) { this.matchedRule = matchedRule; return (T) this; }
    public String getSeverityName() { return severityName; }
    public <T extends RemoteGroupHealthHistory> T setSeverityName(String severityName) { this.severityName = severityName; return (T) this; }
    public Integer getSeverityValue() { return severityValue; }
    public <T extends RemoteGroupHealthHistory> T setSeverityValue(Integer severityValue) { this.severityValue = severityValue; return (T) this; }
    public boolean isHumanInterventionRequired() { return humanInterventionRequired; }
    public <T extends RemoteGroupHealthHistory> T setHumanInterventionRequired(boolean humanInterventionRequired) { this.humanInterventionRequired = humanInterventionRequired; return (T) this; }
    public Integer getPopulationCount() { return populationCount; }
    public <T extends RemoteGroupHealthHistory> T setPopulationCount(Integer populationCount) { this.populationCount = populationCount; return (T) this; }
    public Integer getUnknownCount() { return unknownCount; }
    public <T extends RemoteGroupHealthHistory> T setUnknownCount(Integer unknownCount) { this.unknownCount = unknownCount; return (T) this; }
    public Integer getOfflineCount() { return offlineCount; }
    public <T extends RemoteGroupHealthHistory> T setOfflineCount(Integer offlineCount) { this.offlineCount = offlineCount; return (T) this; }
    public Integer getHumanInterventionCount() { return humanInterventionCount; }
    public <T extends RemoteGroupHealthHistory> T setHumanInterventionCount(Integer humanInterventionCount) { this.humanInterventionCount = humanInterventionCount; return (T) this; }
    public Double getWeightedSeverityAverage() { return weightedSeverityAverage; }
    public <T extends RemoteGroupHealthHistory> T setWeightedSeverityAverage(Double weightedSeverityAverage) { this.weightedSeverityAverage = weightedSeverityAverage; return (T) this; }
    public Integer getPolicyEvaluationVersion() { return policyEvaluationVersion; }
    public <T extends RemoteGroupHealthHistory> T setPolicyEvaluationVersion(Integer policyEvaluationVersion) { this.policyEvaluationVersion = policyEvaluationVersion; return (T) this; }
    public Long getHealthInputVersion() { return healthInputVersion; }
    public <T extends RemoteGroupHealthHistory> T setHealthInputVersion(Long healthInputVersion) { this.healthInputVersion = healthInputVersion; return (T) this; }
    public OffsetDateTime getValidFrom() { return validFrom; }
    public <T extends RemoteGroupHealthHistory> T setValidFrom(OffsetDateTime validFrom) { this.validFrom = validFrom; return (T) this; }
    public OffsetDateTime getValidUntil() { return validUntil; }
    public <T extends RemoteGroupHealthHistory> T setValidUntil(OffsetDateTime validUntil) { this.validUntil = validUntil; return (T) this; }
    public List<RemoteGroupHealthMetricHistory> getMetrics() { return metrics; }
    public <T extends RemoteGroupHealthHistory> T setMetrics(List<RemoteGroupHealthMetricHistory> metrics) { this.metrics = metrics; return (T) this; }
}
