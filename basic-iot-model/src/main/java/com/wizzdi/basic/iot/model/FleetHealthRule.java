package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = @Index(name = "fleet_health_rule_policy_idx", columnList = "fleetHealthPolicy_id,priority,enabled"))
public class FleetHealthRule extends Baseclass {

    @ManyToOne(targetEntity = FleetHealthPolicy.class)
    @JsonIgnore
    private FleetHealthPolicy fleetHealthPolicy;

    private int priority;
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    private ConditionJoinType conditionJoinType = ConditionJoinType.ALL;

    private String resultingSeverityName;
    private Integer resultingSeverityValue;
    private boolean humanInterventionRequired;

    @Transient
    private List<FleetHealthRuleCondition> conditions = new ArrayList<>();

    public FleetHealthPolicy getFleetHealthPolicy() {
        return fleetHealthPolicy;
    }

    public <T extends FleetHealthRule> T setFleetHealthPolicy(FleetHealthPolicy fleetHealthPolicy) {
        this.fleetHealthPolicy = fleetHealthPolicy;
        return (T) this;
    }

    public int getPriority() {
        return priority;
    }

    public <T extends FleetHealthRule> T setPriority(int priority) {
        this.priority = priority;
        return (T) this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public <T extends FleetHealthRule> T setEnabled(boolean enabled) {
        this.enabled = enabled;
        return (T) this;
    }

    public ConditionJoinType getConditionJoinType() {
        return conditionJoinType;
    }

    public <T extends FleetHealthRule> T setConditionJoinType(ConditionJoinType conditionJoinType) {
        this.conditionJoinType = conditionJoinType;
        return (T) this;
    }

    public String getResultingSeverityName() {
        return resultingSeverityName;
    }

    public <T extends FleetHealthRule> T setResultingSeverityName(String resultingSeverityName) {
        this.resultingSeverityName = resultingSeverityName;
        return (T) this;
    }

    public Integer getResultingSeverityValue() {
        return resultingSeverityValue;
    }

    public <T extends FleetHealthRule> T setResultingSeverityValue(Integer resultingSeverityValue) {
        this.resultingSeverityValue = resultingSeverityValue;
        return (T) this;
    }

    public boolean isHumanInterventionRequired() {
        return humanInterventionRequired;
    }

    public <T extends FleetHealthRule> T setHumanInterventionRequired(boolean humanInterventionRequired) {
        this.humanInterventionRequired = humanInterventionRequired;
        return (T) this;
    }

    public List<FleetHealthRuleCondition> getConditions() {
        return conditions;
    }

    public <T extends FleetHealthRule> T setConditions(List<FleetHealthRuleCondition> conditions) {
        this.conditions = conditions;
        return (T) this;
    }
}
