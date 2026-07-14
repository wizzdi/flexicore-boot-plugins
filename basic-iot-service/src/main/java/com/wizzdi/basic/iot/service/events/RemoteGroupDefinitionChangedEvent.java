package com.wizzdi.basic.iot.service.events;

import java.time.OffsetDateTime;

public record RemoteGroupDefinitionChangedEvent(
        String remoteGroupId,
        OffsetDateTime occurredAt) {
}
