package com.flexicore.ui.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class UiStyleFiltering extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    @TypeRetention(String.class)
    private Set<String> targetTypes = new HashSet<>();
    @TypeRetention(String.class)
    private Set<String> propertyKeys = new HashSet<>();
    @JsonIgnore
    private Set<String> resolvedPropertyKeys = new HashSet<>();
    @JsonIgnore
    private boolean propertyConstraint;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends UiStyleFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getTargetTypes() {
        return targetTypes;
    }

    public <T extends UiStyleFiltering> T setTargetTypes(Set<String> targetTypes) {
        this.targetTypes = targetTypes != null ? new HashSet<>(targetTypes) : new HashSet<>();
        return (T) this;
    }

    public Set<String> getPropertyKeys() {
        return propertyKeys;
    }

    public <T extends UiStyleFiltering> T setPropertyKeys(Set<String> propertyKeys) {
        this.propertyKeys = propertyKeys != null ? new HashSet<>(propertyKeys) : new HashSet<>();
        return (T) this;
    }

    @JsonIgnore
    public Set<String> getResolvedPropertyKeys() {
        return resolvedPropertyKeys;
    }

    public <T extends UiStyleFiltering> T setResolvedPropertyKeys(Set<String> resolvedPropertyKeys) {
        this.resolvedPropertyKeys = resolvedPropertyKeys != null ? new HashSet<>(resolvedPropertyKeys) : new HashSet<>();
        return (T) this;
    }

    @JsonIgnore
    public boolean isPropertyConstraint() {
        return propertyConstraint;
    }

    public <T extends UiStyleFiltering> T setPropertyConstraint(boolean propertyConstraint) {
        this.propertyConstraint = propertyConstraint;
        return (T) this;
    }
}
