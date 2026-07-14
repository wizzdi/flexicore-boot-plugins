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

@Entity
@Table(indexes = @Index(name = "health_signal_mapping_profile_idx", columnList = "remoteHealthProfile_id,healthSignal_id,priority"))
public class HealthSignalMapping extends Baseclass {

    @ManyToOne(targetEntity = RemoteHealthProfile.class)
    @JsonIgnore
    private RemoteHealthProfile remoteHealthProfile;

    @ManyToOne(targetEntity = HealthSignalDefinition.class)
    private HealthSignalDefinition healthSignal;

    @Enumerated(EnumType.STRING)
    private HealthSignalSourceType sourceType;

    @ManyToOne(targetEntity = StatePropertyDefinition.class)
    private StatePropertyDefinition stateProperty;

    @Enumerated(EnumType.STRING)
    private HealthSignalMappingOperation operation = HealthSignalMappingOperation.DIRECT;

    private Double multiplier;
    @Column(name = "mappingOffset")
    private Double offset;
    private String expectedStringValue;
    private Boolean negateBoolean;
    private int priority;

    public RemoteHealthProfile getRemoteHealthProfile() {
        return remoteHealthProfile;
    }

    public <T extends HealthSignalMapping> T setRemoteHealthProfile(RemoteHealthProfile remoteHealthProfile) {
        this.remoteHealthProfile = remoteHealthProfile;
        return (T) this;
    }

    public HealthSignalDefinition getHealthSignal() {
        return healthSignal;
    }

    public <T extends HealthSignalMapping> T setHealthSignal(HealthSignalDefinition healthSignal) {
        this.healthSignal = healthSignal;
        return (T) this;
    }

    public HealthSignalSourceType getSourceType() {
        return sourceType;
    }

    public <T extends HealthSignalMapping> T setSourceType(HealthSignalSourceType sourceType) {
        this.sourceType = sourceType;
        return (T) this;
    }

    public StatePropertyDefinition getStateProperty() {
        return stateProperty;
    }

    public <T extends HealthSignalMapping> T setStateProperty(StatePropertyDefinition stateProperty) {
        this.stateProperty = stateProperty;
        return (T) this;
    }

    public HealthSignalMappingOperation getOperation() {
        return operation;
    }

    public <T extends HealthSignalMapping> T setOperation(HealthSignalMappingOperation operation) {
        this.operation = operation;
        return (T) this;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public <T extends HealthSignalMapping> T setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
        return (T) this;
    }

    public Double getOffset() {
        return offset;
    }

    public <T extends HealthSignalMapping> T setOffset(Double offset) {
        this.offset = offset;
        return (T) this;
    }

    public String getExpectedStringValue() {
        return expectedStringValue;
    }

    public <T extends HealthSignalMapping> T setExpectedStringValue(String expectedStringValue) {
        this.expectedStringValue = expectedStringValue;
        return (T) this;
    }

    public Boolean getNegateBoolean() {
        return negateBoolean;
    }

    public <T extends HealthSignalMapping> T setNegateBoolean(Boolean negateBoolean) {
        this.negateBoolean = negateBoolean;
        return (T) this;
    }

    public int getPriority() {
        return priority;
    }

    public <T extends HealthSignalMapping> T setPriority(int priority) {
        this.priority = priority;
        return (T) this;
    }
}
