package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.StateSchema;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SchemaActionFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> deviceTypeIds =new HashSet<>();
    @JsonIgnore
    private List<DeviceType> deviceTypes;

    private Set<String> stateSchemaIds =new HashSet<>();
    @JsonIgnore
    private List<StateSchema> stateSchemas;

    private Set<String> externalIds;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends SchemaActionFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getDeviceTypeIds() {
        return deviceTypeIds;
    }

    public <T extends SchemaActionFilter> T setDeviceTypeIds(Set<String> deviceTypeIds) {
        this.deviceTypeIds = deviceTypeIds;
        return (T) this;
    }
    @JsonIgnore
    public List<DeviceType> getDeviceTypes() {
        return deviceTypes;
    }

    public <T extends SchemaActionFilter> T setDeviceTypes(List<DeviceType> deviceTypes) {
        this.deviceTypes = deviceTypes;
        return (T) this;
    }

    public Set<String> getStateSchemaIds() {
        return stateSchemaIds;
    }

    public <T extends SchemaActionFilter> T setStateSchemaIds(Set<String> stateSchemaIds) {
        this.stateSchemaIds = stateSchemaIds;
        return (T) this;
    }

    @JsonIgnore
    public List<StateSchema> getStateSchemas() {
        return stateSchemas;
    }

    public <T extends SchemaActionFilter> T setStateSchemas(List<StateSchema> stateSchemas) {
        this.stateSchemas = stateSchemas;
        return (T) this;
    }

    public Set<String> getExternalIds() {
        return externalIds;
    }

    public <T extends SchemaActionFilter> T setExternalIds(Set<String> externalIds) {
        this.externalIds = externalIds;
        return (T) this;
    }
}
