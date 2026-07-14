package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Column;
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
@Table(indexes = @Index(name = "remote_health_rule_profile_idx", columnList = "remoteHealthProfile_id,priority,enabled"))
public class RemoteHealthRule extends Baseclass {

    @ManyToOne(targetEntity = RemoteHealthProfile.class)
    @JsonIgnore
    private RemoteHealthProfile remoteHealthProfile;

    private int priority;
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    private ConditionJoinType conditionJoinType = ConditionJoinType.ALL;

    private String resultingSeverityName;
    private Integer resultingSeverityValue;
    private boolean humanInterventionRequired;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(columnDefinition = "text")
    private String mitigationInstructions;

    @Transient
    private List<RemoteHealthRuleCondition> conditions = new ArrayList<>();

    public RemoteHealthProfile getRemoteHealthProfile() {
        return remoteHealthProfile;
    }

    public <T extends RemoteHealthRule> T setRemoteHealthProfile(RemoteHealthProfile remoteHealthProfile) {
        this.remoteHealthProfile = remoteHealthProfile;
        return (T) this;
    }

    public int getPriority() {
        return priority;
    }

    public <T extends RemoteHealthRule> T setPriority(int priority) {
        this.priority = priority;
        return (T) this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public <T extends RemoteHealthRule> T setEnabled(boolean enabled) {
        this.enabled = enabled;
        return (T) this;
    }

    public ConditionJoinType getConditionJoinType() {
        return conditionJoinType;
    }

    public <T extends RemoteHealthRule> T setConditionJoinType(ConditionJoinType conditionJoinType) {
        this.conditionJoinType = conditionJoinType;
        return (T) this;
    }

    public String getResultingSeverityName() {
        return resultingSeverityName;
    }

    public <T extends RemoteHealthRule> T setResultingSeverityName(String resultingSeverityName) {
        this.resultingSeverityName = resultingSeverityName;
        return (T) this;
    }

    public Integer getResultingSeverityValue() {
        return resultingSeverityValue;
    }

    public <T extends RemoteHealthRule> T setResultingSeverityValue(Integer resultingSeverityValue) {
        this.resultingSeverityValue = resultingSeverityValue;
        return (T) this;
    }

    public boolean isHumanInterventionRequired() {
        return humanInterventionRequired;
    }

    public <T extends RemoteHealthRule> T setHumanInterventionRequired(boolean humanInterventionRequired) {
        this.humanInterventionRequired = humanInterventionRequired;
        return (T) this;
    }

    public String getSummary() {
        return summary;
    }

    public <T extends RemoteHealthRule> T setSummary(String summary) {
        this.summary = summary;
        return (T) this;
    }

    public String getMitigationInstructions() {
        return mitigationInstructions;
    }

    public <T extends RemoteHealthRule> T setMitigationInstructions(String mitigationInstructions) {
        this.mitigationInstructions = mitigationInstructions;
        return (T) this;
    }

    public List<RemoteHealthRuleCondition> getConditions() {
        return conditions;
    }

    public <T extends RemoteHealthRule> T setConditions(List<RemoteHealthRuleCondition> conditions) {
        this.conditions = conditions;
        return (T) this;
    }
}
