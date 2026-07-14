package com.wizzdi.basic.iot.service.events;

import java.time.OffsetDateTime;

public record StatePropertyDefinitionChangedEvent(String statePropertyDefinitionId, OffsetDateTime occurredAt) {
}
