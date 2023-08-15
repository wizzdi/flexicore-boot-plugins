package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Connectivity;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.dynamic.properties.converter.postgresql.DynamicFilterItem;
import com.wizzdi.maps.service.request.MappedPOIFilter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RemoteFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> remoteIds;
    private Set<Connectivity> connectivity;
    private Set<String> notIds;
    private OffsetDateTime lastSeenTo;

    private Map<String,DynamicFilterItem> devicePropertiesFilter;
    private Map<String,DynamicFilterItem> userAddedPropertiesFilter;


    private MappedPOIFilter mappedPOIFilter;


    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends RemoteFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getRemoteIds() {
        return remoteIds;
    }

    public <T extends RemoteFilter> T setRemoteIds(Set<String> remoteIds) {
        this.remoteIds = remoteIds;
        return (T) this;
    }

    public Set<Connectivity> getConnectivity() {
        return connectivity;
    }

    public <T extends RemoteFilter> T setConnectivity(Set<Connectivity> connectivity) {
        this.connectivity = connectivity;
        return (T) this;
    }

    @JsonIgnore
    public Set<String> getNotIds() {
        return notIds;
    }

    public <T extends RemoteFilter> T setNotIds(Set<String> notIds) {
        this.notIds = notIds;
        return (T) this;
    }

    public OffsetDateTime getLastSeenTo() {
        return lastSeenTo;
    }

    public <T extends RemoteFilter> T setLastSeenTo(OffsetDateTime lastSeenTo) {
        this.lastSeenTo = lastSeenTo;
        return (T) this;
    }

    public Map<String, DynamicFilterItem> getDevicePropertiesFilter() {
        return devicePropertiesFilter;
    }

    public <T extends RemoteFilter> T setDevicePropertiesFilter(Map<String, DynamicFilterItem> devicePropertiesFilter) {
        this.devicePropertiesFilter = devicePropertiesFilter;
        return (T) this;
    }

    public Map<String, DynamicFilterItem> getUserAddedPropertiesFilter() {
        return userAddedPropertiesFilter;
    }

    public <T extends RemoteFilter> T setUserAddedPropertiesFilter(Map<String, DynamicFilterItem> userAddedPropertiesFilter) {
        this.userAddedPropertiesFilter = userAddedPropertiesFilter;
        return (T) this;
    }

    public MappedPOIFilter getMappedPOIFilter() {
        return mappedPOIFilter;
    }

    public <T extends RemoteFilter> T setMappedPOIFilter(MappedPOIFilter mappedPOIFilter) {
        this.mappedPOIFilter = mappedPOIFilter;
        return (T) this;
    }
}
