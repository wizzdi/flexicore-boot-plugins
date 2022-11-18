package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StateSchemaFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> deviceTypeIds =new HashSet<>();
    @JsonIgnore
    private List<DeviceType> deviceTypes;
    private Integer version;
    private Integer lessThenVersion;


    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends StateSchemaFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getDeviceTypeIds() {
        return deviceTypeIds;
    }

    public <T extends StateSchemaFilter> T setDeviceTypeIds(Set<String> deviceTypeIds) {
        this.deviceTypeIds = deviceTypeIds;
        return (T) this;
    }

    @JsonIgnore
    public List<DeviceType> getDeviceTypes() {
        return deviceTypes;
    }

    public <T extends StateSchemaFilter> T setDeviceTypes(List<DeviceType> deviceTypes) {
        this.deviceTypes = deviceTypes;
        return (T) this;
    }

    public Integer getVersion() {
        return version;
    }

    public <T extends StateSchemaFilter> T setVersion(Integer version) {
        this.version = version;
        return (T) this;
    }

    public Integer getLessThenVersion() {
        return lessThenVersion;
    }

    public <T extends StateSchemaFilter> T setLessThenVersion(Integer lessThenVersion) {
        this.lessThenVersion = lessThenVersion;
        return (T) this;
    }
}
