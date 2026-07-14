package com.wizzdi.basic.iot.service.events;

import java.time.OffsetDateTime;

/**
 * Published when a Device property that determines materialized group membership changes.
 */
public record DeviceGroupMembershipChangedEvent(String deviceId, OffsetDateTime occurredAt) {
}
