package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class RemoteHealthProfileFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> remoteHealthProfileIds = new HashSet<>();
    private Set<String> externalIds = new HashSet<>();
    private Boolean enabled;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends RemoteHealthProfileFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getRemoteHealthProfileIds() { return remoteHealthProfileIds; }
    public <T extends RemoteHealthProfileFilter> T setRemoteHealthProfileIds(Set<String> remoteHealthProfileIds) { this.remoteHealthProfileIds = remoteHealthProfileIds; return (T) this; }
    public Set<String> getExternalIds() { return externalIds; }
    public <T extends RemoteHealthProfileFilter> T setExternalIds(Set<String> externalIds) { this.externalIds = externalIds; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends RemoteHealthProfileFilter> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
}
