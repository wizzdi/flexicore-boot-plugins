package com.wizzdi.basic.iot.service.notification;

import com.wizzdi.basic.iot.model.WhatsAppTemplateButtonSubType;
import com.wizzdi.basic.iot.model.WhatsAppTemplateComponentType;

public record WhatsAppHealthNotificationRuntimeParameter(
        WhatsAppTemplateComponentType componentType,
        WhatsAppTemplateButtonSubType buttonSubType,
        int buttonIndex,
        int parameterOrder,
        String value) {
}
