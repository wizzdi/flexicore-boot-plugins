package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthSignalValueType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class HealthSignalDefinitionFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> healthSignalDefinitionIds = new HashSet<>();
    private Set<String> externalIds = new HashSet<>();
    private Set<HealthSignalValueType> valueTypes = new HashSet<>();
    private Boolean builtIn;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends HealthSignalDefinitionFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getHealthSignalDefinitionIds() { return healthSignalDefinitionIds; }
    public <T extends HealthSignalDefinitionFilter> T setHealthSignalDefinitionIds(Set<String> healthSignalDefinitionIds) { this.healthSignalDefinitionIds = healthSignalDefinitionIds; return (T) this; }
    public Set<String> getExternalIds() { return externalIds; }
    public <T extends HealthSignalDefinitionFilter> T setExternalIds(Set<String> externalIds) { this.externalIds = externalIds; return (T) this; }
    public Set<HealthSignalValueType> getValueTypes() { return valueTypes; }
    public <T extends HealthSignalDefinitionFilter> T setValueTypes(Set<HealthSignalValueType> valueTypes) { this.valueTypes = valueTypes; return (T) this; }
    public Boolean getBuiltIn() { return builtIn; }
    public <T extends HealthSignalDefinitionFilter> T setBuiltIn(Boolean builtIn) { this.builtIn = builtIn; return (T) this; }
}
