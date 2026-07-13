package com.flexicore.ui.request;

public class UiStylePropertyCreate {

    private String propertyKey;
    private String stringValue;
    private Double numericValue;
    private Boolean booleanValue;

    public String getPropertyKey() {
        return propertyKey;
    }

    public <T extends UiStylePropertyCreate> T setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
        return (T) this;
    }

    public String getStringValue() {
        return stringValue;
    }

    public <T extends UiStylePropertyCreate> T setStringValue(String stringValue) {
        this.stringValue = stringValue;
        return (T) this;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public <T extends UiStylePropertyCreate> T setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
        return (T) this;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public <T extends UiStylePropertyCreate> T setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
        return (T) this;
    }
}
