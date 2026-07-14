package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;
import com.wizzdi.basic.iot.model.HealthNotificationEventType;
import com.wizzdi.flexicore.security.request.BaseclassCreate;

public class SendGridHealthNotificationTemplateCreate extends BaseclassCreate {
    private String configurationId;
    private HealthNotificationEventType eventType;
    private HealthNotificationDeliveryMode deliveryMode;
    private String locale;
    private String templateId;
    private Boolean enabled;
    private Integer priority;

    public String getConfigurationId() { return configurationId; }
    public <T extends SendGridHealthNotificationTemplateCreate> T setConfigurationId(String configurationId) { this.configurationId = configurationId; return (T) this; }
    public HealthNotificationEventType getEventType() { return eventType; }
    public <T extends SendGridHealthNotificationTemplateCreate> T setEventType(HealthNotificationEventType eventType) { this.eventType = eventType; return (T) this; }
    public HealthNotificationDeliveryMode getDeliveryMode() { return deliveryMode; }
    public <T extends SendGridHealthNotificationTemplateCreate> T setDeliveryMode(HealthNotificationDeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; return (T) this; }
    public String getLocale() { return locale; }
    public <T extends SendGridHealthNotificationTemplateCreate> T setLocale(String locale) { this.locale = locale; return (T) this; }
    public String getTemplateId() { return templateId; }
    public <T extends SendGridHealthNotificationTemplateCreate> T setTemplateId(String templateId) { this.templateId = templateId; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends SendGridHealthNotificationTemplateCreate> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
    public Integer getPriority() { return priority; }
    public <T extends SendGridHealthNotificationTemplateCreate> T setPriority(Integer priority) { this.priority = priority; return (T) this; }
}
