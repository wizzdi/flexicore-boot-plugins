package com.wizzdi.basic.iot.service.response;

import java.time.OffsetDateTime;
import java.util.Map;

public record RemoteGroupHealthSnapshot(
        String remoteGroupId,
        int populationCount,
        int unknownSeverityCount,
        int offlineCount,
        int humanInterventionCount,
        String severityName,
        Integer severityValue,
        String matchedRuleId,
        boolean humanInterventionRequired,
        Map<String, Double> metrics,
        OffsetDateTime calculatedAt) {
}
