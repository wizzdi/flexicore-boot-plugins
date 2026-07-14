package com.wizzdi.basic.iot.service.service;

import com.flexicore.model.Baseclass;
import com.wizzdi.basic.iot.model.ConditionJoinType;
import com.wizzdi.basic.iot.model.HealthComparisonOperator;
import com.wizzdi.basic.iot.model.HealthSignalDefinition;
import com.wizzdi.basic.iot.model.HealthSignalMapping;
import com.wizzdi.basic.iot.model.HealthSignalMappingOperation;
import com.wizzdi.basic.iot.model.HealthSignalSourceType;
import com.wizzdi.basic.iot.model.HealthSignalValueType;
import com.wizzdi.basic.iot.model.MissingSignalBehavior;
import com.wizzdi.basic.iot.model.RemoteHealthProfile;
import com.wizzdi.basic.iot.model.RemoteHealthRule;
import com.wizzdi.basic.iot.model.RemoteHealthRuleCondition;
import com.wizzdi.basic.iot.model.StatePropertyDefinition;
import com.wizzdi.basic.iot.service.data.RemoteHealthProfileRepository;
import com.wizzdi.basic.iot.service.events.RemoteHealthProfileChangedEvent;
import com.wizzdi.basic.iot.service.request.HealthSignalMappingCreate;
import com.wizzdi.basic.iot.service.request.RemoteHealthProfileCreate;
import com.wizzdi.basic.iot.service.request.RemoteHealthProfileFilter;
import com.wizzdi.basic.iot.service.request.RemoteHealthProfileUpdate;
import com.wizzdi.basic.iot.service.request.RemoteHealthRuleConditionCreate;
import com.wizzdi.basic.iot.service.request.RemoteHealthRuleCreate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Extension
@Component
public class RemoteHealthProfileService implements Plugin {

    @Autowired
    private RemoteHealthProfileRepository repository;
    @Autowired
    private BasicService basicService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private DerivedEntitySecurityService derivedEntitySecurityService;

