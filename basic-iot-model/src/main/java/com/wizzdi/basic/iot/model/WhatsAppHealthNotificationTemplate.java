package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "whatsapp_health_template_config_idx", columnList = "whatsAppConfiguration_id,enabled,priority,softdelete"),
        @Index(name = "whatsapp_health_template_match_idx", columnList = "eventType,deliveryMode,locale")
})
public class WhatsAppHealthNotificationTemplate extends Baseclass {
    @ManyToOne(targetEntity = WhatsAppCloudHealthNotificationConfiguration.class)
    @JsonIgnore
    private WhatsAppCloudHealthNotificationConfiguration whatsAppConfiguration;
    @Enumerated(EnumType.STRING)
    private HealthNotificationEventType eventType;
    @Enumerated(EnumType.STRING)
    private HealthNotificationDeliveryMode deliveryMode;
    private String locale = "en";
    private String templateName;
    private String languageCode = "en_US";
    private boolean enabled = true;
    private int priority;
    @Transient
    private List<WhatsAppHealthNotificationTemplateParameter> parameters = List.of();

    public WhatsAppCloudHealthNotificationConfiguration getWhatsAppConfiguration() { return whatsAppConfiguration; }
    public <T extends WhatsAppHealthNotificationTemplate> T setWhatsAppConfiguration(WhatsAppCloudHealthNotificationConfiguration whatsAppConfiguration) { this.whatsAppConfiguration = whatsAppConfiguration; return (T) this; }
    public HealthNotificationEventType getEventType() { return eventType; }
    public <T extends WhatsAppHealthNotificationTemplate> T setEventType(HealthNotificationEventType eventType) { this.eventType = eventType; return (T) this; }
    public HealthNotificationDeliveryMode getDeliveryMode() { return deliveryMode; }
    public <T extends WhatsAppHealthNotificationTemplate> T setDeliveryMode(HealthNotificationDeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; return (T) this; }
    public String getLocale() { return locale; }
    public <T extends WhatsAppHealthNotificationTemplate> T setLocale(String locale) { this.locale = locale; return (T) this; }
    public String getTemplateName() { return templateName; }
    public <T extends WhatsAppHealthNotificationTemplate> T setTemplateName(String templateName) { this.templateName = templateName; return (T) this; }
    public String getLanguageCode() { return languageCode; }
    public <T extends WhatsAppHealthNotificationTemplate> T setLanguageCode(String languageCode) { this.languageCode = languageCode; return (T) this; }
    public boolean isEnabled() { return enabled; }
    public <T extends WhatsAppHealthNotificationTemplate> T setEnabled(boolean enabled) { this.enabled = enabled; return (T) this; }
    public int getPriority() { return priority; }
    public <T extends WhatsAppHealthNotificationTemplate> T setPriority(int priority) { this.priority = priority; return (T) this; }
    public List<WhatsAppHealthNotificationTemplateParameter> getParameters() { return parameters; }
    public <T extends WhatsAppHealthNotificationTemplate> T setParameters(List<WhatsAppHealthNotificationTemplateParameter> parameters) { this.parameters = parameters == null ? List.of() : parameters; return (T) this; }
}
