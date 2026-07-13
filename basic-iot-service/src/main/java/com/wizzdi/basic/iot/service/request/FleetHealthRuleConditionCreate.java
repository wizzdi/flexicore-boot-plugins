package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.FleetDenominatorPolicy;
import com.wizzdi.basic.iot.model.FleetHealthMetricType;
import com.wizzdi.basic.iot.model.HealthComparisonOperator;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class FleetHealthRuleConditionCreate extends BasicCreate {
    private String id;
    private FleetHealthMetricType metricType;
    private HealthComparisonOperator operator;
    private Double threshold;
    private Double secondThreshold;
    private Integer severityThresholdValue;
    private Long staleAfterSeconds;
    private Boolean requiredMembersOnly;
    private String memberRoleId;
    private FleetDenominatorPolicy denominatorPolicy;

    public String getId() { return id; }
    public <T extends FleetHealthRuleConditionCreate> T setId(String id) { this.id = id; return (T) this; }
    public FleetHealthMetricType getMetricType() { return metricType; }
    public <T extends FleetHealthRuleConditionCreate> T setMetricType(FleetHealthMetricType metricType) { this.metricType = metricType; return (T) this; }
    public HealthComparisonOperator getOperator() { return operator; }
    public <T extends FleetHealthRuleConditionCreate> T setOperator(HealthComparisonOperator operator) { this.operator = operator; return (T) this; }
    public Double getThreshold() { return threshold; }
    public <T extends FleetHealthRuleConditionCreate> T setThreshold(Double threshold) { this.threshold = threshold; return (T) this; }
    public Double getSecondThreshold() { return secondThreshold; }
    public <T extends FleetHealthRuleConditionCreate> T setSecondThreshold(Double secondThreshold) { this.secondThreshold = secondThreshold; return (T) this; }
    public Integer getSeverityThresholdValue() { return severityThresholdValue; }
    public <T extends FleetHealthRuleConditionCreate> T setSeverityThresholdValue(Integer severityThresholdValue) { this.severityThresholdValue = severityThresholdValue; return (T) this; }
    public Long getStaleAfterSeconds() { return staleAfterSeconds; }
    public <T extends FleetHealthRuleConditionCreate> T setStaleAfterSeconds(Long staleAfterSeconds) { this.staleAfterSeconds = staleAfterSeconds; return (T) this; }
    public Boolean getRequiredMembersOnly() { return requiredMembersOnly; }
    public <T extends FleetHealthRuleConditionCreate> T setRequiredMembersOnly(Boolean requiredMembersOnly) { this.requiredMembersOnly = requiredMembersOnly; return (T) this; }
    public String getMemberRoleId() { return memberRoleId; }
    public <T extends FleetHealthRuleConditionCreate> T setMemberRoleId(String memberRoleId) { this.memberRoleId = memberRoleId; return (T) this; }
    public FleetDenominatorPolicy getDenominatorPolicy() { return denominatorPolicy; }
    public <T extends FleetHealthRuleConditionCreate> T setDenominatorPolicy(FleetDenominatorPolicy denominatorPolicy) { this.denominatorPolicy = denominatorPolicy; return (T) this; }
}