    public void validateFiltering(RemoteHealthProfileFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public void validate(RemoteHealthProfileCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        boolean updating = create instanceof RemoteHealthProfileUpdate;
        if (updating) {
            RemoteHealthProfileUpdate update = (RemoteHealthProfileUpdate) create;
            RemoteHealthProfile existing = repository.getByIdOrNull(update.getId(), RemoteHealthProfile.class, securityContext);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible RemoteHealthProfile with id " + update.getId());
            }
            update.setRemoteHealthProfile(existing);
        }
        if (!updating && (create.getExternalId() == null || create.getExternalId().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "externalId is required");
        }
        if (create.getActionRequiredFromSeverityValue() != null && create.getActionRequiredFromSeverityValue() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "actionRequiredFromSeverityValue must be non-negative");
        }
        validateMappings(create.getMappings(), securityContext);
        validateRules(create.getRules(), securityContext);
    }

    private void validateMappings(List<HealthSignalMappingCreate> mappings, SecurityContext securityContext) {
        if (mappings == null) {
            return;
        }
        Set<String> ids = new HashSet<>();
        Set<String> uniqueness = new HashSet<>();
        for (HealthSignalMappingCreate mapping : mappings) {
            basicService.validate(mapping, securityContext);
            if (mapping.getId() != null && !ids.add(mapping.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate HealthSignalMapping id " + mapping.getId());
            }
            if (mapping.getHealthSignalId() == null || mapping.getHealthSignalId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "healthSignalId is required for every mapping");
            }
            HealthSignalDefinition signal = repository.getByIdOrNull(mapping.getHealthSignalId(), HealthSignalDefinition.class, securityContext);
            if (signal == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible HealthSignalDefinition with id " + mapping.getHealthSignalId());
            }
            HealthSignalSourceType sourceType = mapping.getSourceType();
            if (sourceType == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sourceType is required for every mapping");
            }
            StatePropertyDefinition property = null;
            if (sourceType == HealthSignalSourceType.STATE_PROPERTY) {
                if (mapping.getStatePropertyId() == null || mapping.getStatePropertyId().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "statePropertyId is required for STATE_PROPERTY mappings");
                }
                property = repository.getByIdOrNull(mapping.getStatePropertyId(), StatePropertyDefinition.class, securityContext);
                if (property == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible StatePropertyDefinition with id " + mapping.getStatePropertyId());
                }
            } else if (mapping.getStatePropertyId() != null && !mapping.getStatePropertyId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "statePropertyId is only valid for STATE_PROPERTY mappings");
            }
            String schemaId = property == null || property.getStateSchema() == null ? "built-in" : property.getStateSchema().getId();
            String key = signal.getId() + "|" + sourceType + "|" + schemaId;
            if (!uniqueness.add(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate mapping for signal " + signal.getExternalId() + " and source " + sourceType + " in schema " + schemaId);
            }
            validateMappingOperation(mapping, signal, property);
        }
    }

    private void validateMappingOperation(HealthSignalMappingCreate mapping, HealthSignalDefinition signal, StatePropertyDefinition property) {
        HealthSignalMappingOperation operation = mapping.getOperation() == null ? HealthSignalMappingOperation.DIRECT : mapping.getOperation();
        HealthSignalValueType targetType = signal.getValueType();
        HealthSignalValueType sourceType = property == null ? builtInSourceValueType(mapping.getSourceType()) : property.getValueType();
        if (sourceType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot determine source value type for " + mapping.getSourceType());
        }
        switch (operation) {
            case DIRECT -> {
                boolean compatible = sourceType == targetType
                        || sourceType == HealthSignalValueType.STRING && targetType == HealthSignalValueType.ENUM
                        || sourceType == HealthSignalValueType.ENUM && targetType == HealthSignalValueType.STRING;
                if (!compatible) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DIRECT mapping cannot map " + sourceType + " to " + targetType);
                }
            }
            case NUMERIC_SCALE, NUMERIC_OFFSET -> {
                if (sourceType != HealthSignalValueType.NUMBER || targetType != HealthSignalValueType.NUMBER) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, operation + " requires NUMBER source and target");
                }
            }
            case ENUM_EQUALS -> {
                if (sourceType != HealthSignalValueType.STRING && sourceType != HealthSignalValueType.ENUM) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ENUM_EQUALS requires STRING or ENUM source");
                }
                if (targetType != HealthSignalValueType.BOOLEAN) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ENUM_EQUALS requires BOOLEAN target signal");
                }
                if (mapping.getExpectedStringValue() == null || mapping.getExpectedStringValue().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "expectedStringValue is required for ENUM_EQUALS");
                }
            }
            case BOOLEAN_NEGATE -> {
                if (sourceType != HealthSignalValueType.BOOLEAN || targetType != HealthSignalValueType.BOOLEAN) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BOOLEAN_NEGATE requires BOOLEAN source and target");
                }
            }
            case TIMESTAMP_AGE_SECONDS -> {
                if (sourceType != HealthSignalValueType.TIMESTAMP || targetType != HealthSignalValueType.NUMBER) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TIMESTAMP_AGE_SECONDS requires TIMESTAMP source and NUMBER target");
                }
            }
        }
    }

    private HealthSignalValueType builtInSourceValueType(HealthSignalSourceType sourceType) {
        return switch (sourceType) {
            case REMOTE_CONNECTIVITY -> HealthSignalValueType.ENUM;
            case REMOTE_LAST_SEEN_AGE_SECONDS, REMOTE_CURRENT_SEVERITY -> HealthSignalValueType.NUMBER;
            case REMOTE_VERSION -> HealthSignalValueType.STRING;
            case REMOTE_HUMAN_INTERVENTION -> HealthSignalValueType.BOOLEAN;
            case STATE_PROPERTY -> null;
        };
    }

    private void validateRules(List<RemoteHealthRuleCreate> rules, SecurityContext securityContext) {
        if (rules == null) {
            return;
        }
        Set<String> ruleIds = new HashSet<>();
        for (RemoteHealthRuleCreate rule : rules) {
            basicService.validate(rule, securityContext);
            if (rule.getId() != null && !ruleIds.add(rule.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate RemoteHealthRule id " + rule.getId());
            }
            if (rule.getResultingSeverityValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "resultingSeverityValue is required for every rule");
            }
            if (rule.getMinimumStableMillis() != null && rule.getMinimumStableMillis() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minimumStableMillis must be non-negative");
            }
            if (rule.getRecoveryStableMillis() != null && rule.getRecoveryStableMillis() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recoveryStableMillis must be non-negative");
            }
            if ((rule.getConditions() == null || rule.getConditions().isEmpty()) && rule.getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Every new RemoteHealthRule requires at least one condition");
            }
            if (rule.getConditions() == null) {
                continue;
            }
            Set<String> conditionIds = new HashSet<>();
            for (RemoteHealthRuleConditionCreate condition : rule.getConditions()) {
                basicService.validate(condition, securityContext);
                if (condition.getId() != null && !conditionIds.add(condition.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate RemoteHealthRuleCondition id " + condition.getId());
                }
                if (condition.getHealthSignalId() == null || condition.getHealthSignalId().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "healthSignalId is required for every condition");
                }
                HealthSignalDefinition signal = repository.getByIdOrNull(condition.getHealthSignalId(), HealthSignalDefinition.class, securityContext);
                if (signal == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible HealthSignalDefinition with id " + condition.getHealthSignalId());
                }
                if (condition.getOperator() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "operator is required for every condition");
                }
                validateConditionValue(condition, signal);
            }
        }
    }

    private void validateConditionValue(RemoteHealthRuleConditionCreate condition, HealthSignalDefinition signal) {
        HealthSignalValueType type = signal.getValueType();
        if (type == HealthSignalValueType.NUMBER || type == HealthSignalValueType.TIMESTAMP) {
            if (condition.getNumericValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numericValue is required for signal " + signal.getExternalId());
            }
            if (condition.getOperator() == HealthComparisonOperator.BETWEEN && condition.getSecondNumericValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "secondNumericValue is required for BETWEEN");
            }
        } else if (type == HealthSignalValueType.BOOLEAN) {
            if (condition.getBooleanValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "booleanValue is required for signal " + signal.getExternalId());
            }
            if (condition.getOperator() != HealthComparisonOperator.EQ && condition.getOperator() != HealthComparisonOperator.NE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BOOLEAN signals support only EQ and NE");
            }
        } else {
            if (condition.getStringValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stringValue is required for signal " + signal.getExternalId());
            }
            if (condition.getOperator() != HealthComparisonOperator.EQ && condition.getOperator() != HealthComparisonOperator.NE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "STRING and ENUM signals support only EQ and NE");
            }
        }
        MissingSignalBehavior missing = condition.getMissingSignalBehavior() == null ? MissingSignalBehavior.NO_MATCH : condition.getMissingSignalBehavior();
        if (missing == MissingSignalBehavior.USE_DEFAULT_VALUE) {
            if ((type == HealthSignalValueType.NUMBER || type == HealthSignalValueType.TIMESTAMP) && condition.getDefaultNumericValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "defaultNumericValue is required when missingSignalBehavior is USE_DEFAULT_VALUE");
            }
            if (type == HealthSignalValueType.BOOLEAN && condition.getDefaultBooleanValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "defaultBooleanValue is required when missingSignalBehavior is USE_DEFAULT_VALUE");
            }
            if ((type == HealthSignalValueType.STRING || type == HealthSignalValueType.ENUM) && condition.getDefaultStringValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "defaultStringValue is required when missingSignalBehavior is USE_DEFAULT_VALUE");
            }
        }
    }

    public PaginationResponse<RemoteHealthProfile> getAll(SecurityContext securityContext, RemoteHealthProfileFilter filter) {
        List<RemoteHealthProfile> list = repository.list(securityContext, filter);
        list.forEach(this::populate);
        return new PaginationResponse<>(list, filter, repository.count(securityContext, filter));
    }

    @Transactional
    public RemoteHealthProfile create(RemoteHealthProfileCreate create, SecurityContext securityContext) {
        RemoteHealthProfile profile = new RemoteHealthProfile();
        profile.setId(UUID.randomUUID().toString());
        updateProfileNoMerge(profile, create);
        profile.setEvaluationVersion(1);
        BaseclassService.createSecurityObjectNoMerge(profile, securityContext);
        List<Object> toMerge = new ArrayList<>();
        toMerge.add(profile);
        syncMappings(profile, create.getMappings(), securityContext, toMerge);
        syncRules(profile, create.getRules(), securityContext, toMerge);
        repository.massMerge(toMerge);
        eventPublisher.publishEvent(new RemoteHealthProfileChangedEvent(profile.getId(), profile.getEvaluationVersion(), OffsetDateTime.now()));
        return populate(profile);
    }

    @Transactional
    public RemoteHealthProfile update(RemoteHealthProfileUpdate update, SecurityContext securityContext) {
        RemoteHealthProfile profile = update.getRemoteHealthProfile();
        List<Object> toMerge = new ArrayList<>();
        boolean definitionChanged = updateProfileNoMerge(profile, update);
        if (update.getMappings() != null) {
            syncMappings(profile, update.getMappings(), securityContext, toMerge);
            definitionChanged = true;
        }
        if (update.getRules() != null) {
            syncRules(profile, update.getRules(), securityContext, toMerge);
            definitionChanged = true;
        }
        if (definitionChanged) {
            profile.setEvaluationVersion(Math.max(1, profile.getEvaluationVersion() + 1));
            if (!toMerge.contains(profile)) {
                toMerge.add(0, profile);
            }
        }
        if (!toMerge.isEmpty()) {
            repository.massMerge(toMerge);
        }
        if (definitionChanged) {
            eventPublisher.publishEvent(new RemoteHealthProfileChangedEvent(profile.getId(), profile.getEvaluationVersion(), OffsetDateTime.now()));
        }
        return populate(profile);
    }

    private boolean updateProfileNoMerge(RemoteHealthProfile profile, RemoteHealthProfileCreate create) {
        boolean changed = basicService.updateBasicNoMerge(create, profile);
        if (create.getExternalId() != null && !Objects.equals(profile.getExternalId(), create.getExternalId())) {
            profile.setExternalId(create.getExternalId());
            changed = true;
        }
        if (create.getEnabled() != null && profile.isEnabled() != create.getEnabled()) {
            profile.setEnabled(create.getEnabled());
            changed = true;
        }
        if (create.getDefaultSeverityName() != null && !Objects.equals(profile.getDefaultSeverityName(), create.getDefaultSeverityName())) {
            profile.setDefaultSeverityName(create.getDefaultSeverityName());
            changed = true;
        }
        if (create.getDefaultSeverityValue() != null && !Objects.equals(profile.getDefaultSeverityValue(), create.getDefaultSeverityValue())) {
            profile.setDefaultSeverityValue(create.getDefaultSeverityValue());
            changed = true;
        }
        if (create.getActionRequiredFromSeverityValue() != null
                && !Objects.equals(profile.getActionRequiredFromSeverityValue(), create.getActionRequiredFromSeverityValue())) {
            profile.setActionRequiredFromSeverityValue(create.getActionRequiredFromSeverityValue());
            changed = true;
        }
        return changed;
    }

    private void syncMappings(RemoteHealthProfile profile, List<HealthSignalMappingCreate> requested, SecurityContext securityContext, List<Object> toMerge) {
        if (requested == null) {
            return;
        }
        List<HealthSignalMapping> existing = repository.listMappings(profile.getId());
        Map<String, HealthSignalMapping> existingById = existing.stream().collect(Collectors.toMap(Baseclass::getId, Function.identity()));
        Set<String> retained = new HashSet<>();

        for (HealthSignalMappingCreate item : requested) {
            HealthSignalDefinition signal = repository.getByIdOrNull(item.getHealthSignalId(), HealthSignalDefinition.class, securityContext);
            StatePropertyDefinition property = item.getStatePropertyId() == null || item.getStatePropertyId().isBlank()
                    ? null
                    : repository.getByIdOrNull(item.getStatePropertyId(), StatePropertyDefinition.class, securityContext);
            HealthSignalMapping mapping = item.getId() == null ? null : existingById.get(item.getId());
            if (item.getId() != null && mapping == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HealthSignalMapping " + item.getId() + " does not belong to profile " + profile.getId());
            }
            if (mapping == null) {
                mapping = new HealthSignalMapping();
                mapping.setId(UUID.randomUUID().toString());
                mapping.setRemoteHealthProfile(profile);
                basicService.updateBasicNoMerge(item, mapping);
                BaseclassService.createSecurityObjectNoMerge(mapping, derivedEntitySecurityService.creationContext(securityContext, profile));
            } else {
                basicService.updateBasicNoMerge(item, mapping);
            }
            derivedEntitySecurityService.setTenantFrom(mapping, profile);
            mapping.setHealthSignal(signal);
            mapping.setSourceType(item.getSourceType());
            mapping.setStateProperty(property);
            mapping.setOperation(item.getOperation() == null ? HealthSignalMappingOperation.DIRECT : item.getOperation());
            mapping.setMultiplier(item.getMultiplier());
            mapping.setOffset(item.getOffset());
            mapping.setExpectedStringValue(item.getExpectedStringValue());
            mapping.setNegateBoolean(item.getNegateBoolean());
            mapping.setPriority(item.getPriority() == null ? 0 : item.getPriority());
            mapping.setSoftDelete(false);
            retained.add(mapping.getId());
            toMerge.add(mapping);
        }
        for (HealthSignalMapping old : existing) {
            if (!retained.contains(old.getId())) {
                old.setSoftDelete(true);
                toMerge.add(old);
            }
        }
    }

    private void syncRules(RemoteHealthProfile profile, List<RemoteHealthRuleCreate> requested, SecurityContext securityContext, List<Object> toMerge) {
        if (requested == null) {
            return;
        }
        List<RemoteHealthRule> existing = repository.listRules(profile.getId());
        Map<String, RemoteHealthRule> existingById = existing.stream().collect(Collectors.toMap(Baseclass::getId, Function.identity()));
        Set<String> retained = new HashSet<>();

        for (RemoteHealthRuleCreate item : requested) {
            RemoteHealthRule rule = item.getId() == null ? null : existingById.get(item.getId());
            if (item.getId() != null && rule == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RemoteHealthRule " + item.getId() + " does not belong to profile " + profile.getId());
            }
            if (rule == null) {
                rule = new RemoteHealthRule();
                rule.setId(UUID.randomUUID().toString());
                rule.setRemoteHealthProfile(profile);
                basicService.updateBasicNoMerge(item, rule);
                BaseclassService.createSecurityObjectNoMerge(rule, derivedEntitySecurityService.creationContext(securityContext, profile));
            } else {
                basicService.updateBasicNoMerge(item, rule);
            }
            derivedEntitySecurityService.setTenantFrom(rule, profile);
            rule.setPriority(item.getPriority() == null ? 0 : item.getPriority());
            rule.setEnabled(item.getEnabled() == null || item.getEnabled());
            rule.setConditionJoinType(item.getConditionJoinType() == null ? ConditionJoinType.ALL : item.getConditionJoinType());
            rule.setResultingSeverityName(item.getResultingSeverityName());
            rule.setResultingSeverityValue(item.getResultingSeverityValue());
            rule.setHumanInterventionRequired(Boolean.TRUE.equals(item.getHumanInterventionRequired()));
            rule.setMinimumStableMillis(item.getMinimumStableMillis() == null ? 0L : item.getMinimumStableMillis());
            rule.setRecoveryStableMillis(item.getRecoveryStableMillis() == null ? 0L : item.getRecoveryStableMillis());
            rule.setSummary(item.getSummary());
            rule.setMitigationInstructions(item.getMitigationInstructions());
            rule.setSoftDelete(false);
            retained.add(rule.getId());
            toMerge.add(rule);
            if (item.getConditions() != null) {
                syncConditions(rule, item.getConditions(), securityContext, toMerge);
            }
        }
        for (RemoteHealthRule old : existing) {
            if (!retained.contains(old.getId())) {
                old.setSoftDelete(true);
                toMerge.add(old);
                for (RemoteHealthRuleCondition condition : repository.listConditions(List.of(old.getId()))) {
                    condition.setSoftDelete(true);
                    toMerge.add(condition);
                }
            }
        }
    }

    private void syncConditions(RemoteHealthRule rule, List<RemoteHealthRuleConditionCreate> requested, SecurityContext securityContext, List<Object> toMerge) {
        List<RemoteHealthRuleCondition> existing = repository.listConditions(List.of(rule.getId()));
        Map<String, RemoteHealthRuleCondition> existingById = existing.stream().collect(Collectors.toMap(Baseclass::getId, Function.identity()));
        Set<String> retained = new HashSet<>();

        for (RemoteHealthRuleConditionCreate item : requested) {
            HealthSignalDefinition signal = repository.getByIdOrNull(item.getHealthSignalId(), HealthSignalDefinition.class, securityContext);
            RemoteHealthRuleCondition condition = item.getId() == null ? null : existingById.get(item.getId());
            if (item.getId() != null && condition == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RemoteHealthRuleCondition " + item.getId() + " does not belong to rule " + rule.getId());
            }
            if (condition == null) {
                condition = new RemoteHealthRuleCondition();
                condition.setId(UUID.randomUUID().toString());
                condition.setRemoteHealthRule(rule);
                basicService.updateBasicNoMerge(item, condition);
                BaseclassService.createSecurityObjectNoMerge(condition, derivedEntitySecurityService.creationContext(securityContext, rule));
            } else {
                basicService.updateBasicNoMerge(item, condition);
            }
            derivedEntitySecurityService.setTenantFrom(condition, rule);
            condition.setHealthSignal(signal);
            condition.setOperator(item.getOperator());
            condition.setNumericValue(item.getNumericValue());
            condition.setSecondNumericValue(item.getSecondNumericValue());
            condition.setStringValue(item.getStringValue());
            condition.setBooleanValue(item.getBooleanValue());
            condition.setMissingSignalBehavior(item.getMissingSignalBehavior() == null ? MissingSignalBehavior.NO_MATCH : item.getMissingSignalBehavior());
            condition.setDefaultNumericValue(item.getDefaultNumericValue());
            condition.setDefaultStringValue(item.getDefaultStringValue());
            condition.setDefaultBooleanValue(item.getDefaultBooleanValue());
            condition.setPriority(item.getPriority() == null ? 0 : item.getPriority());
            condition.setSoftDelete(false);
            retained.add(condition.getId());
            toMerge.add(condition);
        }
        for (RemoteHealthRuleCondition old : existing) {
            if (!retained.contains(old.getId())) {
                old.setSoftDelete(true);
                toMerge.add(old);
            }
        }
    }

    @Transactional
    public RemoteHealthProfile incrementDefinitionVersion(String profileId) {
        RemoteHealthProfile profile = repository.getByIdOrNull(profileId, RemoteHealthProfile.class, null);
        if (profile == null || profile.isSoftDelete()) {
            return null;
        }
        profile.setEvaluationVersion(Math.max(1, profile.getEvaluationVersion() + 1));
        repository.merge(profile);
        eventPublisher.publishEvent(new RemoteHealthProfileChangedEvent(
                profile.getId(), profile.getEvaluationVersion(), OffsetDateTime.now()));
        return profile;
    }

    public RemoteHealthProfile populate(RemoteHealthProfile profile) {
        List<HealthSignalMapping> mappings = repository.listMappings(profile.getId());
        List<RemoteHealthRule> rules = repository.listRules(profile.getId());
        Map<String, List<RemoteHealthRuleCondition>> byRule = new HashMap<>();
        for (RemoteHealthRuleCondition condition : repository.listConditions(rules.stream().map(Baseclass::getId).toList())) {
            byRule.computeIfAbsent(condition.getRemoteHealthRule().getId(), key -> new ArrayList<>()).add(condition);
        }
        for (RemoteHealthRule rule : rules) {
            rule.setConditions(byRule.getOrDefault(rule.getId(), List.of()));
        }
        profile.setMappings(mappings);
        profile.setRules(rules);
        return profile;
    }
}
