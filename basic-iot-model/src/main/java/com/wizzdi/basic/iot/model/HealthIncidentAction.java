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
@Table(indexes = @Index(name = "health_incident_action_incident_idx", columnList = "healthIncident_id,performedAt"))
public class HealthIncidentAction extends Baseclass {
    @ManyToOne(targetEntity = HealthIncident.class)
    private HealthIncident healthIncident;
    @ManyToOne(targetEntity = SecurityUser.class)
    private SecurityUser performedBy;
    @Enumerated(EnumType.STRING)
    private HealthIncidentActionType actionType;
    @Enumerated(EnumType.STRING)
    private HealthIncidentStatus previousStatus;
    @Enumerated(EnumType.STRING)
    private HealthIncidentStatus resultingStatus;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime performedAt;
    @Column(length = 4000)
    private String actionDescription;

    public HealthIncident getHealthIncident() { return healthIncident; }
    public <T extends HealthIncidentAction> T setHealthIncident(HealthIncident healthIncident) { this.healthIncident = healthIncident; return (T) this; }
    public SecurityUser getPerformedBy() { return performedBy; }
    public <T extends HealthIncidentAction> T setPerformedBy(SecurityUser performedBy) { this.performedBy = performedBy; return (T) this; }
    public HealthIncidentActionType getActionType() { return actionType; }
    public <T extends HealthIncidentAction> T setActionType(HealthIncidentActionType actionType) { this.actionType = actionType; return (T) this; }
    public HealthIncidentStatus getPreviousStatus() { return previousStatus; }
    public <T extends HealthIncidentAction> T setPreviousStatus(HealthIncidentStatus previousStatus) { this.previousStatus = previousStatus; return (T) this; }
    public HealthIncidentStatus getResultingStatus() { return resultingStatus; }
    public <T extends HealthIncidentAction> T setResultingStatus(HealthIncidentStatus resultingStatus) { this.resultingStatus = resultingStatus; return (T) this; }
    public OffsetDateTime getPerformedAt() { return performedAt; }
    public <T extends HealthIncidentAction> T setPerformedAt(OffsetDateTime performedAt) { this.performedAt = performedAt; return (T) this; }
    public String getActionDescription() { return actionDescription; }
    public <T extends HealthIncidentAction> T setActionDescription(String actionDescription) { this.actionDescription = actionDescription; return (T) this; }
}
