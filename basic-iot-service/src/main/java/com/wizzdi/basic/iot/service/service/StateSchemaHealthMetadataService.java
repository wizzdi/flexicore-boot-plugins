package com.wizzdi.basic.iot.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.request.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Normalizes supported x-flexicore JSON-Schema annotations into the typed
 * Basic IoT health model. Runtime health evaluation therefore reads typed
 * entities and never reparses schema metadata.
 */
@Component
@Extension
public class StateSchemaHealthMetadataService implements Plugin {

    public static final String PROPERTY_ID = "x-flexicore-property-id";
    public static final String HEALTH_SIGNAL = "x-flexicore-health-signal";
    public static final String UNIT = "x-flexicore-unit";
    public static final String AGGREGATABLE = "x-flexicore-aggregatable";
    public static final String KEEP_STATE_HISTORY = "x-flexicore-keep-state-history";
    public static final String HEALTH_PROFILE = "x-flexicore-health-profile";

    private static final Logger logger = LoggerFactory.getLogger(StateSchemaHealthMetadataService.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StatePropertyDefinitionService statePropertyDefinitionService;
    @Autowired
    private HealthSignalDefinitionService healthSignalDefinitionService;
    @Autowired
    private RemoteHealthProfileService remoteHealthProfileService;
    @Autowired
    private DeviceTypeService deviceTypeService;

    private final ConcurrentMap<String, Object> schemaLocks = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> synchronizedSchemas = new ConcurrentHashMap<>();

    public void synchronize(StateSchema stateSchema,
                            DeviceType deviceType,
                            String jsonSchema,
                            SecurityContext securityContext) {
        if (stateSchema == null || deviceType == null || jsonSchema == null || jsonSchema.isBlank()) {
            return;
        }
        String synchronizationKey = stateSchema.getId();
        Object lock = schemaLocks.computeIfAbsent(synchronizationKey, ignored -> new Object());
        synchronized (lock) {
            if (Objects.equals(synchronizedSchemas.get(synchronizationKey), jsonSchema)) {
                return;
            }
            try {
                JsonNode root = objectMapper.readTree(jsonSchema);
                boolean keepStateHistory = root.path(KEEP_STATE_HISTORY).asBoolean(false);
                JsonNode profileNode = root.get(HEALTH_PROFILE);
                List<AnnotatedProperty> annotatedProperties = readAnnotatedProperties(root, deviceType);

                if (annotatedProperties.isEmpty() && !keepStateHistory && (profileNode == null || !profileNode.isObject())) {
                    return;
                }

                Map<String, StatePropertyDefinition> properties = upsertStateProperties(
                        stateSchema, annotatedProperties, securityContext);
                Map<String, HealthSignalDefinition> signals = upsertHealthSignals(
                        annotatedProperties, securityContext);

                RemoteHealthProfile profile = null;
                if (profileNode != null && profileNode.isObject()) {
                    profile = upsertProfile(deviceType, profileNode, annotatedProperties,
                            properties, signals, securityContext);
                }

                updateDeviceType(deviceType, keepStateHistory, profile, securityContext);
                synchronizedSchemas.put(synchronizationKey, jsonSchema);
                logger.info("normalized health metadata for stateSchema={} deviceType={} properties={} signals={} profile={} history={}",
                        stateSchema.getId(), deviceType.getId(), properties.size(), signals.size(),
                        profile == null ? null : profile.getExternalId(), keepStateHistory);
            } catch (Exception e) {
                throw new IllegalArgumentException("failed normalizing health metadata for schema " + stateSchema.getId(), e);
            }
        }
    }

    private List<AnnotatedProperty> readAnnotatedProperties(JsonNode root, DeviceType deviceType) {
        JsonNode properties = root.path("properties");
        if (!properties.isObject()) {
            return List.of();
        }
        List<AnnotatedProperty> result = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String propertyPath = entry.getKey();
            JsonNode definition = entry.getValue();
            String propertyExternalId = text(definition, PROPERTY_ID);
            String signalExternalId = text(definition, HEALTH_SIGNAL);
            if (propertyExternalId == null && signalExternalId == null) {
                continue;
            }
            if (propertyExternalId == null) {
                propertyExternalId = stableDeviceTypeId(deviceType) + "." + propertyPath;
            }
            HealthSignalValueType valueType = valueType(definition);
            String unit = firstNonBlank(text(definition, UNIT), text(definition, "unit"));
            boolean aggregatable = !definition.has(AGGREGATABLE) || definition.path(AGGREGATABLE).asBoolean(true);
            result.add(new AnnotatedProperty(propertyPath, propertyExternalId, signalExternalId,
                    valueType, unit, aggregatable));
        }
        return result;
    }

