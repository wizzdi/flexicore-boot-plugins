package com.flexicore.ui.response;

public class UiStyleAllowedValue {

    private String value;
    private String displayName;

    public UiStyleAllowedValue() {
    }

    public UiStyleAllowedValue(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public <T extends UiStyleAllowedValue> T setValue(String value) {
        this.value = value;
        return (T) this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public <T extends UiStyleAllowedValue> T setDisplayName(String displayName) {
        this.displayName = displayName;
        return (T) this;
    }
}
