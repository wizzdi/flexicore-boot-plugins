package com.wizzdi.basic.iot.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.events.RemoteUpdatedEvent;
import com.wizzdi.basic.iot.service.events.SeverityChangedEvent;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

@Component
@Extension
public class DeviceSeverityEvaluationService implements Plugin {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener
    @Transactional
    public void onRemoteUpdated(RemoteUpdatedEvent event) {
        if (!event.isStateUpdated() || !(event.getBaseclass() instanceof Device device)) {
            return;
        }
        evaluate(device, OffsetDateTime.now());
    }

    public void evaluate(Device device, OffsetDateTime now) {
        DeviceType type = device.getDeviceType();
        if (type == null || type.getSeverityDefinitions() == null || type.getSeverityDefinitions().isBlank()) {
            return;
        }
        try {
            JsonNode definitions = objectMapper.readTree(type.getSeverityDefinitions());
            if (!definitions.isArray()) {
                return;
            }
            JsonNode state = objectMapper.valueToTree(device.getDeviceProperties());
            List<MatchedRule> active = new ArrayList<>();
            for (JsonNode rule : definitions) {
                String ruleId = rule.path("id").asText(rule.path("name").asText());
                if (ruleId.isBlank()) {
                    continue;
                }
                DeviceSeverityRuntime runtime = getOrCreateRuntime(device, ruleId);
                boolean rawMatch = ruleMatches(state, rule, runtime.isActive());
                updateRuntime(runtime, rule, rawMatch, now);
                em.merge(runtime);
                if (runtime.isActive()) {
                    active.add(new MatchedRule(rule, runtime));
                }
            }
            active.sort(Comparator
                    .comparingInt((MatchedRule m) -> m.rule.path("priority").asInt())
                    .thenComparingInt(m -> m.rule.path("value").asInt())
                    .reversed());
            applySelectedRule(device, type, active.isEmpty() ? null : active.get(0), now);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid severityDefinitions for DeviceType " + type.getId(), e);
        }
    }

    private DeviceSeverityRuntime getOrCreateRuntime(Device device, String ruleId) {
        List<DeviceSeverityRuntime> result = em.createQuery(
                        "select r from DeviceSeverityRuntime r where r.remote.id=:remoteId and r.ruleId=:ruleId",
                        DeviceSeverityRuntime.class)
                .setParameter("remoteId", device.getId())
                .setParameter("ruleId", ruleId)
                .setMaxResults(1)
                .getResultList();
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return new DeviceSeverityRuntime()
                .setId(UUID.randomUUID().toString())
                .setRemote(device)
                .setRuleId(ruleId);
    }

    private void updateRuntime(DeviceSeverityRuntime runtime, JsonNode rule, boolean matched, OffsetDateTime now) {
        long enterSeconds = durationSeconds(rule, "forSeconds", "enterForSeconds");
        long exitSeconds = durationSeconds(rule, "clearForSeconds", "exitForSeconds");
        runtime.setLastEvaluatedAt(now);
        if (!runtime.isActive()) {
            runtime.setClearCandidateSince(null);
            if (!matched) {
                runtime.setCandidateSince(null);
                return;
            }
            if (runtime.getCandidateSince() == null) {
                runtime.setCandidateSince(now);
            }
            if (Duration.between(runtime.getCandidateSince(), now).getSeconds() >= enterSeconds) {
                runtime.setActive(true).setActiveSince(now).setCandidateSince(null);
            }
            return;
        }
        runtime.setCandidateSince(null);
        if (matched) {
            runtime.setClearCandidateSince(null);
            return;
        }
        if (runtime.getClearCandidateSince() == null) {
            runtime.setClearCandidateSince(now);
        }
        if (Duration.between(runtime.getClearCandidateSince(), now).getSeconds() >= exitSeconds) {
            runtime.setActive(false).setActiveSince(null).setClearCandidateSince(null);
        }
    }

    private long durationSeconds(JsonNode rule, String directField, String nestedField) {
        if (rule.has(directField)) {
            return Math.max(0, rule.path(directField).asLong());
        }
        return Math.max(0, rule.path("timeCondition").path(nestedField).asLong());
    }

    private boolean ruleMatches(JsonNode state, JsonNode rule, boolean active) {
        JsonNode conditions = rule.path("conditions");
        if (!conditions.isArray() || conditions.isEmpty()) {
            return false;
        }
        boolean any = "ANY".equalsIgnoreCase(rule.path("useWhen").asText("ALL"));
        for (JsonNode condition : conditions) {
            boolean matched = conditionMatches(state, condition, active);
            if (any && matched) return true;
            if (!any && !matched) return false;
        }
        return !any;
    }

