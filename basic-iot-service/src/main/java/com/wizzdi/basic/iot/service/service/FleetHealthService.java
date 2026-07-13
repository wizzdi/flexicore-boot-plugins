package com.wizzdi.basic.iot.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.service.response.FleetHealthSnapshot;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Extension
public class FleetHealthService implements Plugin {
    @PersistenceContext private EntityManager em;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private DeviceTypeService deviceTypeService;

    public FleetHealthSnapshot getSnapshot(String deviceTypeId, SecurityContext securityContext) {
        DeviceType type = deviceTypeService.getByIdOrNull(deviceTypeId, DeviceType.class, securityContext);
        if (type == null) throw new IllegalArgumentException("No accessible DeviceType with id " + deviceTypeId);
        List<Device> devices = em.createQuery("select d from Device d where d.deviceType.id=:id", Device.class)
                .setParameter("id", deviceTypeId).getResultList();
        Map<String, Long> counts = new TreeMap<>();
        long human = 0;
        for (Device device : devices) {
            counts.merge(Optional.ofNullable(device.getCurrentSeverityName()).orElse("NONE"), 1L, Long::sum);
            if (device.isHumanInterventionRequired()) human++;
        }
        AggregationResult aggregation = evaluateAggregation(type.getFleetHealthDefinitions(), devices, counts);
        return new FleetHealthSnapshot(type.getId(), devices.size(), human, counts,
                aggregation.name, aggregation.value, aggregation.ruleId);
    }

    private AggregationResult evaluateAggregation(String json, List<Device> devices, Map<String, Long> counts) {
        if (json == null || json.isBlank()) return new AggregationResult(null, null, null);
        try {
            JsonNode rules = objectMapper.readTree(json);
            if (!rules.isArray()) return new AggregationResult(null, null, null);
            List<JsonNode> matched = new ArrayList<>();
            for (JsonNode rule : rules) {
                String severity = rule.path("deviceSeverity").asText();
                long count = counts.getOrDefault(severity, 0L);
                double percent = devices.isEmpty() ? 0 : (count * 100.0 / devices.size());
                boolean ok = (!rule.has("minimumCount") || count >= rule.path("minimumCount").asLong())
                        && (!rule.has("minimumPercent") || percent >= rule.path("minimumPercent").asDouble());
                if (ok) matched.add(rule);
            }
            matched.sort(Comparator.comparingInt((JsonNode n) -> n.path("priority").asInt()).reversed());
            if (matched.isEmpty()) return new AggregationResult(null, null, null);
            JsonNode winner = matched.get(0);
            return new AggregationResult(winner.path("name").asText(null), winner.path("value").asInt(), winner.path("id").asText(null));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid fleetHealthDefinitions", e);
        }
    }

    private record AggregationResult(String name, Integer value, String ruleId) {}
}
