package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.SecurityUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "health_incident_remote_status_idx", columnList = "remote_id,status,softdelete"),
        @Index(name = "health_incident_group_status_idx", columnList = "remoteGroup_id,status,softdelete"),
        @Index(name = "health_incident_opened_idx", columnList = "openedAt")
})
public class HealthIncident extends Baseclass {

    @ManyToOne(targetEntity = Remote.class)
    private Remote remote;
    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup remoteGroup;
    @Enumerated(EnumType.STRING)
    private HealthIncidentStatus status = HealthIncidentStatus.OPEN;
    private String severityName;
    private Integer severityValue;
    private String matchedRuleId;
    private boolean actionRequired;
    private boolean healthRecovered;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime openedAt;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime lastHealthEventAt;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime healthRecoveredAt;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime acknowledgedAt;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime resolvedAt;
    @ManyToOne(targetEntity = SecurityUser.class)
    private SecurityUser acknowledgedBy;
    @ManyToOne(targetEntity = SecurityUser.class)
    private SecurityUser resolvedBy;
    @ManyToOne(targetEntity = SecurityUser.class)
    private SecurityUser assignedTo;
    @Column(length = 4000)
    private String summary;
    @Column(length = 4000)
    private String mitigationInstructions;
    @Column(length = 4000)
    private String latestActionSummary;

    public Remote getRemote() { return remote; }
    public <T extends HealthIncident> T setRemote(Remote remote) { this.remote = remote; return (T) this; }
    public RemoteGroup getRemoteGroup() { return remoteGroup; }
    public <T extends HealthIncident> T setRemoteGroup(RemoteGroup remoteGroup) { this.remoteGroup = remoteGroup; return (T) this; }
    public HealthIncidentStatus getStatus() { return status; }
    public <T extends HealthIncident> T setStatus(HealthIncidentStatus status) { this.status = status; return (T) this; }
    public String getSeverityName() { return severityName; }
    public <T extends HealthIncident> T setSeverityName(String severityName) { this.severityName = severityName; return (T) this; }
    public Integer getSeverityValue() { return severityValue; }
    public <T extends HealthIncident> T setSeverityValue(Integer severityValue) { this.severityValue = severityValue; return (T) this; }
    public String getMatchedRuleId() { return matchedRuleId; }
    public <T extends HealthIncident> T setMatchedRuleId(String matchedRuleId) { this.matchedRuleId = matchedRuleId; return (T) this; }
    public boolean isActionRequired() { return actionRequired; }
    public <T extends HealthIncident> T setActionRequired(boolean actionRequired) { this.actionRequired = actionRequired; return (T) this; }
    public boolean isHealthRecovered() { return healthRecovered; }
    public <T extends HealthIncident> T setHealthRecovered(boolean healthRecovered) { this.healthRecovered = healthRecovered; return (T) this; }
    public OffsetDateTime getOpenedAt() { return openedAt; }
    public <T extends HealthIncident> T setOpenedAt(OffsetDateTime openedAt) { this.openedAt = openedAt; return (T) this; }
    public OffsetDateTime getLastHealthEventAt() { return lastHealthEventAt; }
    public <T extends HealthIncident> T setLastHealthEventAt(OffsetDateTime lastHealthEventAt) { this.lastHealthEventAt = lastHealthEventAt; return (T) this; }
    public OffsetDateTime getHealthRecoveredAt() { return healthRecoveredAt; }
    public <T extends HealthIncident> T setHealthRecoveredAt(OffsetDateTime healthRecoveredAt) { this.healthRecoveredAt = healthRecoveredAt; return (T) this; }
    public OffsetDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public <T extends HealthIncident> T setAcknowledgedAt(OffsetDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; return (T) this; }
    public OffsetDateTime getResolvedAt() { return resolvedAt; }
    public <T extends HealthIncident> T setResolvedAt(OffsetDateTime resolvedAt) { this.resolvedAt = resolvedAt; return (T) this; }
    public SecurityUser getAcknowledgedBy() { return acknowledgedBy; }
    public <T extends HealthIncident> T setAcknowledgedBy(SecurityUser acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; return (T) this; }
    public SecurityUser getResolvedBy() { return resolvedBy; }
    public <T extends HealthIncident> T setResolvedBy(SecurityUser resolvedBy) { this.resolvedBy = resolvedBy; return (T) this; }
    public SecurityUser getAssignedTo() { return assignedTo; }
    public <T extends HealthIncident> T setAssignedTo(SecurityUser assignedTo) { this.assignedTo = assignedTo; return (T) this; }
    public String getSummary() { return summary; }
    public <T extends HealthIncident> T setSummary(String summary) { this.summary = summary; return (T) this; }
    public String getMitigationInstructions() { return mitigationInstructions; }
    public <T extends HealthIncident> T setMitigationInstructions(String mitigationInstructions) { this.mitigationInstructions = mitigationInstructions; return (T) this; }
    public String getLatestActionSummary() { return latestActionSummary; }
    public <T extends HealthIncident> T setLatestActionSummary(String latestActionSummary) { this.latestActionSummary = latestActionSummary; return (T) this; }
}
