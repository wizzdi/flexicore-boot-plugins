package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthNotificationTemplateValueSource;
import com.wizzdi.basic.iot.model.WhatsAppTemplateButtonSubType;
import com.wizzdi.basic.iot.model.WhatsAppTemplateComponentType;
import com.wizzdi.flexicore.security.request.BaseclassCreate;

public class WhatsAppHealthNotificationTemplateParameterCreate extends BaseclassCreate {
    private String id;
    private WhatsAppTemplateComponentType componentType;
    private WhatsAppTemplateButtonSubType buttonSubType;
    private Integer buttonIndex;
    private Integer parameterOrder;
    private HealthNotificationTemplateValueSource valueSource;
    private String literalValue;
    private String dateTimePattern;
    private String timeZone;
    private String fallbackValue;

    public String getId() { return id; }
    public <T extends WhatsAppHealthNotificationTemplateParameterCreate> T setId(String id) { this.id = id; return (T) this; }
    public WhatsAppTemplateComponentType getComponentType() { return componentType; }
    public <T extends WhatsAppHealthNotificationTemplateParameterCreate> T setComponentType(WhatsAppTemplateComponentType componentType) { this.componentType = componentType; return (T) this; }
    public WhatsAppTemplateButtonSubType getButtonSubType() { return buttonSubType; }
    public <T extends WhatsAppHealthNotificationTemplateParameterCreate> T setButtonSubType(WhatsAppTemplateButtonSubType buttonSubType) { this.buttonSubType = buttonSubType; return (T) this; }
    public Integer getButtonIndex() { return buttonIndex; }
    public <T extends WhatsAppHealthNotificationTemplateParameterCreate> T setButtonIndex(Integer buttonIndex) { this.buttonIndex = buttonIndex; return (T) this; }
    public Integer getParameterOrder() { return parameterOrder; }
    public <T extends WhatsAppHealthNotificationTemplateParameterCreate> T setParameterOrder(Integer parameterOrder) { this.parameterOrder = parameterOrder; return (T) this; }
    public HealthNotificationTemplateValueSource getValueSource() { return valueSource; }
    public <T extends WhatsAppHealthNotificationTemplateParameterCreate> T setValueSource(HealthNotificationTemplateValueSource valueSource) { this.valueSource = valueSource; return (T) this; }
    public String getLiteralValue() { return literalValue; }
    public <T extends WhatsAppHealthNotificationTemplateParameterCreate> T setLiteralValue(String literalValue) { this.literalValue = literalValue; return (T) this; }
    public String getDateTimePattern() { return dateTimePattern; }
    public <T extends WhatsAppHealthNotificationTemplateParameterCreate> T setDateTimePattern(String dateTimePattern) { this.dateTimePattern = dateTimePattern; return (T) this; }
    public String getTimeZone() { return timeZone; }
    public <T extends WhatsAppHealthNotificationTemplateParameterCreate> T setTimeZone(String timeZone) { this.timeZone = timeZone; return (T) this; }
    public String getFallbackValue() { return fallbackValue; }
    public <T extends WhatsAppHealthNotificationTemplateParameterCreate> T setFallbackValue(String fallbackValue) { this.fallbackValue = fallbackValue; return (T) this; }
}
