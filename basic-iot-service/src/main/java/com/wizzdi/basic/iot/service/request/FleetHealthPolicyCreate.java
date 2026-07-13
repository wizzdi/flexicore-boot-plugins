package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.FleetUnknownPolicy;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.List;

public class FleetHealthPolicyCreate extends BasicCreate {
    private String externalId;
    private Boolean enabled;
    private Integer minimumPopulation;
    private String defaultSeverityName;
    private Integer defaultSeverityValue;
    private FleetUnknownPolicy unknownPolicy;
    private List<FleetHealthRuleCreate> rules;

    public String getExternalId() { return externalId; }
    public <T extends FleetHealthPolicyCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends FleetHealthPolicyCreate> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
    public Integer getMinimumPopulation() { return minimumPopulation; }
    public <T extends FleetHealthPolicyCreate> T setMinimumPopulation(Integer minimumPopulation) { this.minimumPopulation = minimumPopulation; return (T) this; }
    public String getDefaultSeverityName() { return defaultSeverityName; }
    public <T extends FleetHealthPolicyCreate> T setDefaultSeverityName(String defaultSeverityName) { this.defaultSeverityName = defaultSeverityName; return (T) this; }
    public Integer getDefaultSeverityValue() { return defaultSeverityValue; }
    public <T extends FleetHealthPolicyCreate> T setDefaultSeverityValue(Integer defaultSeverityValue) { this.defaultSeverityValue = defaultSeverityValue; return (T) this; }
    public FleetUnknownPolicy getUnknownPolicy() { return unknownPolicy; }
    public <T extends FleetHealthPolicyCreate> T setUnknownPolicy(FleetUnknownPolicy unknownPolicy) { this.unknownPolicy = unknownPolicy; return (T) this; }
    public List<FleetHealthRuleCreate> getRules() { return rules; }
    public <T extends FleetHealthPolicyCreate> T setRules(List<FleetHealthRuleCreate> rules) { this.rules = rules; return (T) this; }
}
