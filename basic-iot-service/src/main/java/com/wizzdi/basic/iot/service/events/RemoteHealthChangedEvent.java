package com.wizzdi.basic.iot.service.events;

import com.wizzdi.basic.iot.model.Remote;

import java.time.OffsetDateTime;

public record RemoteHealthChangedEvent(
        Remote remote,
        String previousSeverityName,
        Integer previousSeverityValue,
        String severityName,
        Integer severityValue,
        String ruleId,
        boolean humanInterventionRequired,
        String summary,
        String mitigationInstructions,
        OffsetDateTime occurredAt) {
}
