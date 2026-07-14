package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "whatsapp_health_parameter_template_idx", columnList = "whatsAppTemplate_id,componentType,buttonIndex,parameterOrder,softdelete")
})
public class WhatsAppHealthNotificationTemplateParameter extends Baseclass {
    @ManyToOne(targetEntity = WhatsAppHealthNotificationTemplate.class)
    @JsonIgnore
    private WhatsAppHealthNotificationTemplate whatsAppTemplate;
    @Enumerated(EnumType.STRING)
    private WhatsAppTemplateComponentType componentType = WhatsAppTemplateComponentType.BODY;
    @Enumerated(EnumType.STRING)
    private WhatsAppTemplateButtonSubType buttonSubType;
    private int buttonIndex;
    private int parameterOrder;
    @Enumerated(EnumType.STRING)
    private HealthNotificationTemplateValueSource valueSource = HealthNotificationTemplateValueSource.LITERAL;
    @Column(length = 4000)
    private String literalValue;
    private String dateTimePattern;
    @Column(name = "parameterTimeZone")
    private String timeZone = "UTC";
    @Column(length = 1000)
    private String fallbackValue;

    public WhatsAppHealthNotificationTemplate getWhatsAppTemplate() { return whatsAppTemplate; }
    public <T extends WhatsAppHealthNotificationTemplateParameter> T setWhatsAppTemplate(WhatsAppHealthNotificationTemplate whatsAppTemplate) { this.whatsAppTemplate = whatsAppTemplate; return (T) this; }
    public WhatsAppTemplateComponentType getComponentType() { return componentType; }
    public <T extends WhatsAppHealthNotificationTemplateParameter> T setComponentType(WhatsAppTemplateComponentType componentType) { this.componentType = componentType; return (T) this; }
    public WhatsAppTemplateButtonSubType getButtonSubType() { return buttonSubType; }
    public <T extends WhatsAppHealthNotificationTemplateParameter> T setButtonSubType(WhatsAppTemplateButtonSubType buttonSubType) { this.buttonSubType = buttonSubType; return (T) this; }
    public int getButtonIndex() { return buttonIndex; }
    public <T extends WhatsAppHealthNotificationTemplateParameter> T setButtonIndex(int buttonIndex) { this.buttonIndex = buttonIndex; return (T) this; }
    public int getParameterOrder() { return parameterOrder; }
    public <T extends WhatsAppHealthNotificationTemplateParameter> T setParameterOrder(int parameterOrder) { this.parameterOrder = parameterOrder; return (T) this; }
    public HealthNotificationTemplateValueSource getValueSource() { return valueSource; }
    public <T extends WhatsAppHealthNotificationTemplateParameter> T setValueSource(HealthNotificationTemplateValueSource valueSource) { this.valueSource = valueSource; return (T) this; }
    public String getLiteralValue() { return literalValue; }
    public <T extends WhatsAppHealthNotificationTemplateParameter> T setLiteralValue(String literalValue) { this.literalValue = literalValue; return (T) this; }
    public String getDateTimePattern() { return dateTimePattern; }
    public <T extends WhatsAppHealthNotificationTemplateParameter> T setDateTimePattern(String dateTimePattern) { this.dateTimePattern = dateTimePattern; return (T) this; }
    public String getTimeZone() { return timeZone; }
    public <T extends WhatsAppHealthNotificationTemplateParameter> T setTimeZone(String timeZone) { this.timeZone = timeZone; return (T) this; }
    public String getFallbackValue() { return fallbackValue; }
    public <T extends WhatsAppHealthNotificationTemplateParameter> T setFallbackValue(String fallbackValue) { this.fallbackValue = fallbackValue; return (T) this; }
}
