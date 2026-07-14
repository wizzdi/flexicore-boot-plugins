package com.wizzdi.basic.iot.service.notification;

import com.wizzdi.basic.iot.model.HealthNotificationChannel;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;

public interface HealthNotificationChannelAdapter extends Plugin {
    HealthNotificationChannel channel();

    default int priority() { return 0; }

    default boolean supports(HealthNotificationMessage message) { return true; }

    HealthNotificationSendResult send(HealthNotificationMessage message) throws Exception;
}
