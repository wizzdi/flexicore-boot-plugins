package com.wizzdi.basic.iot.service.response;

import java.util.Map;

public record FleetHealthSnapshot(
        String deviceTypeId,
        long totalDevices,
        long devicesRequiringHumanIntervention,
        Map<String, Long> severityCounts,
        String fleetSeverityName,
        Integer fleetSeverityValue,
        String matchedAggregationRuleId) {
}
