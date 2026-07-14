package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;
import com.wizzdi.basic.iot.model.HealthNotificationEventType;
import com.wizzdi.flexicore.security.request.BaseclassCreate;

import java.util.List;

public class WhatsAppHealthNotificationTemplateCreate extends BaseclassCreate {
    private String configurationId;
    private HealthNotificationEventType eventType;
    private HealthNotificationDeliveryMode deliveryMode;
    private String locale;
    private String templateName;
    private String languageCode;
    private Boolean enabled;
    private Integer priority;
    private List<WhatsAppHealthNotificationTemplateParameterCreate> parameters;

    public String getConfigurationId() { return configurationId; }
    public <T extends WhatsAppHealthNotificationTemplateCreate> T setConfigurationId(String configurationId) { this.configurationId = configurationId; return (T) this; }
    public HealthNotificationEventType getEventType() { return eventType; }
    public <T extends WhatsAppHealthNotificationTemplateCreate> T setEventType(HealthNotificationEventType eventType) { this.eventType = eventType; return (T) this; }
    public HealthNotificationDeliveryMode getDeliveryMode() { return deliveryMode; }
    public <T extends WhatsAppHealthNotificationTemplateCreate> T setDeliveryMode(HealthNotificationDeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; return (T) this; }
    public String getLocale() { return locale; }
    public <T extends WhatsAppHealthNotificationTemplateCreate> T setLocale(String locale) { this.locale = locale; return (T) this; }
    public String getTemplateName() { return templateName; }
    public <T extends WhatsAppHealthNotificationTemplateCreate> T setTemplateName(String templateName) { this.templateName = templateName; return (T) this; }
    public String getLanguageCode() { return languageCode; }
    public <T extends WhatsAppHealthNotificationTemplateCreate> T setLanguageCode(String languageCode) { this.languageCode = languageCode; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends WhatsAppHealthNotificationTemplateCreate> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
    public Integer getPriority() { return priority; }
    public <T extends WhatsAppHealthNotificationTemplateCreate> T setPriority(Integer priority) { this.priority = priority; return (T) this; }
    public List<WhatsAppHealthNotificationTemplateParameterCreate> getParameters() { return parameters; }
    public <T extends WhatsAppHealthNotificationTemplateCreate> T setParameters(List<WhatsAppHealthNotificationTemplateParameterCreate> parameters) { this.parameters = parameters; return (T) this; }
}
