package com.wizzdi.basic.iot.service.request;

import java.util.Set;

public class MarkHealthNotificationsRequest {
    private Set<String> healthNotificationDeliveryIds;
    private boolean read = true;
    private boolean dismissed;

    public Set<String> getHealthNotificationDeliveryIds() { return healthNotificationDeliveryIds; }
    public <T extends MarkHealthNotificationsRequest> T setHealthNotificationDeliveryIds(Set<String> healthNotificationDeliveryIds) { this.healthNotificationDeliveryIds = healthNotificationDeliveryIds; return (T) this; }
    public boolean isRead() { return read; }
    public <T extends MarkHealthNotificationsRequest> T setRead(boolean read) { this.read = read; return (T) this; }
    public boolean isDismissed() { return dismissed; }
    public <T extends MarkHealthNotificationsRequest> T setDismissed(boolean dismissed) { this.dismissed = dismissed; return (T) this; }
}
