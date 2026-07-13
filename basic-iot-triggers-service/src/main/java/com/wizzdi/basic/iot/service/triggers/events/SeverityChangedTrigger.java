package com.wizzdi.basic.iot.service.triggers.events;

import com.flexicore.model.SecurityTenant;
import com.flexicore.rules.events.ScenarioEventBase;
import com.wizzdi.basic.iot.model.Device;
import java.time.OffsetDateTime;
import java.util.List;

public class SeverityChangedTrigger extends ScenarioEventBase {
    private final Device device;
    private final String previousSeverityName;
    private final Integer previousSeverityValue;
    private final String severityName;
    private final Integer severityValue;
    private final String ruleId;
    private final boolean humanInterventionRequired;
    private final String mitigationInstructions;
    private final String escalationKey;
    private final OffsetDateTime occurredAt;

    public SeverityChangedTrigger(Device device, String previousSeverityName, Integer previousSeverityValue,
                                  String severityName, Integer severityValue, String ruleId,
                                  boolean humanInterventionRequired, String mitigationInstructions,
                                  String escalationKey, OffsetDateTime occurredAt, List<SecurityTenant> tenants) {
        super(tenants);
        this.device = device; this.previousSeverityName = previousSeverityName; this.previousSeverityValue = previousSeverityValue;
        this.severityName = severityName; this.severityValue = severityValue; this.ruleId = ruleId;
        this.humanInterventionRequired = humanInterventionRequired; this.mitigationInstructions = mitigationInstructions;
        this.escalationKey = escalationKey; this.occurredAt = occurredAt;
    }
    public Device getDevice() { return device; }
    public String getPreviousSeverityName() { return previousSeverityName; }
    public Integer getPreviousSeverityValue() { return previousSeverityValue; }
    public String getSeverityName() { return severityName; }
    public Integer getSeverityValue() { return severityValue; }
    public String getRuleId() { return ruleId; }
    public boolean isHumanInterventionRequired() { return humanInterventionRequired; }
    public String getMitigationInstructions() { return mitigationInstructions; }
    public String getEscalationKey() { return escalationKey; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
}