    private Map<String, StatePropertyDefinition> upsertStateProperties(StateSchema schema,
                                                                        List<AnnotatedProperty> annotated,
                                                                        SecurityContext context) {
        Set<String> externalIds = annotated.stream().map(AnnotatedProperty::propertyExternalId).collect(Collectors.toSet());
        Map<String, StatePropertyDefinition> existing = externalIds.isEmpty()
                ? new HashMap<>()
                : statePropertyDefinitionService.getAll(context, new StatePropertyDefinitionFilter()
                        .setStateSchemaIds(Set.of(schema.getId()))
                        .setExternalIds(externalIds))
                .getList().stream().collect(Collectors.toMap(StatePropertyDefinition::getExternalId,
                        Function.identity(), (a, b) -> a));

        for (AnnotatedProperty property : annotated) {
            StatePropertyDefinition entity = existing.get(property.propertyExternalId());
            if (entity == null) {
                StatePropertyDefinitionCreate create = new StatePropertyDefinitionCreate();
                create.setName(property.propertyExternalId());
                create.setStateSchemaId(schema.getId());
                create.setStateSchema(schema);
                create.setExternalId(property.propertyExternalId());
                create.setPropertyPath(property.propertyPath());
                create.setValueType(property.valueType());
                create.setUnit(property.unit());
                entity = statePropertyDefinitionService.create(create, context);
                existing.put(property.propertyExternalId(), entity);
            } else {
                StatePropertyDefinitionUpdate update = new StatePropertyDefinitionUpdate();
                update.setId(entity.getId());
                update.setStatePropertyDefinition(entity);
                update.setName(property.propertyExternalId());
                update.setStateSchema(schema);
                update.setExternalId(property.propertyExternalId());
                update.setPropertyPath(property.propertyPath());
                update.setValueType(property.valueType());
                update.setUnit(property.unit());
                statePropertyDefinitionService.update(update, context);
            }
        }
        return existing;
    }

