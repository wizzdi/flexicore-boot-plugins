package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;
import com.wizzdi.basic.iot.model.HealthNotificationEventType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class HealthNotificationProviderTemplateFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> templateIds = new HashSet<>();
    private Set<String> configurationIds = new HashSet<>();
    private Set<HealthNotificationEventType> eventTypes = new HashSet<>();
    private Set<HealthNotificationDeliveryMode> deliveryModes = new HashSet<>();
    private Set<String> locales = new HashSet<>();
    private Boolean enabled;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends HealthNotificationProviderTemplateFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getTemplateIds() { return templateIds; }
    public <T extends HealthNotificationProviderTemplateFilter> T setTemplateIds(Set<String> templateIds) { this.templateIds = templateIds; return (T) this; }
    public Set<String> getConfigurationIds() { return configurationIds; }
    public <T extends HealthNotificationProviderTemplateFilter> T setConfigurationIds(Set<String> configurationIds) { this.configurationIds = configurationIds; return (T) this; }
    public Set<HealthNotificationEventType> getEventTypes() { return eventTypes; }
    public <T extends HealthNotificationProviderTemplateFilter> T setEventTypes(Set<HealthNotificationEventType> eventTypes) { this.eventTypes = eventTypes; return (T) this; }
    public Set<HealthNotificationDeliveryMode> getDeliveryModes() { return deliveryModes; }
    public <T extends HealthNotificationProviderTemplateFilter> T setDeliveryModes(Set<HealthNotificationDeliveryMode> deliveryModes) { this.deliveryModes = deliveryModes; return (T) this; }
    public Set<String> getLocales() { return locales; }
    public <T extends HealthNotificationProviderTemplateFilter> T setLocales(Set<String> locales) { this.locales = locales; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends HealthNotificationProviderTemplateFilter> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
}
