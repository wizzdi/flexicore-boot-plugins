package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthNotificationScopeType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.Set;

public class HealthNotificationPolicyFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> healthNotificationPolicyIds;
    private Set<String> userIds;
    private Set<HealthNotificationScopeType> scopeTypes;
    private Set<String> remoteIds;
    private Set<String> remoteGroupIds;
    private Set<String> deviceTypeIds;
    private Boolean enabled;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends HealthNotificationPolicyFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getHealthNotificationPolicyIds() { return healthNotificationPolicyIds; }
    public <T extends HealthNotificationPolicyFilter> T setHealthNotificationPolicyIds(Set<String> healthNotificationPolicyIds) { this.healthNotificationPolicyIds = healthNotificationPolicyIds; return (T) this; }
    public Set<String> getUserIds() { return userIds; }
    public <T extends HealthNotificationPolicyFilter> T setUserIds(Set<String> userIds) { this.userIds = userIds; return (T) this; }
    public Set<HealthNotificationScopeType> getScopeTypes() { return scopeTypes; }
    public <T extends HealthNotificationPolicyFilter> T setScopeTypes(Set<HealthNotificationScopeType> scopeTypes) { this.scopeTypes = scopeTypes; return (T) this; }
    public Set<String> getRemoteIds() { return remoteIds; }
    public <T extends HealthNotificationPolicyFilter> T setRemoteIds(Set<String> remoteIds) { this.remoteIds = remoteIds; return (T) this; }
    public Set<String> getRemoteGroupIds() { return remoteGroupIds; }
    public <T extends HealthNotificationPolicyFilter> T setRemoteGroupIds(Set<String> remoteGroupIds) { this.remoteGroupIds = remoteGroupIds; return (T) this; }
    public Set<String> getDeviceTypeIds() { return deviceTypeIds; }
    public <T extends HealthNotificationPolicyFilter> T setDeviceTypeIds(Set<String> deviceTypeIds) { this.deviceTypeIds = deviceTypeIds; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends HealthNotificationPolicyFilter> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
}
