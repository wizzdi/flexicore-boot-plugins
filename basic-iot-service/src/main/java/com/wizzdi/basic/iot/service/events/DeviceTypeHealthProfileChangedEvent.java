package com.wizzdi.basic.iot.service.events;

import java.time.OffsetDateTime;

public record DeviceTypeHealthProfileChangedEvent(
        String deviceTypeId,
        String previousRemoteHealthProfileId,
        String currentRemoteHealthProfileId,
        OffsetDateTime occurredAt) {
}
