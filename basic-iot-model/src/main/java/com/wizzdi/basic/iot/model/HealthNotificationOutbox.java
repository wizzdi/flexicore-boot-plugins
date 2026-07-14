package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
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
        @Index(name = "health_notification_outbox_event_idx", columnList = "eventId", unique = true),
        @Index(name = "health_notification_outbox_time_idx", columnList = "occurredAt")
})
public class HealthNotificationOutbox extends Baseclass {
    @Column(nullable = false, unique = true)
    private String eventId;
    @ManyToOne(targetEntity = HealthIncident.class)
    private HealthIncident healthIncident;
    @ManyToOne(targetEntity = Remote.class)
    private Remote remote;
    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup remoteGroup;
    @Enumerated(EnumType.STRING)
    private HealthNotificationEventType eventType;
    private String previousSeverityName;
    private Integer previousSeverityValue;
    private String severityName;
    private Integer severityValue;
    private String matchedRuleId;
    private String title;
    @Column(length = 8000)
    private String message;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime occurredAt;

    public String getEventId() { return eventId; }
    public <T extends HealthNotificationOutbox> T setEventId(String eventId) { this.eventId = eventId; return (T) this; }
    public HealthIncident getHealthIncident() { return healthIncident; }
    public <T extends HealthNotificationOutbox> T setHealthIncident(HealthIncident healthIncident) { this.healthIncident = healthIncident; return (T) this; }
    public Remote getRemote() { return remote; }
    public <T extends HealthNotificationOutbox> T setRemote(Remote remote) { this.remote = remote; return (T) this; }
    public RemoteGroup getRemoteGroup() { return remoteGroup; }
    public <T extends HealthNotificationOutbox> T setRemoteGroup(RemoteGroup remoteGroup) { this.remoteGroup = remoteGroup; return (T) this; }
    public HealthNotificationEventType getEventType() { return eventType; }
    public <T extends HealthNotificationOutbox> T setEventType(HealthNotificationEventType eventType) { this.eventType = eventType; return (T) this; }
    public String getPreviousSeverityName() { return previousSeverityName; }
    public <T extends HealthNotificationOutbox> T setPreviousSeverityName(String previousSeverityName) { this.previousSeverityName = previousSeverityName; return (T) this; }
    public Integer getPreviousSeverityValue() { return previousSeverityValue; }
    public <T extends HealthNotificationOutbox> T setPreviousSeverityValue(Integer previousSeverityValue) { this.previousSeverityValue = previousSeverityValue; return (T) this; }
    public String getSeverityName() { return severityName; }
    public <T extends HealthNotificationOutbox> T setSeverityName(String severityName) { this.severityName = severityName; return (T) this; }
    public Integer getSeverityValue() { return severityValue; }
    public <T extends HealthNotificationOutbox> T setSeverityValue(Integer severityValue) { this.severityValue = severityValue; return (T) this; }
    public String getMatchedRuleId() { return matchedRuleId; }
    public <T extends HealthNotificationOutbox> T setMatchedRuleId(String matchedRuleId) { this.matchedRuleId = matchedRuleId; return (T) this; }
    public String getTitle() { return title; }
    public <T extends HealthNotificationOutbox> T setTitle(String title) { this.title = title; return (T) this; }
    public String getMessage() { return message; }
    public <T extends HealthNotificationOutbox> T setMessage(String message) { this.message = message; return (T) this; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public <T extends HealthNotificationOutbox> T setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; return (T) this; }
}
