package com.wizzdi.basic.iot.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "severity_history_remote_time_idx", columnList = "remote_id,recordedAt"),
        @Index(name = "severity_history_type_value_idx", columnList = "deviceType_id,severityValue,recordedAt")
})
public class SeverityHistory {
    @Id
    private String id;
    @ManyToOne(targetEntity = Remote.class, optional = false)
    private Remote remote;
    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType deviceType;
    private String ruleId;
    private String severityName;
    private Integer severityValue;
    private Integer previousSeverityValue;
    private boolean humanInterventionRequired;
    private String mitigationStatus;
    @Column(columnDefinition = "text")
    private String matchedConditions;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime recordedAt;

    public String getId() { return id; }
    public SeverityHistory setId(String id) { this.id = id; return this; }
    public Remote getRemote() { return remote; }
    public SeverityHistory setRemote(Remote remote) { this.remote = remote; return this; }
    public DeviceType getDeviceType() { return deviceType; }
    public SeverityHistory setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; return this; }
    public String getRuleId() { return ruleId; }
    public SeverityHistory setRuleId(String ruleId) { this.ruleId = ruleId; return this; }
    public String getSeverityName() { return severityName; }
    public SeverityHistory setSeverityName(String severityName) { this.severityName = severityName; return this; }
    public Integer getSeverityValue() { return severityValue; }
    public SeverityHistory setSeverityValue(Integer severityValue) { this.severityValue = severityValue; return this; }
    public Integer getPreviousSeverityValue() { return previousSeverityValue; }
    public SeverityHistory setPreviousSeverityValue(Integer previousSeverityValue) { this.previousSeverityValue = previousSeverityValue; return this; }
    public boolean isHumanInterventionRequired() { return humanInterventionRequired; }
    public SeverityHistory setHumanInterventionRequired(boolean humanInterventionRequired) { this.humanInterventionRequired = humanInterventionRequired; return this; }
    public String getMitigationStatus() { return mitigationStatus; }
    public SeverityHistory setMitigationStatus(String mitigationStatus) { this.mitigationStatus = mitigationStatus; return this; }
    public String getMatchedConditions() { return matchedConditions; }
    public SeverityHistory setMatchedConditions(String matchedConditions) { this.matchedConditions = matchedConditions; return this; }
    public OffsetDateTime getRecordedAt() { return recordedAt; }
    public SeverityHistory setRecordedAt(OffsetDateTime recordedAt) { this.recordedAt = recordedAt; return this; }
}
