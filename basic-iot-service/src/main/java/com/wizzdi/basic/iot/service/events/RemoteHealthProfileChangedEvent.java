package com.wizzdi.basic.iot.service.events;

import java.time.OffsetDateTime;

public record RemoteHealthProfileChangedEvent(
        String remoteHealthProfileId,
        int evaluationVersion,
        OffsetDateTime occurredAt) {
}
