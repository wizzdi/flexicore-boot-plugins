package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        @Index(name = "remote_health_signal_history_idx", columnList = "remoteHealthHistory_id,priority,softdelete"),
        @Index(name = "remote_health_signal_definition_idx", columnList = "healthSignalDefinition_id,softdelete")
})
public class RemoteHealthSignalEvidence extends Baseclass {

    @ManyToOne(targetEntity = RemoteHealthHistory.class)
    @JsonIgnore
    private RemoteHealthHistory remoteHealthHistory;
    @ManyToOne(targetEntity = HealthSignalDefinition.class)
    private HealthSignalDefinition healthSignalDefinition;
    @Enumerated(EnumType.STRING)
    private HealthSignalValueType valueType;
    private Double numericValue;
    private Boolean booleanValue;
    @Column(length = 4000)
    private String stringValue;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime timestampValue;
    private int priority;

    public RemoteHealthHistory getRemoteHealthHistory() { return remoteHealthHistory; }
    public <T extends RemoteHealthSignalEvidence> T setRemoteHealthHistory(RemoteHealthHistory remoteHealthHistory) { this.remoteHealthHistory = remoteHealthHistory; return (T) this; }
    public HealthSignalDefinition getHealthSignalDefinition() { return healthSignalDefinition; }
    public <T extends RemoteHealthSignalEvidence> T setHealthSignalDefinition(HealthSignalDefinition healthSignalDefinition) { this.healthSignalDefinition = healthSignalDefinition; return (T) this; }
    public HealthSignalValueType getValueType() { return valueType; }
    public <T extends RemoteHealthSignalEvidence> T setValueType(HealthSignalValueType valueType) { this.valueType = valueType; return (T) this; }
    public Double getNumericValue() { return numericValue; }
    public <T extends RemoteHealthSignalEvidence> T setNumericValue(Double numericValue) { this.numericValue = numericValue; return (T) this; }
    public Boolean getBooleanValue() { return booleanValue; }
    public <T extends RemoteHealthSignalEvidence> T setBooleanValue(Boolean booleanValue) { this.booleanValue = booleanValue; return (T) this; }
    public String getStringValue() { return stringValue; }
    public <T extends RemoteHealthSignalEvidence> T setStringValue(String stringValue) { this.stringValue = stringValue; return (T) this; }
    public OffsetDateTime getTimestampValue() { return timestampValue; }
    public <T extends RemoteHealthSignalEvidence> T setTimestampValue(OffsetDateTime timestampValue) { this.timestampValue = timestampValue; return (T) this; }
    public int getPriority() { return priority; }
    public <T extends RemoteHealthSignalEvidence> T setPriority(int priority) { this.priority = priority; return (T) this; }
}
