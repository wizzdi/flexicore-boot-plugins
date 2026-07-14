package com.wizzdi.basic.iot.service.events;

import java.time.OffsetDateTime;

/**
 * Published when a DeviceType is created or updated and its system-managed group may need synchronization.
 */
public record DeviceTypeGroupDefinitionChangedEvent(String deviceTypeId, OffsetDateTime occurredAt) {
}
