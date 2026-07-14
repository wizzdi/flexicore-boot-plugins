package com.wizzdi.basic.iot.service.notification;

public record SendGridHealthNotificationRuntimeConfiguration(
        String configurationId,
        String apiBaseUrl,
        String apiKey,
        String fromEmail,
        String fromName,
        String replyToEmail,
        String replyToName,
        String onBehalfOf,
        String templateId) {
}
