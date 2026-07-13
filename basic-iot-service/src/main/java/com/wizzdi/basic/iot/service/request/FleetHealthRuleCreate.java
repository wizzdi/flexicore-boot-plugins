package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.ConditionJoinType;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.List;

public class FleetHealthRuleCreate extends BasicCreate {
    private String id;
    private Integer priority;
    private Boolean enabled;
    private ConditionJoinType conditionJoinType;
    private String resultingSeverityName;
    private Integer resultingSeverityValue;
    private Boolean humanInterventionRequired;
    private List<FleetHealthRuleConditionCreate> conditions;

    public String getId() { return id; }
    public <T extends FleetHealthRuleCreate> T setId(String id) { this.id = id; return (T) this; }
    public Integer getPriority() { return priority; }
    public <T extends FleetHealthRuleCreate> T setPriority(Integer priority) { this.priority = priority; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends FleetHealthRuleCreate> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
    public ConditionJoinType getConditionJoinType() { return conditionJoinType; }
    public <T extends FleetHealthRuleCreate> T setConditionJoinType(ConditionJoinType conditionJoinType) { this.conditionJoinType = conditionJoinType; return (T) this; }
    public String getResultingSeverityName() { return resultingSeverityName; }
    public <T extends FleetHealthRuleCreate> T setResultingSeverityName(String resultingSeverityName) { this.resultingSeverityName = resultingSeverityName; return (T) this; }
    public Integer getResultingSeverityValue() { return resultingSeverityValue; }
    public <T extends FleetHealthRuleCreate> T setResultingSeverityValue(Integer resultingSeverityValue) { this.resultingSeverityValue = resultingSeverityValue; return (T) this; }
    public Boolean getHumanInterventionRequired() { return humanInterventionRequired; }
    public <T extends FleetHealthRuleCreate> T setHumanInterventionRequired(Boolean humanInterventionRequired) { this.humanInterventionRequired = humanInterventionRequired; return (T) this; }
    public List<FleetHealthRuleConditionCreate> getConditions() { return conditions; }
    public <T extends FleetHealthRuleCreate> T setConditions(List<FleetHealthRuleConditionCreate> conditions) { this.conditions = conditions; return (T) this; }
}
