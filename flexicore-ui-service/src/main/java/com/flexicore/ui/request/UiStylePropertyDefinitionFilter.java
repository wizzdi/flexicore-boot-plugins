package com.flexicore.ui.request;

import com.flexicore.annotations.TypeRetention;

import java.util.HashSet;
import java.util.Set;

public class UiStylePropertyDefinitionFilter {

    @TypeRetention(String.class)
    private Set<String> targetTypes = new HashSet<>();

    public Set<String> getTargetTypes() {
        return targetTypes;
    }

    public <T extends UiStylePropertyDefinitionFilter> T setTargetTypes(Set<String> targetTypes) {
        this.targetTypes = targetTypes != null ? new HashSet<>(targetTypes) : new HashSet<>();
        return (T) this;
    }
}
