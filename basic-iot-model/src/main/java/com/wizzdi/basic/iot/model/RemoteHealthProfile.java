package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = @Index(name = "remote_health_profile_external_id_idx", columnList = "externalId"))
public class RemoteHealthProfile extends Baseclass {

    private String externalId;
    private boolean enabled = true;
    private String defaultSeverityName;
    private Integer defaultSeverityValue;
    private Integer actionRequiredFromSeverityValue;
    private int evaluationVersion = 1;

    @Transient
    private List<HealthSignalMapping> mappings = new ArrayList<>();

    @Transient
    private List<RemoteHealthRule> rules = new ArrayList<>();

    public String getExternalId() {
        return externalId;
    }

    public <T extends RemoteHealthProfile> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public <T extends RemoteHealthProfile> T setEnabled(boolean enabled) {
        this.enabled = enabled;
        return (T) this;
    }

    public String getDefaultSeverityName() {
        return defaultSeverityName;
    }

    public <T extends RemoteHealthProfile> T setDefaultSeverityName(String defaultSeverityName) {
        this.defaultSeverityName = defaultSeverityName;
        return (T) this;
    }

    public Integer getDefaultSeverityValue() {
        return defaultSeverityValue;
    }

    public <T extends RemoteHealthProfile> T setDefaultSeverityValue(Integer defaultSeverityValue) {
        this.defaultSeverityValue = defaultSeverityValue;
        return (T) this;
    }

    public Integer getActionRequiredFromSeverityValue() {
        return actionRequiredFromSeverityValue;
    }

    public <T extends RemoteHealthProfile> T setActionRequiredFromSeverityValue(Integer actionRequiredFromSeverityValue) {
        this.actionRequiredFromSeverityValue = actionRequiredFromSeverityValue;
        return (T) this;
    }

    public int getEvaluationVersion() {
        return evaluationVersion;
    }

    public <T extends RemoteHealthProfile> T setEvaluationVersion(int evaluationVersion) {
        this.evaluationVersion = evaluationVersion;
        return (T) this;
    }

    public List<HealthSignalMapping> getMappings() {
        return mappings;
    }

    public <T extends RemoteHealthProfile> T setMappings(List<HealthSignalMapping> mappings) {
        this.mappings = mappings;
        return (T) this;
    }

    public List<RemoteHealthRule> getRules() {
        return rules;
    }

    public <T extends RemoteHealthProfile> T setRules(List<RemoteHealthRule> rules) {
        this.rules = rules;
        return (T) this;
    }
}
