package com.wizzdi.basic.iot.service.notification;

import com.wizzdi.basic.iot.model.HealthNotificationChannel;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;

import java.util.List;

public record HealthNotificationMessage(
        String tenantId,
        String userId,
        String destination,
        HealthNotificationChannel channel,
        HealthNotificationDeliveryMode deliveryMode,
        String locale,
        String subject,
        String body,
        List<HealthNotificationItem> items) {
}