    private boolean conditionMatches(JsonNode state, JsonNode condition, boolean active) {
        JsonNode value = resolvePath(state, condition.path("propertyPath").asText());
        String operator = condition.path("operator").asText("equals").toLowerCase(Locale.ROOT);
        if ("missing".equals(operator)) return value == null || value.isMissingNode();
        if ("exists".equals(operator)) return value != null && !value.isMissingNode() && !value.isNull();
        if (value == null || value.isMissingNode() || value.isNull()) return false;

        double margin = active ? condition.path("hysteresis").asDouble(0) : 0;
        return switch (operator) {
            case "gt", ">" -> value.isNumber() && value.asDouble() > condition.path("value").asDouble() - margin;
            case "gte", ">=" -> value.isNumber() && value.asDouble() >= condition.path("value").asDouble() - margin;
            case "lt", "<" -> value.isNumber() && value.asDouble() < condition.path("value").asDouble() + margin;
            case "lte", "<=" -> value.isNumber() && value.asDouble() <= condition.path("value").asDouble() + margin;
            case "range", "between" -> value.isNumber()
                    && (!condition.has("minimum") || value.asDouble() >= condition.path("minimum").asDouble() - margin)
                    && (!condition.has("maximum") || value.asDouble() <= condition.path("maximum").asDouble() + margin);
            case "enum", "in" -> arrayContains(condition.path("enumValues"), value.asText());
            case "like" -> like(value.asText(), condition.path("pattern").asText());
            case "notequals", "!=" -> !Objects.equals(value.asText(), condition.path("value").asText());
            default -> Objects.equals(value.asText(), condition.path("value").asText());
        };
    }

    private boolean arrayContains(JsonNode values, String value) {
        if (!values.isArray()) return false;
        for (JsonNode item : values) if (Objects.equals(item.asText(), value)) return true;
        return false;
    }

    private boolean like(String value, String pattern) {
        String regex = java.util.regex.Pattern.quote(pattern).replace("%", "\\E.*\\Q");
        return value.matches("(?s)" + regex);
    }

    private JsonNode resolvePath(JsonNode root, String path) {
        JsonNode current = root;
        for (String part : path.split("\\.")) {
            if (!part.isBlank()) current = current.path(part);
        }
        return current;
    }

    private void applySelectedRule(Device device, DeviceType type, MatchedRule selected, OffsetDateTime now) throws Exception {
        String previousName = device.getCurrentSeverityName();
        Integer previousValue = device.getCurrentSeverityValue();
        JsonNode rule = selected == null ? null : selected.rule;
        String name = rule == null ? null : rule.path("name").asText(null);
        Integer value = rule == null ? null : rule.path("value").asInt();
        String ruleId = rule == null ? null : rule.path("id").asText(name);
        boolean changed = !Objects.equals(previousName, name) || !Objects.equals(previousValue, value)
                || !Objects.equals(device.getCurrentSeverityRuleId(), ruleId);

        boolean humanRequired = rule != null && rule.path("humanInterventionRequired").asBoolean(false);
        String instructions = rule == null ? null : rule.path("mitigationInstructions").asText(null);
        device.setCurrentSeverityName(name)
                .setCurrentSeverityValue(value)
                .setCurrentSeverityRuleId(ruleId)
                .setHumanInterventionRequired(humanRequired)
                .setMitigationInstructions(instructions);
        if (changed) {
            device.setSeveritySince(now)
                    .setMitigationStatus(humanRequired ? "REQUIRED" : "NOT_REQUIRED");
        }
        em.merge(device);

        String policy = type.getHistoryRecordingPolicy() == null ? "SEVERITY_CHANGES" : type.getHistoryRecordingPolicy();
        String ruleHistory = rule == null ? "NEVER" : rule.path("recordHistory").asText("ON_CHANGE");
        boolean record = "ALL_STATE_CHANGES".equalsIgnoreCase(policy)
                || (changed && "SEVERITY_CHANGES".equalsIgnoreCase(policy))
                || (rule != null && "MATCHED_RULES".equalsIgnoreCase(policy) && !"NEVER".equalsIgnoreCase(ruleHistory))
                || (changed && "ON_CHANGE".equalsIgnoreCase(ruleHistory))
                || "ALWAYS".equalsIgnoreCase(ruleHistory);
        if (record) {
            em.persist(new SeverityHistory()
                    .setId(UUID.randomUUID().toString())
                    .setRemote(device)
                    .setDeviceType(type)
                    .setRuleId(ruleId)
                    .setSeverityName(name)
                    .setSeverityValue(value)
                    .setPreviousSeverityValue(previousValue)
                    .setHumanInterventionRequired(humanRequired)
                    .setMitigationStatus(device.getMitigationStatus())
                    .setMatchedConditions(rule == null ? null : objectMapper.writeValueAsString(rule.path("conditions")))
                    .setRecordedAt(now));
        }
        if (changed) {
            eventPublisher.publishEvent(new SeverityChangedEvent(
                    device, previousName, previousValue, name, value, ruleId, humanRequired, instructions,
                    rule == null ? null : rule.path("escalationKey").asText(null), now));
        }
    }

    private record MatchedRule(JsonNode rule, DeviceSeverityRuntime runtime) {}
}
