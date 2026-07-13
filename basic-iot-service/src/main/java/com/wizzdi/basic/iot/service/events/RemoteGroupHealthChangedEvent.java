package com.wizzdi.basic.iot.service.events;

import com.wizzdi.basic.iot.model.RemoteGroup;

import java.time.OffsetDateTime;

public record RemoteGroupHealthChangedEvent(
        RemoteGroup remoteGroup,
        String previousSeverityName,
        Integer previousSeverityValue,
        String severityName,
        Integer severityValue,
        String matchedRuleId,
        int populationCount,
        int unknownSeverityCount,
        int offlineCount,
        int humanInterventionCount,
        OffsetDateTime occurredAt) {
}