    private Map<String, HealthSignalDefinition> upsertHealthSignals(List<AnnotatedProperty> annotated,
                                                                     SecurityContext context) {
        Map<String, AnnotatedProperty> requested = annotated.stream()
                .filter(f -> f.signalExternalId() != null)
                .collect(Collectors.toMap(AnnotatedProperty::signalExternalId, Function.identity(), (a, b) -> a));
        if (requested.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, HealthSignalDefinition> existing = healthSignalDefinitionService.getAll(context,
                        new HealthSignalDefinitionFilter().setExternalIds(requested.keySet()))
                .getList().stream().collect(Collectors.toMap(HealthSignalDefinition::getExternalId,
                        Function.identity(), (a, b) -> a));

        for (AnnotatedProperty property : requested.values()) {
            HealthSignalDefinition entity = existing.get(property.signalExternalId());
            if (entity == null) {
                HealthSignalDefinitionCreate create = new HealthSignalDefinitionCreate();
                create.setName(property.signalExternalId());
                create.setExternalId(property.signalExternalId());
                create.setValueType(property.valueType());
                create.setUnit(property.unit());
                create.setBuiltIn(false);
                create.setAggregatable(property.aggregatable());
                entity = healthSignalDefinitionService.create(create, context);
                existing.put(property.signalExternalId(), entity);
            } else {
                HealthSignalDefinitionUpdate update = new HealthSignalDefinitionUpdate();
                update.setId(entity.getId());
                update.setHealthSignalDefinition(entity);
                update.setName(property.signalExternalId());
                update.setExternalId(property.signalExternalId());
                update.setValueType(property.valueType());
                update.setUnit(property.unit());
                update.setBuiltIn(false);
                update.setAggregatable(property.aggregatable());
                healthSignalDefinitionService.update(update, context);
            }
        }
        return existing;
    }

    private RemoteHealthProfile upsertProfile(DeviceType deviceType,
                                              JsonNode profileNode,
                                              List<AnnotatedProperty> annotated,
                                              Map<String, StatePropertyDefinition> properties,
                                              Map<String, HealthSignalDefinition> signals,
                                              SecurityContext context) {
        String externalId = firstNonBlank(text(profileNode, "externalId"), stableDeviceTypeId(deviceType) + ".health");
        RemoteHealthProfile existing = remoteHealthProfileService.getAll(context,
                        new RemoteHealthProfileFilter().setExternalIds(Set.of(externalId)))
                .getList().stream().findFirst().orElse(null);
        if (existing != null) {
            remoteHealthProfileService.populate(existing);
        }

        List<HealthSignalMappingCreate> mappings = mappings(annotated, properties, signals, existing);
        List<RemoteHealthRuleCreate> rules = rules(profileNode.path("rules"), signals, existing);
        String name = firstNonBlank(text(profileNode, "name"), externalId);

        if (existing == null) {
            RemoteHealthProfileCreate create = new RemoteHealthProfileCreate();
            create.setName(name);
            create.setExternalId(externalId);
            create.setEnabled(booleanValue(profileNode, "enabled", true));
            create.setDefaultSeverityName(firstNonBlank(text(profileNode, "defaultSeverityName"), "NORMAL"));
            create.setDefaultSeverityValue(intValue(profileNode, "defaultSeverityValue", 0));
            create.setActionRequiredFromSeverityValue(intValue(profileNode, "actionRequiredFromSeverityValue", 60));
            create.setMappings(mappings);
            create.setRules(rules);
            return remoteHealthProfileService.create(create, context);
        }

        RemoteHealthProfileUpdate update = new RemoteHealthProfileUpdate();
        update.setId(existing.getId());
        update.setRemoteHealthProfile(existing);
        update.setName(name);
        update.setExternalId(externalId);
        update.setEnabled(booleanValue(profileNode, "enabled", true));
        update.setDefaultSeverityName(firstNonBlank(text(profileNode, "defaultSeverityName"), "NORMAL"));
        update.setDefaultSeverityValue(intValue(profileNode, "defaultSeverityValue", 0));
        update.setActionRequiredFromSeverityValue(intValue(profileNode, "actionRequiredFromSeverityValue", 60));
        update.setMappings(mappings);
        update.setRules(rules);
        return remoteHealthProfileService.update(update, context);
    }

    private List<HealthSignalMappingCreate> mappings(List<AnnotatedProperty> annotated,
                                                     Map<String, StatePropertyDefinition> properties,
                                                     Map<String, HealthSignalDefinition> signals,
                                                     RemoteHealthProfile existingProfile) {
        Map<String, HealthSignalMapping> existing = existingProfile == null ? Map.of()
                : existingProfile.getMappings().stream().filter(f -> !f.isSoftDelete())
                .collect(Collectors.toMap(this::mappingKey, Function.identity(), (a, b) -> a));
        List<HealthSignalMappingCreate> result = new ArrayList<>();
        int priority = 0;
        for (AnnotatedProperty property : annotated) {
            if (property.signalExternalId() == null) {
                continue;
            }
            StatePropertyDefinition stateProperty = properties.get(property.propertyExternalId());
            HealthSignalDefinition signal = signals.get(property.signalExternalId());
            if (stateProperty == null || signal == null) {
                continue;
            }
            String key = signal.getId() + "|" + stateProperty.getId();
            HealthSignalMapping prior = existing.get(key);
            HealthSignalMappingCreate create = new HealthSignalMappingCreate();
            if (prior != null) {
                create.setId(prior.getId());
            }
            create.setName(property.signalExternalId() + " <- " + property.propertyPath());
            create.setHealthSignalId(signal.getId());
            create.setSourceType(HealthSignalSourceType.STATE_PROPERTY);
            create.setStatePropertyId(stateProperty.getId());
            create.setOperation(HealthSignalMappingOperation.DIRECT);
            create.setPriority(priority++);
            result.add(create);
        }
        return result;
    }

    private List<RemoteHealthRuleCreate> rules(JsonNode rulesNode,
                                               Map<String, HealthSignalDefinition> signals,
                                               RemoteHealthProfile existingProfile) {
        if (!rulesNode.isArray()) {
            return List.of();
        }
        Map<String, RemoteHealthRule> existingRules = existingProfile == null ? Map.of()
                : existingProfile.getRules().stream().filter(f -> !f.isSoftDelete() && f.getName() != null)
                .collect(Collectors.toMap(RemoteHealthRule::getName, Function.identity(), (a, b) -> a));
        List<RemoteHealthRuleCreate> result = new ArrayList<>();
        int sequence = 0;
        for (JsonNode ruleNode : rulesNode) {
            String ruleKey = firstNonBlank(text(ruleNode, "id"), "rule-" + sequence);
            RemoteHealthRule existingRule = existingRules.get(ruleKey);
            RemoteHealthRuleCreate rule = new RemoteHealthRuleCreate();
            if (existingRule != null) {
                rule.setId(existingRule.getId());
            }
            rule.setName(ruleKey);
            rule.setPriority(intValue(ruleNode, "priority", sequence));
            rule.setEnabled(booleanValue(ruleNode, "enabled", true));
            rule.setConditionJoinType(enumValue(ConditionJoinType.class, text(ruleNode, "conditionJoinType"), ConditionJoinType.ALL));
            rule.setResultingSeverityName(firstNonBlank(text(ruleNode, "resultingSeverityName"), "WARNING"));
            rule.setResultingSeverityValue(intValue(ruleNode, "resultingSeverityValue", 40));
            rule.setHumanInterventionRequired(booleanValue(ruleNode, "humanInterventionRequired", false));
            rule.setMinimumStableMillis(longValue(ruleNode, "minimumStableMillis", 0L));
            rule.setRecoveryStableMillis(longValue(ruleNode, "recoveryStableMillis", 0L));
            rule.setSummary(text(ruleNode, "summary"));
            rule.setMitigationInstructions(text(ruleNode, "mitigationInstructions"));
            rule.setConditions(conditions(ruleNode.path("conditions"), signals, existingRule));
            result.add(rule);
            sequence++;
        }
        return result;
    }

    private List<RemoteHealthRuleConditionCreate> conditions(JsonNode conditionsNode,
                                                              Map<String, HealthSignalDefinition> signals,
                                                              RemoteHealthRule existingRule) {
        if (!conditionsNode.isArray()) {
            return List.of();
        }
        Map<String, RemoteHealthRuleCondition> existing = existingRule == null ? Map.of()
                : existingRule.getConditions().stream().filter(f -> !f.isSoftDelete())
                .collect(Collectors.toMap(this::conditionKey, Function.identity(), (a, b) -> a));
        List<RemoteHealthRuleConditionCreate> result = new ArrayList<>();
        int sequence = 0;
        for (JsonNode conditionNode : conditionsNode) {
            String signalExternalId = text(conditionNode, "healthSignal");
            HealthSignalDefinition signal = signals.get(signalExternalId);
            if (signal == null) {
                throw new IllegalArgumentException("health rule references unknown schema signal " + signalExternalId);
            }
            HealthComparisonOperator operator = enumValue(HealthComparisonOperator.class,
                    text(conditionNode, "operator"), HealthComparisonOperator.EQ);
            int priority = intValue(conditionNode, "priority", sequence);
            String key = signal.getId() + "|" + operator + "|" + priority;
            RemoteHealthRuleCondition prior = existing.get(key);
            RemoteHealthRuleConditionCreate condition = new RemoteHealthRuleConditionCreate();
            if (prior != null) {
                condition.setId(prior.getId());
            }
            condition.setName(firstNonBlank(text(conditionNode, "name"), signalExternalId + " " + operator));
            condition.setHealthSignalId(signal.getId());
            condition.setOperator(operator);
            condition.setNumericValue(doubleValue(conditionNode, "numericValue"));
            condition.setSecondNumericValue(doubleValue(conditionNode, "secondNumericValue"));
            condition.setStringValue(text(conditionNode, "stringValue"));
            condition.setBooleanValue(nullableBoolean(conditionNode, "booleanValue"));
            condition.setMissingSignalBehavior(enumValue(MissingSignalBehavior.class,
                    text(conditionNode, "missingSignalBehavior"), MissingSignalBehavior.NO_MATCH));
            condition.setDefaultNumericValue(doubleValue(conditionNode, "defaultNumericValue"));
            condition.setDefaultStringValue(text(conditionNode, "defaultStringValue"));
            condition.setDefaultBooleanValue(nullableBoolean(conditionNode, "defaultBooleanValue"));
            condition.setPriority(priority);
            result.add(condition);
            sequence++;
        }
        return result;
    }

    private void updateDeviceType(DeviceType deviceType,
                                  boolean keepStateHistory,
                                  RemoteHealthProfile profile,
                                  SecurityContext context) {
        boolean profileChanged = profile != null && (deviceType.getDefaultHealthProfile() == null
                || !Objects.equals(deviceType.getDefaultHealthProfile().getId(), profile.getId()));
        if (deviceType.isKeepStateHistory() == keepStateHistory && !profileChanged) {
            return;
        }
        DeviceTypeUpdate update = new DeviceTypeUpdate();
        update.setId(deviceType.getId());
        update.setDeviceType(deviceType);
        update.setKeepStateHistory(keepStateHistory);
        if (profile != null) {
            update.setDefaultHealthProfileId(profile.getId());
            update.setDefaultHealthProfile(profile);
        }
        deviceTypeService.updateDeviceType(update, context);
    }

    private String mappingKey(HealthSignalMapping mapping) {
        String signalId = mapping.getHealthSignal() == null ? "" : mapping.getHealthSignal().getId();
        String propertyId = mapping.getStateProperty() == null ? "" : mapping.getStateProperty().getId();
        return signalId + "|" + propertyId;
    }

    private String conditionKey(RemoteHealthRuleCondition condition) {
        String signalId = condition.getHealthSignal() == null ? "" : condition.getHealthSignal().getId();
        return signalId + "|" + condition.getOperator() + "|" + condition.getPriority();
    }

    private HealthSignalValueType valueType(JsonNode definition) {
        String explicit = text(definition, "x-flexicore-health-value-type");
        if (explicit != null) {
            return enumValue(HealthSignalValueType.class, explicit, HealthSignalValueType.STRING);
        }
        String type = text(definition, "type");
        if ("number".equals(type) || "integer".equals(type)) {
            return HealthSignalValueType.NUMBER;
        }
        if ("boolean".equals(type)) {
            return HealthSignalValueType.BOOLEAN;
        }
        if ("string".equals(type) && definition.path("enum").isArray()) {
            return HealthSignalValueType.ENUM;
        }
        if ("string".equals(type) && "date-time".equals(text(definition, "format"))) {
            return HealthSignalValueType.TIMESTAMP;
        }
        return HealthSignalValueType.STRING;
    }

    private String stableDeviceTypeId(DeviceType deviceType) {
        return firstNonBlank(deviceType.getExternalId(), deviceType.getName(), deviceType.getId());
    }

    private String text(JsonNode node, String field) {
        if (node == null || !node.hasNonNull(field)) {
            return null;
        }
        String value = node.get(field).asText();
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    private int intValue(JsonNode node, String field, int defaultValue) {
        return node != null && node.hasNonNull(field) ? node.get(field).asInt() : defaultValue;
    }

    private long longValue(JsonNode node, String field, long defaultValue) {
        return node != null && node.hasNonNull(field) ? node.get(field).asLong() : defaultValue;
    }

    private Double doubleValue(JsonNode node, String field) {
        return node != null && node.hasNonNull(field) ? node.get(field).asDouble() : null;
    }

    private boolean booleanValue(JsonNode node, String field, boolean defaultValue) {
        return node != null && node.hasNonNull(field) ? node.get(field).asBoolean() : defaultValue;
    }

    private Boolean nullableBoolean(JsonNode node, String field) {
        return node != null && node.hasNonNull(field) ? node.get(field).asBoolean() : null;
    }

    private <E extends Enum<E>> E enumValue(Class<E> type, String value, E defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            throw new IllegalArgumentException("invalid " + type.getSimpleName() + " value " + value);
        }
    }

    private record AnnotatedProperty(String propertyPath,
                                     String propertyExternalId,
                                     String signalExternalId,
                                     HealthSignalValueType valueType,
                                     String unit,
                                     boolean aggregatable) {
    }
}
