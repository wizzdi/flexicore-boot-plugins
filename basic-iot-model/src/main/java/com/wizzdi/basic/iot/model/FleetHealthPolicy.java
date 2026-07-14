package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = @Index(name = "fleet_health_policy_external_id_idx", columnList = "externalId"))
public class FleetHealthPolicy extends Baseclass {

    private String externalId;
    private boolean enabled = true;
    private Integer minimumPopulation;
    private String defaultSeverityName;
    private Integer defaultSeverityValue;
    private Integer actionRequiredFromSeverityValue;
    private int evaluationVersion = 1;

    @Enumerated(EnumType.STRING)
    private FleetUnknownPolicy unknownPolicy = FleetUnknownPolicy.USE_DEFAULT;

    @Transient
    private List<FleetHealthRule> rules = new ArrayList<>();

    public String getExternalId() {
        return externalId;
    }

    public <T extends FleetHealthPolicy> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public <T extends FleetHealthPolicy> T setEnabled(boolean enabled) {
        this.enabled = enabled;
        return (T) this;
    }

    public Integer getMinimumPopulation() {
        return minimumPopulation;
    }

    public <T extends FleetHealthPolicy> T setMinimumPopulation(Integer minimumPopulation) {
        this.minimumPopulation = minimumPopulation;
        return (T) this;
    }

    public String getDefaultSeverityName() {
        return defaultSeverityName;
    }

    public <T extends FleetHealthPolicy> T setDefaultSeverityName(String defaultSeverityName) {
        this.defaultSeverityName = defaultSeverityName;
        return (T) this;
    }

    public Integer getDefaultSeverityValue() {
        return defaultSeverityValue;
    }

    public <T extends FleetHealthPolicy> T setDefaultSeverityValue(Integer defaultSeverityValue) {
        this.defaultSeverityValue = defaultSeverityValue;
        return (T) this;
    }

    public Integer getActionRequiredFromSeverityValue() {
        return actionRequiredFromSeverityValue;
    }

    public <T extends FleetHealthPolicy> T setActionRequiredFromSeverityValue(Integer actionRequiredFromSeverityValue) {
        this.actionRequiredFromSeverityValue = actionRequiredFromSeverityValue;
        return (T) this;
    }

    public int getEvaluationVersion() {
        return evaluationVersion;
    }

    public <T extends FleetHealthPolicy> T setEvaluationVersion(int evaluationVersion) {
        this.evaluationVersion = evaluationVersion;
        return (T) this;
    }

    public FleetUnknownPolicy getUnknownPolicy() {
        return unknownPolicy;
    }

    public <T extends FleetHealthPolicy> T setUnknownPolicy(FleetUnknownPolicy unknownPolicy) {
        this.unknownPolicy = unknownPolicy;
        return (T) this;
    }

    public List<FleetHealthRule> getRules() {
        return rules;
    }

    public <T extends FleetHealthPolicy> T setRules(List<FleetHealthRule> rules) {
        this.rules = rules;
        return (T) this;
    }
}
