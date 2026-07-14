package com.wizzdi.basic.iot.model;

public enum HealthNotificationDeliveryStatus {
    PENDING,
    PROCESSING,
    DELIVERED,
    WAITING_FOR_ADAPTER,
    FAILED,
    CANCELLED
}
