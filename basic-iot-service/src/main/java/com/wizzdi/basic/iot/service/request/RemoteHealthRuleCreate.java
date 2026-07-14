package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.ConditionJoinType;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.List;

public class RemoteHealthRuleCreate extends BasicCreate {
    private String id;
    private Integer priority;
    private Boolean enabled;
    private ConditionJoinType conditionJoinType;
    private String resultingSeverityName;
    private Integer resultingSeverityValue;
    private Boolean humanInterventionRequired;
    private Long minimumStableMillis;
    private Long recoveryStableMillis;
    private String summary;
    private String mitigationInstructions;
    private List<RemoteHealthRuleConditionCreate> conditions;

    public String getId() { return id; }
    public <T extends RemoteHealthRuleCreate> T setId(String id) { this.id = id; return (T) this; }
    public Integer getPriority() { return priority; }
    public <T extends RemoteHealthRuleCreate> T setPriority(Integer priority) { this.priority = priority; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends RemoteHealthRuleCreate> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
    public ConditionJoinType getConditionJoinType() { return conditionJoinType; }
    public <T extends RemoteHealthRuleCreate> T setConditionJoinType(ConditionJoinType conditionJoinType) { this.conditionJoinType = conditionJoinType; return (T) this; }
    public String getResultingSeverityName() { return resultingSeverityName; }
    public <T extends RemoteHealthRuleCreate> T setResultingSeverityName(String resultingSeverityName) { this.resultingSeverityName = resultingSeverityName; return (T) this; }
    public Integer getResultingSeverityValue() { return resultingSeverityValue; }
    public <T extends RemoteHealthRuleCreate> T setResultingSeverityValue(Integer resultingSeverityValue) { this.resultingSeverityValue = resultingSeverityValue; return (T) this; }
    public Boolean getHumanInterventionRequired() { return humanInterventionRequired; }
    public <T extends RemoteHealthRuleCreate> T setHumanInterventionRequired(Boolean humanInterventionRequired) { this.humanInterventionRequired = humanInterventionRequired; return (T) this; }
    public Long getMinimumStableMillis() { return minimumStableMillis; }
    public <T extends RemoteHealthRuleCreate> T setMinimumStableMillis(Long minimumStableMillis) { this.minimumStableMillis = minimumStableMillis; return (T) this; }
    public Long getRecoveryStableMillis() { return recoveryStableMillis; }
    public <T extends RemoteHealthRuleCreate> T setRecoveryStableMillis(Long recoveryStableMillis) { this.recoveryStableMillis = recoveryStableMillis; return (T) this; }
    public String getSummary() { return summary; }
    public <T extends RemoteHealthRuleCreate> T setSummary(String summary) { this.summary = summary; return (T) this; }
    public String getMitigationInstructions() { return mitigationInstructions; }
    public <T extends RemoteHealthRuleCreate> T setMitigationInstructions(String mitigationInstructions) { this.mitigationInstructions = mitigationInstructions; return (T) this; }
    public List<RemoteHealthRuleConditionCreate> getConditions() { return conditions; }
    public <T extends RemoteHealthRuleCreate> T setConditions(List<RemoteHealthRuleConditionCreate> conditions) { this.conditions = conditions; return (T) this; }
}
