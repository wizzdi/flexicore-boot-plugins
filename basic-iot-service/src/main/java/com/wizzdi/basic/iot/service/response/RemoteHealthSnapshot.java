package com.wizzdi.basic.iot.service.response;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class RemoteHealthSnapshot {
    private String remoteId;
    private String remoteHealthProfileId;
    private String remoteHealthProfileName;
    private String severityName;
    private Integer severityValue;
    private String matchedRuleId;
    private String matchedRuleName;
    private boolean humanInterventionRequired;
    private String summary;
    private String mitigationInstructions;
    private OffsetDateTime calculatedAt;
    private Map<String, Object> signals = new LinkedHashMap<>();

    public String getRemoteId() { return remoteId; }
    public <T extends RemoteHealthSnapshot> T setRemoteId(String remoteId) { this.remoteId = remoteId; return (T) this; }
    public String getRemoteHealthProfileId() { return remoteHealthProfileId; }
    public <T extends RemoteHealthSnapshot> T setRemoteHealthProfileId(String remoteHealthProfileId) { this.remoteHealthProfileId = remoteHealthProfileId; return (T) this; }
    public String getRemoteHealthProfileName() { return remoteHealthProfileName; }
    public <T extends RemoteHealthSnapshot> T setRemoteHealthProfileName(String remoteHealthProfileName) { this.remoteHealthProfileName = remoteHealthProfileName; return (T) this; }
    public String getSeverityName() { return severityName; }
    public <T extends RemoteHealthSnapshot> T setSeverityName(String severityName) { this.severityName = severityName; return (T) this; }
    public Integer getSeverityValue() { return severityValue; }
    public <T extends RemoteHealthSnapshot> T setSeverityValue(Integer severityValue) { this.severityValue = severityValue; return (T) this; }
    public String getMatchedRuleId() { return matchedRuleId; }
    public <T extends RemoteHealthSnapshot> T setMatchedRuleId(String matchedRuleId) { this.matchedRuleId = matchedRuleId; return (T) this; }
    public String getMatchedRuleName() { return matchedRuleName; }
    public <T extends RemoteHealthSnapshot> T setMatchedRuleName(String matchedRuleName) { this.matchedRuleName = matchedRuleName; return (T) this; }
    public boolean isHumanInterventionRequired() { return humanInterventionRequired; }
    public <T extends RemoteHealthSnapshot> T setHumanInterventionRequired(boolean humanInterventionRequired) { this.humanInterventionRequired = humanInterventionRequired; return (T) this; }
    public String getSummary() { return summary; }
    public <T extends RemoteHealthSnapshot> T setSummary(String summary) { this.summary = summary; return (T) this; }
    public String getMitigationInstructions() { return mitigationInstructions; }
    public <T extends RemoteHealthSnapshot> T setMitigationInstructions(String mitigationInstructions) { this.mitigationInstructions = mitigationInstructions; return (T) this; }
    public OffsetDateTime getCalculatedAt() { return calculatedAt; }
    public <T extends RemoteHealthSnapshot> T setCalculatedAt(OffsetDateTime calculatedAt) { this.calculatedAt = calculatedAt; return (T) this; }
    public Map<String, Object> getSignals() { return signals; }
    public <T extends RemoteHealthSnapshot> T setSignals(Map<String, Object> signals) { this.signals = signals; return (T) this; }
}
