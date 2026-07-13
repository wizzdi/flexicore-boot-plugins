package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class UiStyleProperty extends Baseclass {

    @ManyToOne(targetEntity = UiStyle.class)
    @JsonIgnore
    private UiStyle uiStyle;

    private String propertyKey;
    private String stringValue;
    private Double numericValue;
    private Boolean booleanValue;
    private int priority;

    public UiStyleProperty() {
    }

    @ManyToOne(targetEntity = UiStyle.class)
    @JsonIgnore
    public UiStyle getUiStyle() {
        return uiStyle;
    }

    public <T extends UiStyleProperty> T setUiStyle(UiStyle uiStyle) {
        this.uiStyle = uiStyle;
        return (T) this;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public <T extends UiStyleProperty> T setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
        return (T) this;
    }

    public String getStringValue() {
        return stringValue;
    }

    public <T extends UiStyleProperty> T setStringValue(String stringValue) {
        this.stringValue = stringValue;
        return (T) this;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public <T extends UiStyleProperty> T setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
        return (T) this;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public <T extends UiStyleProperty> T setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
        return (T) this;
    }

    public int getPriority() {
        return priority;
    }

    public <T extends UiStyleProperty> T setPriority(int priority) {
        this.priority = priority;
        return (T) this;
    }
}
