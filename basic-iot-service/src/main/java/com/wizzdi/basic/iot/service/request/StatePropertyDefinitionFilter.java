package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthSignalValueType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class StatePropertyDefinitionFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> statePropertyDefinitionIds = new HashSet<>();
    private Set<String> stateSchemaIds = new HashSet<>();
    private Set<String> externalIds = new HashSet<>();
    private Set<HealthSignalValueType> valueTypes = new HashSet<>();

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends StatePropertyDefinitionFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getStatePropertyDefinitionIds() { return statePropertyDefinitionIds; }
    public <T extends StatePropertyDefinitionFilter> T setStatePropertyDefinitionIds(Set<String> statePropertyDefinitionIds) { this.statePropertyDefinitionIds = statePropertyDefinitionIds; return (T) this; }
    public Set<String> getStateSchemaIds() { return stateSchemaIds; }
    public <T extends StatePropertyDefinitionFilter> T setStateSchemaIds(Set<String> stateSchemaIds) { this.stateSchemaIds = stateSchemaIds; return (T) this; }
    public Set<String> getExternalIds() { return externalIds; }
    public <T extends StatePropertyDefinitionFilter> T setExternalIds(Set<String> externalIds) { this.externalIds = externalIds; return (T) this; }
    public Set<HealthSignalValueType> getValueTypes() { return valueTypes; }
    public <T extends StatePropertyDefinitionFilter> T setValueTypes(Set<HealthSignalValueType> valueTypes) { this.valueTypes = valueTypes; return (T) this; }
}
