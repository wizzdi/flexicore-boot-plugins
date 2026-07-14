package com.wizzdi.basic.iot.service.notification;

public record HealthNotificationSendResult(
        String providerMessageId,
        Integer responseCode,
        String providerResponse) {
}
