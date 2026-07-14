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
@Table(indexes = @Index(name = "remote_health_condition_rule_idx", columnList = "remoteHealthRule_id,healthSignal_id"))
public class RemoteHealthRuleCondition extends Baseclass {

    @ManyToOne(targetEntity = RemoteHealthRule.class)
    @JsonIgnore
    private RemoteHealthRule remoteHealthRule;

    @ManyToOne(targetEntity = HealthSignalDefinition.class)
    private HealthSignalDefinition healthSignal;

    @Enumerated(EnumType.STRING)
    private HealthComparisonOperator operator;

    private Double numericValue;
    private Double secondNumericValue;
    private String stringValue;
    private Boolean booleanValue;

    @Enumerated(EnumType.STRING)
    private MissingSignalBehavior missingSignalBehavior = MissingSignalBehavior.NO_MATCH;

    private Double defaultNumericValue;
    private String defaultStringValue;
    private Boolean defaultBooleanValue;
    private int priority;

    public RemoteHealthRule getRemoteHealthRule() {
        return remoteHealthRule;
    }

    public <T extends RemoteHealthRuleCondition> T setRemoteHealthRule(RemoteHealthRule remoteHealthRule) {
        this.remoteHealthRule = remoteHealthRule;
        return (T) this;
    }

    public HealthSignalDefinition getHealthSignal() {
        return healthSignal;
    }

    public <T extends RemoteHealthRuleCondition> T setHealthSignal(HealthSignalDefinition healthSignal) {
        this.healthSignal = healthSignal;
        return (T) this;
    }

    public HealthComparisonOperator getOperator() {
        return operator;
    }

    public <T extends RemoteHealthRuleCondition> T setOperator(HealthComparisonOperator operator) {
        this.operator = operator;
        return (T) this;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public <T extends RemoteHealthRuleCondition> T setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
        return (T) this;
    }

    public Double getSecondNumericValue() {
        return secondNumericValue;
    }

    public <T extends RemoteHealthRuleCondition> T setSecondNumericValue(Double secondNumericValue) {
        this.secondNumericValue = secondNumericValue;
        return (T) this;
    }

    public String getStringValue() {
        return stringValue;
    }

    public <T extends RemoteHealthRuleCondition> T setStringValue(String stringValue) {
        this.stringValue = stringValue;
        return (T) this;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public <T extends RemoteHealthRuleCondition> T setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
        return (T) this;
    }

    public MissingSignalBehavior getMissingSignalBehavior() {
        return missingSignalBehavior;
    }

    public <T extends RemoteHealthRuleCondition> T setMissingSignalBehavior(MissingSignalBehavior missingSignalBehavior) {
        this.missingSignalBehavior = missingSignalBehavior;
        return (T) this;
    }

    public Double getDefaultNumericValue() {
        return defaultNumericValue;
    }

    public <T extends RemoteHealthRuleCondition> T setDefaultNumericValue(Double defaultNumericValue) {
        this.defaultNumericValue = defaultNumericValue;
        return (T) this;
    }

    public String getDefaultStringValue() {
        return defaultStringValue;
    }

    public <T extends RemoteHealthRuleCondition> T setDefaultStringValue(String defaultStringValue) {
        this.defaultStringValue = defaultStringValue;
        return (T) this;
    }

    public Boolean getDefaultBooleanValue() {
        return defaultBooleanValue;
    }

    public <T extends RemoteHealthRuleCondition> T setDefaultBooleanValue(Boolean defaultBooleanValue) {
        this.defaultBooleanValue = defaultBooleanValue;
        return (T) this;
    }

    public int getPriority() {
        return priority;
    }

    public <T extends RemoteHealthRuleCondition> T setPriority(int priority) {
        this.priority = priority;
        return (T) this;
    }
}
