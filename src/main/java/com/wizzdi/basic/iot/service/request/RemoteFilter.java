package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Connectivity;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class RemoteFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> remoteIds;
    private Set<Connectivity> connectivity;
    private Set<String> notIds;


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
}
