package com.flexicore.ui.response;

import com.flexicore.ui.model.UiStyleValueType;

import java.util.ArrayList;
import java.util.List;

public class UiStylePropertyDefinition {

    private String key;
    private String targetType;
    private String section;
    private String displayName;
    private String description;
    private UiStyleValueType valueType;
    private List<UiStyleAllowedValue> allowedValues = new ArrayList<>();
    private Double minimum;
    private Double maximum;
    private Double step;
    private String unit;
    private String defaultStringValue;
    private Double defaultNumericValue;
    private Boolean defaultBooleanValue;
    private int priority;

    public String getKey() {
        return key;
    }

    public <T extends UiStylePropertyDefinition> T setKey(String key) {
        this.key = key;
        return (T) this;
    }

    public String getTargetType() {
        return targetType;
    }

    public <T extends UiStylePropertyDefinition> T setTargetType(String targetType) {
        this.targetType = targetType;
        return (T) this;
    }

    public String getSection() {
        return section;
    }

    public <T extends UiStylePropertyDefinition> T setSection(String section) {
        this.section = section;
        return (T) this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public <T extends UiStylePropertyDefinition> T setDisplayName(String displayName) {
        this.displayName = displayName;
        return (T) this;
    }

    public String getDescription() {
        return description;
    }

    public <T extends UiStylePropertyDefinition> T setDescription(String description) {
        this.description = description;
        return (T) this;
    }

    public UiStyleValueType getValueType() {
        return valueType;
    }

    public <T extends UiStylePropertyDefinition> T setValueType(UiStyleValueType valueType) {
        this.valueType = valueType;
        return (T) this;
    }

    public List<UiStyleAllowedValue> getAllowedValues() {
        return allowedValues;
    }

    public <T extends UiStylePropertyDefinition> T setAllowedValues(List<UiStyleAllowedValue> allowedValues) {
        this.allowedValues = allowedValues != null ? new ArrayList<>(allowedValues) : new ArrayList<>();
        return (T) this;
    }

    public Double getMinimum() {
        return minimum;
    }

    public <T extends UiStylePropertyDefinition> T setMinimum(Double minimum) {
        this.minimum = minimum;
        return (T) this;
    }

    public Double getMaximum() {
        return maximum;
    }

    public <T extends UiStylePropertyDefinition> T setMaximum(Double maximum) {
        this.maximum = maximum;
        return (T) this;
    }

    public Double getStep() {
        return step;
    }

    public <T extends UiStylePropertyDefinition> T setStep(Double step) {
        this.step = step;
        return (T) this;
    }

    public String getUnit() {
        return unit;
    }

    public <T extends UiStylePropertyDefinition> T setUnit(String unit) {
        this.unit = unit;
        return (T) this;
    }

    public String getDefaultStringValue() {
        return defaultStringValue;
    }

    public <T extends UiStylePropertyDefinition> T setDefaultStringValue(String defaultStringValue) {
        this.defaultStringValue = defaultStringValue;
        return (T) this;
    }

    public Double getDefaultNumericValue() {
        return defaultNumericValue;
    }

    public <T extends UiStylePropertyDefinition> T setDefaultNumericValue(Double defaultNumericValue) {
        this.defaultNumericValue = defaultNumericValue;
        return (T) this;
    }

    public Boolean getDefaultBooleanValue() {
        return defaultBooleanValue;
    }

    public <T extends UiStylePropertyDefinition> T setDefaultBooleanValue(Boolean defaultBooleanValue) {
        this.defaultBooleanValue = defaultBooleanValue;
        return (T) this;
    }

    public int getPriority() {
        return priority;
    }

    public <T extends UiStylePropertyDefinition> T setPriority(int priority) {
        this.priority = priority;
        return (T) this;
    }
}
