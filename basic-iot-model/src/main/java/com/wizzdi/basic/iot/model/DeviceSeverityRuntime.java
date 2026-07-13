package com.wizzdi.basic.iot.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "device_severity_runtime_remote_rule_idx", columnList = "remote_id,ruleId", unique = true)
})
public class DeviceSeverityRuntime {
    @Id
    private String id;
    @ManyToOne(targetEntity = Remote.class, optional = false)
    private Remote remote;
    private String ruleId;
    private boolean active;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime candidateSince;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime clearCandidateSince;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime activeSince;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime lastEvaluatedAt;

    public String getId() { return id; }
    public DeviceSeverityRuntime setId(String id) { this.id = id; return this; }
    public Remote getRemote() { return remote; }
    public DeviceSeverityRuntime setRemote(Remote remote) { this.remote = remote; return this; }
    public String getRuleId() { return ruleId; }
    public DeviceSeverityRuntime setRuleId(String ruleId) { this.ruleId = ruleId; return this; }
    public boolean isActive() { return active; }
    public DeviceSeverityRuntime setActive(boolean active) { this.active = active; return this; }
    public OffsetDateTime getCandidateSince() { return candidateSince; }
    public DeviceSeverityRuntime setCandidateSince(OffsetDateTime candidateSince) { this.candidateSince = candidateSince; return this; }
    public OffsetDateTime getClearCandidateSince() { return clearCandidateSince; }
    public DeviceSeverityRuntime setClearCandidateSince(OffsetDateTime clearCandidateSince) { this.clearCandidateSince = clearCandidateSince; return this; }
    public OffsetDateTime getActiveSince() { return activeSince; }
    public DeviceSeverityRuntime setActiveSince(OffsetDateTime activeSince) { this.activeSince = activeSince; return this; }
    public OffsetDateTime getLastEvaluatedAt() { return lastEvaluatedAt; }
    public DeviceSeverityRuntime setLastEvaluatedAt(OffsetDateTime lastEvaluatedAt) { this.lastEvaluatedAt = lastEvaluatedAt; return this; }
}
