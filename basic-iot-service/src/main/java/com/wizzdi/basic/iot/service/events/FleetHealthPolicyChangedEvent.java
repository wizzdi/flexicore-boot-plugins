package com.wizzdi.basic.iot.service.events;

import java.time.OffsetDateTime;

public record FleetHealthPolicyChangedEvent(
        String fleetHealthPolicyId,
        int evaluationVersion,
        OffsetDateTime occurredAt) {
}
