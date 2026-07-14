package com.wizzdi.basic.iot.service.notification;

import com.wizzdi.basic.iot.model.HealthNotificationEventType;

import java.time.OffsetDateTime;

public record HealthNotificationItem(
        String deliveryId,
        String eventId,
        HealthNotificationEventType eventType,
        String incidentId,
        String remoteId,
        String remoteGroupId,
        String title,
        String message,
        String severityName,
        Integer severityValue,
        OffsetDateTime occurredAt) {
}
