package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.List;

public class RemoteHealthProfileCreate extends BasicCreate {
    private String externalId;
    private Boolean enabled;
    private String defaultSeverityName;
    private Integer defaultSeverityValue;
    private Integer actionRequiredFromSeverityValue;
    private List<HealthSignalMappingCreate> mappings;
    private List<RemoteHealthRuleCreate> rules;

    public String getExternalId() { return externalId; }
    public <T extends RemoteHealthProfileCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends RemoteHealthProfileCreate> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
    public String getDefaultSeverityName() { return defaultSeverityName; }
    public <T extends RemoteHealthProfileCreate> T setDefaultSeverityName(String defaultSeverityName) { this.defaultSeverityName = defaultSeverityName; return (T) this; }
    public Integer getDefaultSeverityValue() { return defaultSeverityValue; }
    public <T extends RemoteHealthProfileCreate> T setDefaultSeverityValue(Integer defaultSeverityValue) { this.defaultSeverityValue = defaultSeverityValue; return (T) this; }
    public Integer getActionRequiredFromSeverityValue() { return actionRequiredFromSeverityValue; }
    public <T extends RemoteHealthProfileCreate> T setActionRequiredFromSeverityValue(Integer actionRequiredFromSeverityValue) { this.actionRequiredFromSeverityValue = actionRequiredFromSeverityValue; return (T) this; }
    public List<HealthSignalMappingCreate> getMappings() { return mappings; }
    public <T extends RemoteHealthProfileCreate> T setMappings(List<HealthSignalMappingCreate> mappings) { this.mappings = mappings; return (T) this; }
    public List<RemoteHealthRuleCreate> getRules() { return rules; }
    public <T extends RemoteHealthProfileCreate> T setRules(List<RemoteHealthRuleCreate> rules) { this.rules = rules; return (T) this; }
}
