package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class HealthNotificationProviderConfigurationFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> configurationIds = new HashSet<>();
    private Boolean enabled;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends HealthNotificationProviderConfigurationFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getConfigurationIds() { return configurationIds; }
    public <T extends HealthNotificationProviderConfigurationFilter> T setConfigurationIds(Set<String> configurationIds) { this.configurationIds = configurationIds; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends HealthNotificationProviderConfigurationFilter> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
}
