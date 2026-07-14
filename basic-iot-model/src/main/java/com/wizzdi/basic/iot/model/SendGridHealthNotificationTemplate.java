package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "sendgrid_health_template_config_idx", columnList = "sendGridConfiguration_id,enabled,priority,softdelete"),
        @Index(name = "sendgrid_health_template_match_idx", columnList = "eventType,deliveryMode,locale")
})
public class SendGridHealthNotificationTemplate extends Baseclass {
    @ManyToOne(targetEntity = SendGridHealthNotificationConfiguration.class)
    @JsonIgnore
    private SendGridHealthNotificationConfiguration sendGridConfiguration;
    @Enumerated(EnumType.STRING)
    private HealthNotificationEventType eventType;
    @Enumerated(EnumType.STRING)
    private HealthNotificationDeliveryMode deliveryMode;
    private String locale = "en";
    private String templateId;
    private boolean enabled = true;
    private int priority;

    public SendGridHealthNotificationConfiguration getSendGridConfiguration() { return sendGridConfiguration; }
    public <T extends SendGridHealthNotificationTemplate> T setSendGridConfiguration(SendGridHealthNotificationConfiguration sendGridConfiguration) { this.sendGridConfiguration = sendGridConfiguration; return (T) this; }
    public HealthNotificationEventType getEventType() { return eventType; }
    public <T extends SendGridHealthNotificationTemplate> T setEventType(HealthNotificationEventType eventType) { this.eventType = eventType; return (T) this; }
    public HealthNotificationDeliveryMode getDeliveryMode() { return deliveryMode; }
    public <T extends SendGridHealthNotificationTemplate> T setDeliveryMode(HealthNotificationDeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; return (T) this; }
    public String getLocale() { return locale; }
    public <T extends SendGridHealthNotificationTemplate> T setLocale(String locale) { this.locale = locale; return (T) this; }
    public String getTemplateId() { return templateId; }
    public <T extends SendGridHealthNotificationTemplate> T setTemplateId(String templateId) { this.templateId = templateId; return (T) this; }
    public boolean isEnabled() { return enabled; }
    public <T extends SendGridHealthNotificationTemplate> T setEnabled(boolean enabled) { this.enabled = enabled; return (T) this; }
    public int getPriority() { return priority; }
    public <T extends SendGridHealthNotificationTemplate> T setPriority(int priority) { this.priority = priority; return (T) this; }
}
