package com.wizzdi.basic.iot.service.events;

import com.wizzdi.basic.iot.model.Device;
import java.time.OffsetDateTime;

public record SeverityChangedEvent(
        Device device,
        String previousSeverityName,
        Integer previousSeverityValue,
        String severityName,
        Integer severityValue,
        String ruleId,
        boolean humanInterventionRequired,
        String mitigationInstructions,
        String escalationKey,
        OffsetDateTime occurredAt) {
}
