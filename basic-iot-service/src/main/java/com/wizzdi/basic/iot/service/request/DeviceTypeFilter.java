package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class DeviceTypeFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> deviceTypeIds =new HashSet<>();
    private Set<String> externalIds = new HashSet<>();


    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends DeviceTypeFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getDeviceTypeIds() {
        return deviceTypeIds;
    }

    public <T extends DeviceTypeFilter> T setDeviceTypeIds(Set<String> deviceTypeIds) {
        this.deviceTypeIds = deviceTypeIds;
        return (T) this;
    }

    public Set<String> getExternalIds() { return externalIds; }

    public <T extends DeviceTypeFilter> T setExternalIds(Set<String> externalIds) { this.externalIds = externalIds; return (T) this; }

}

