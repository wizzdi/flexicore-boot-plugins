package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthComparisonOperator;
import com.wizzdi.basic.iot.model.MissingSignalBehavior;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class RemoteHealthRuleConditionCreate extends BasicCreate {
    private String id;
    private String healthSignalId;
    private HealthComparisonOperator operator;
    private Double numericValue;
    private Double secondNumericValue;
    private String stringValue;
    private Boolean booleanValue;
    private MissingSignalBehavior missingSignalBehavior;
    private Double defaultNumericValue;
    private String defaultStringValue;
    private Boolean defaultBooleanValue;
    private Integer priority;

    public String getId() { return id; }
    public <T extends RemoteHealthRuleConditionCreate> T setId(String id) { this.id = id; return (T) this; }
    public String getHealthSignalId() { return healthSignalId; }
    public <T extends RemoteHealthRuleConditionCreate> T setHealthSignalId(String healthSignalId) { this.healthSignalId = healthSignalId; return (T) this; }
    public HealthComparisonOperator getOperator() { return operator; }
    public <T extends RemoteHealthRuleConditionCreate> T setOperator(HealthComparisonOperator operator) { this.operator = operator; return (T) this; }
    public Double getNumericValue() { return numericValue; }
    public <T extends RemoteHealthRuleConditionCreate> T setNumericValue(Double numericValue) { this.numericValue = numericValue; return (T) this; }
    public Double getSecondNumericValue() { return secondNumericValue; }
    public <T extends RemoteHealthRuleConditionCreate> T setSecondNumericValue(Double secondNumericValue) { this.secondNumericValue = secondNumericValue; return (T) this; }
    public String getStringValue() { return stringValue; }
    public <T extends RemoteHealthRuleConditionCreate> T setStringValue(String stringValue) { this.stringValue = stringValue; return (T) this; }
    public Boolean getBooleanValue() { return booleanValue; }
    public <T extends RemoteHealthRuleConditionCreate> T setBooleanValue(Boolean booleanValue) { this.booleanValue = booleanValue; return (T) this; }
    public MissingSignalBehavior getMissingSignalBehavior() { return missingSignalBehavior; }
    public <T extends RemoteHealthRuleConditionCreate> T setMissingSignalBehavior(MissingSignalBehavior missingSignalBehavior) { this.missingSignalBehavior = missingSignalBehavior; return (T) this; }
    public Double getDefaultNumericValue() { return defaultNumericValue; }
    public <T extends RemoteHealthRuleConditionCreate> T setDefaultNumericValue(Double defaultNumericValue) { this.defaultNumericValue = defaultNumericValue; return (T) this; }
    public String getDefaultStringValue() { return defaultStringValue; }
    public <T extends RemoteHealthRuleConditionCreate> T setDefaultStringValue(String defaultStringValue) { this.defaultStringValue = defaultStringValue; return (T) this; }
    public Boolean getDefaultBooleanValue() { return defaultBooleanValue; }
    public <T extends RemoteHealthRuleConditionCreate> T setDefaultBooleanValue(Boolean defaultBooleanValue) { this.defaultBooleanValue = defaultBooleanValue; return (T) this; }
    public Integer getPriority() { return priority; }
    public <T extends RemoteHealthRuleConditionCreate> T setPriority(Integer priority) { this.priority = priority; return (T) this; }
}
