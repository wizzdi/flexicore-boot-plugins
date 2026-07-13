package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class RemoteRoleDefinitionFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> remoteRoleDefinitionIds = new HashSet<>();
    private Set<String> externalIds = new HashSet<>();

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends RemoteRoleDefinitionFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getRemoteRoleDefinitionIds() { return remoteRoleDefinitionIds; }
    public <T extends RemoteRoleDefinitionFilter> T setRemoteRoleDefinitionIds(Set<String> remoteRoleDefinitionIds) { this.remoteRoleDefinitionIds = remoteRoleDefinitionIds; return (T) this; }
    public Set<String> getExternalIds() { return externalIds; }
    public <T extends RemoteRoleDefinitionFilter> T setExternalIds(Set<String> externalIds) { this.externalIds = externalIds; return (T) this; }
}
