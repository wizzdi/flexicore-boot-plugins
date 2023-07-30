package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectivityChangeFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> remoteIds=new HashSet<>();
    @JsonIgnore
    private List<Remote> remotes;


    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends ConnectivityChangeFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getRemoteIds() {
        return remoteIds;
    }

    public <T extends ConnectivityChangeFilter> T setRemoteIds(Set<String> remoteIds) {
        this.remoteIds = remoteIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Remote> getRemotes() {
        return remotes;
    }

    public <T extends ConnectivityChangeFilter> T setRemotes(List<Remote> remotes) {
        this.remotes = remotes;
        return (T) this;
    }
}
