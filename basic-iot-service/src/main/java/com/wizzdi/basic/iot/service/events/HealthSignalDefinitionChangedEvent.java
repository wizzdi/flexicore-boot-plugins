package com.wizzdi.basic.iot.service.events;

import java.time.OffsetDateTime;

public record HealthSignalDefinitionChangedEvent(String healthSignalDefinitionId, OffsetDateTime occurredAt) {
}
