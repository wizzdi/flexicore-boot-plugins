package com.wizzdi.basic.iot.service.notification;

import java.util.List;

public record WhatsAppCloudHealthNotificationRuntimeConfiguration(
        String configurationId,
        String apiBaseUrl,
        String graphApiVersion,
        String phoneNumberId,
        String whatsAppBusinessAccountId,
        String accessToken,
        String templateName,
        String languageCode,
        List<WhatsAppHealthNotificationRuntimeParameter> parameters) {
}
