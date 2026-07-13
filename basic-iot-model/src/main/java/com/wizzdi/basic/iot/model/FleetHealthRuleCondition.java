package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = @Index(name = "fleet_health_condition_rule_idx", columnList = "fleetHealthRule_id,metricType"))
public class FleetHealthRuleCondition extends Baseclass {

    @ManyToOne(targetEntity = FleetHealthRule.class)
    @JsonIgnore
    private FleetHealthRule fleetHealthRule;

    @Enumerated(EnumType.STRING)
    private FleetHealthMetricType metricType;

    @Enumerated(EnumType.STRING)
    private HealthComparisonOperator operator;

    private Double threshold;
    private Double secondThreshold;
    private Integer severityThresholdValue;
    private Long staleAfterSeconds;
    private boolean requiredMembersOnly;

    @ManyToOne(targetEntity = RemoteRoleDefinition.class)
    private RemoteRoleDefinition memberRole;

    @Enumerated(EnumType.STRING)
    private FleetDenominatorPolicy denominatorPolicy = FleetDenominatorPolicy.ALL_ELIGIBLE_MEMBERS;

    public FleetHealthRule getFleetHealthRule() {
        return fleetHealthRule;
    }

    public <T extends FleetHealthRuleCondition> T setFleetHealthRule(FleetHealthRule fleetHealthRule) {
        this.fleetHealthRule = fleetHealthRule;
        return (T) this;
    }

    public FleetHealthMetricType getMetricType() {
        return metricType;
    }

    public <T extends FleetHealthRuleCondition> T setMetricType(FleetHealthMetricType metricType) {
        this.metricType = metricType;
        return (T) this;
    }

    public HealthComparisonOperator getOperator() {
        return operator;
    }

    public <T extends FleetHealthRuleCondition> T setOperator(HealthComparisonOperator operator) {
        this.operator = operator;
        return (T) this;
    }

    public Double getThreshold() {
        return threshold;
    }

    public <T extends FleetHealthRuleCondition> T setThreshold(Double threshold) {
        this.threshold = threshold;
        return (T) this;
    }

    public Double getSecondThreshold() {
        return secondThreshold;
    }

    public <T extends FleetHealthRuleCondition> T setSecondThreshold(Double secondThreshold) {
        this.secondThreshold = secondThreshold;
        return (T) this;
    }

    public Integer getSeverityThresholdValue() {
        return severityThresholdValue;
    }

    public <T extends FleetHealthRuleCondition> T setSeverityThresholdValue(Integer severityThresholdValue) {
        this.severityThresholdValue = severityThresholdValue;
        return (T) this;
    }

    public Long getStaleAfterSeconds() {
        return staleAfterSeconds;
    }

    public <T extends FleetHealthRuleCondition> T setStaleAfterSeconds(Long staleAfterSeconds) {
        this.staleAfterSeconds = staleAfterSeconds;
        return (T) this;
    }

    public boolean isRequiredMembersOnly() {
        return requiredMembersOnly;
    }

    public <T extends FleetHealthRuleCondition> T setRequiredMembersOnly(boolean requiredMembersOnly) {
        this.requiredMembersOnly = requiredMembersOnly;
        return (T) this;
    }

    public RemoteRoleDefinition getMemberRole() {
        return memberRole;
    }

    public <T extends FleetHealthRuleCondition> T setMemberRole(RemoteRoleDefinition memberRole) {
        this.memberRole = memberRole;
        return (T) this;
    }

    public FleetDenominatorPolicy getDenominatorPolicy() {
        return denominatorPolicy;
    }

    public <T extends FleetHealthRuleCondition> T setDenominatorPolicy(FleetDenominatorPolicy denominatorPolicy) {
        this.denominatorPolicy = denominatorPolicy;
        return (T) this;
    }
}
