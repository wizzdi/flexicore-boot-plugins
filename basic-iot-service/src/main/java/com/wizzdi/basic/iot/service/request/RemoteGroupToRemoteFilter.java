package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.RemoteGroupMembershipAction;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipSource;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public class RemoteGroupToRemoteFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> remoteGroupToRemoteIds = new HashSet<>();
    private Set<String> remoteGroupIds = new HashSet<>();
    private Set<String> remoteIds = new HashSet<>();
    private Set<String> roleIds = new HashSet<>();
    private Set<RemoteGroupMembershipAction> membershipActions = new HashSet<>();
    private Set<RemoteGroupMembershipSource> membershipSources = new HashSet<>();
    private OffsetDateTime activeAt;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends RemoteGroupToRemoteFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getRemoteGroupToRemoteIds() { return remoteGroupToRemoteIds; }
    public <T extends RemoteGroupToRemoteFilter> T setRemoteGroupToRemoteIds(Set<String> remoteGroupToRemoteIds) { this.remoteGroupToRemoteIds = remoteGroupToRemoteIds; return (T) this; }
    public Set<String> getRemoteGroupIds() { return remoteGroupIds; }
    public <T extends RemoteGroupToRemoteFilter> T setRemoteGroupIds(Set<String> remoteGroupIds) { this.remoteGroupIds = remoteGroupIds; return (T) this; }
    public Set<String> getRemoteIds() { return remoteIds; }
    public <T extends RemoteGroupToRemoteFilter> T setRemoteIds(Set<String> remoteIds) { this.remoteIds = remoteIds; return (T) this; }
    public Set<String> getRoleIds() { return roleIds; }
    public <T extends RemoteGroupToRemoteFilter> T setRoleIds(Set<String> roleIds) { this.roleIds = roleIds; return (T) this; }
    public Set<RemoteGroupMembershipAction> getMembershipActions() { return membershipActions; }
    public <T extends RemoteGroupToRemoteFilter> T setMembershipActions(Set<RemoteGroupMembershipAction> membershipActions) { this.membershipActions = membershipActions; return (T) this; }
    public Set<RemoteGroupMembershipSource> getMembershipSources() {
        return membershipSources;
    }

    public <T extends RemoteGroupToRemoteFilter> T setMembershipSources(Set<RemoteGroupMembershipSource> membershipSources) {
        this.membershipSources = membershipSources;
        return (T) this;
    }

    public OffsetDateTime getActiveAt() { return activeAt; }
    public <T extends RemoteGroupToRemoteFilter> T setActiveAt(OffsetDateTime activeAt) { this.activeAt = activeAt; return (T) this; }
}
