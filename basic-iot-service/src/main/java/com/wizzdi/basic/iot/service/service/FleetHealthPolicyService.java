package com.wizzdi.basic.iot.service.service;

import com.flexicore.model.Baseclass;
import com.wizzdi.basic.iot.model.ConditionJoinType;
import com.wizzdi.basic.iot.model.FleetDenominatorPolicy;
import com.wizzdi.basic.iot.model.FleetHealthMetricType;
import com.wizzdi.basic.iot.model.FleetHealthPolicy;
import com.wizzdi.basic.iot.model.FleetHealthRule;
import com.wizzdi.basic.iot.model.FleetHealthRuleCondition;
import com.wizzdi.basic.iot.model.FleetUnknownPolicy;
import com.wizzdi.basic.iot.model.HealthComparisonOperator;
import com.wizzdi.basic.iot.model.RemoteRoleDefinition;
import com.wizzdi.basic.iot.service.data.FleetHealthPolicyRepository;
import com.wizzdi.basic.iot.service.events.FleetHealthPolicyChangedEvent;
import com.wizzdi.basic.iot.service.request.FleetHealthPolicyCreate;
import com.wizzdi.basic.iot.service.request.FleetHealthPolicyFilter;
import com.wizzdi.basic.iot.service.request.FleetHealthPolicyUpdate;
import com.wizzdi.basic.iot.service.request.FleetHealthRuleConditionCreate;
import com.wizzdi.basic.iot.service.request.FleetHealthRuleCreate;
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
public class FleetHealthPolicyService implements Plugin {
    @Autowired
    private FleetHealthPolicyRepository repository;
    @Autowired
    private BasicService basicService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void validate(FleetHealthPolicyCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        if (create.getMinimumPopulation() != null && create.getMinimumPopulation() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minimumPopulation cannot be negative");
        }
        validateRules(create.getRules());
    }

    public void validate(FleetHealthPolicyUpdate update, SecurityContext securityContext) {
        validate((FleetHealthPolicyCreate) update, securityContext);
        FleetHealthPolicy policy = repository.getByIdOrNull(update.getId(), FleetHealthPolicy.class, securityContext);
        if (policy == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible FleetHealthPolicy with id " + update.getId());
        }
        update.setFleetHealthPolicy(policy);
    }

    public void validateFiltering(FleetHealthPolicyFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    private void validateRules(List<FleetHealthRuleCreate> rules) {
        if (rules == null) {
            return;
        }
        Set<String> suppliedIds = new HashSet<>();
        for (FleetHealthRuleCreate rule : rules) {
            if (rule.getId() != null && !suppliedIds.add(rule.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate fleet health rule id " + rule.getId());
            }
            if (rule.getResultingSeverityValue() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Every fleet health rule requires resultingSeverityValue");
            }
            if (rule.getMinimumStableMillis() != null && rule.getMinimumStableMillis() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minimumStableMillis must be non-negative");
            }
            if (rule.getRecoveryStableMillis() != null && rule.getRecoveryStableMillis() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "recoveryStableMillis must be non-negative");
            }
            if (rule.getConditions() == null) {
                if (rule.getId() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Every new fleet health rule requires at least one condition");
                }
                continue;
            }
            if (rule.getConditions().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Every fleet health rule requires at least one condition");
            }
            Set<String> conditionIds = new HashSet<>();
            for (FleetHealthRuleConditionCreate condition : rule.getConditions()) {
                if (condition.getId() != null && !conditionIds.add(condition.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate fleet health condition id " + condition.getId());
                }
                if (condition.getMetricType() == null || condition.getOperator() == null || condition.getThreshold() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Every fleet health condition requires metricType, operator and threshold");
                }
                if (condition.getOperator() == HealthComparisonOperator.BETWEEN && condition.getSecondThreshold() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "BETWEEN requires secondThreshold");
                }
                if ((condition.getMetricType() == FleetHealthMetricType.STALE_COUNT || condition.getMetricType() == FleetHealthMetricType.STALE_PERCENT)
                        && (condition.getStaleAfterSeconds() == null || condition.getStaleAfterSeconds() < 0)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stale metrics require non-negative staleAfterSeconds");
                }
                if ((condition.getMetricType() == FleetHealthMetricType.SEVERITY_AT_OR_ABOVE_COUNT
                        || condition.getMetricType() == FleetHealthMetricType.SEVERITY_AT_OR_ABOVE_PERCENT
                        || condition.getMetricType() == FleetHealthMetricType.REQUIRED_UNHEALTHY_COUNT)
                        && condition.getSeverityThresholdValue() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Severity metrics require severityThresholdValue");
                }
            }
        }
    }

    @Transactional
    public FleetHealthPolicy create(FleetHealthPolicyCreate create, SecurityContext securityContext) {
        FleetHealthPolicy policy = new FleetHealthPolicy();
        policy.setId(UUID.randomUUID().toString());
        updatePolicyNoMerge(policy, create);
        policy.setEvaluationVersion(1);
        BaseclassService.createSecurityObjectNoMerge(policy, securityContext);
        repository.merge(policy);
        if (create.getRules() != null) {
            syncRules(policy, create.getRules(), securityContext);
        }
        eventPublisher.publishEvent(new FleetHealthPolicyChangedEvent(policy.getId(), policy.getEvaluationVersion(), OffsetDateTime.now()));
        return populate(policy);
    }

    @Transactional
    public FleetHealthPolicy update(FleetHealthPolicyUpdate update, SecurityContext securityContext) {
        FleetHealthPolicy policy = update.getFleetHealthPolicy();
        boolean definitionChanged = updatePolicyNoMerge(policy, update);
        if (update.getRules() != null) {
            syncRules(policy, update.getRules(), securityContext);
            definitionChanged = true;
        }
        if (definitionChanged) {
            policy.setEvaluationVersion(Math.max(1, policy.getEvaluationVersion() + 1));
            repository.merge(policy);
            eventPublisher.publishEvent(new FleetHealthPolicyChangedEvent(policy.getId(), policy.getEvaluationVersion(), OffsetDateTime.now()));
        }
        return populate(policy);
    }

    private boolean updatePolicyNoMerge(FleetHealthPolicy policy, FleetHealthPolicyCreate create) {
        boolean changed = basicService.updateBasicNoMerge(create, policy);
        if (create.getExternalId() != null && !Objects.equals(create.getExternalId(), policy.getExternalId())) {
            policy.setExternalId(create.getExternalId());
            changed = true;
        }
        if (create.getEnabled() != null && create.getEnabled() != policy.isEnabled()) {
            policy.setEnabled(create.getEnabled());
            changed = true;
        }
        if (create.getMinimumPopulation() != null && !Objects.equals(create.getMinimumPopulation(), policy.getMinimumPopulation())) {
            policy.setMinimumPopulation(create.getMinimumPopulation());
            changed = true;
        }
        if (create.getDefaultSeverityName() != null && !Objects.equals(create.getDefaultSeverityName(), policy.getDefaultSeverityName())) {
            policy.setDefaultSeverityName(create.getDefaultSeverityName());
            changed = true;
        }
        if (create.getDefaultSeverityValue() != null && !Objects.equals(create.getDefaultSeverityValue(), policy.getDefaultSeverityValue())) {
            policy.setDefaultSeverityValue(create.getDefaultSeverityValue());
            changed = true;
        }
        if (create.getUnknownPolicy() != null && create.getUnknownPolicy() != policy.getUnknownPolicy()) {
            policy.setUnknownPolicy(create.getUnknownPolicy());
            changed = true;
        }
        return changed;
    }

    private void syncRules(FleetHealthPolicy policy, List<FleetHealthRuleCreate> requested, SecurityContext securityContext) {
        List<FleetHealthRule> existing = repository.listRules(policy.getId());
        Map<String, FleetHealthRule> existingById = existing.stream().collect(Collectors.toMap(Baseclass::getId, Function.identity()));
        Set<String> retained = new HashSet<>();
        List<Object> toMerge = new ArrayList<>();

        for (FleetHealthRuleCreate item : requested) {
            FleetHealthRule rule = item.getId() == null ? null : existingById.get(item.getId());
            if (item.getId() != null && rule == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FleetHealthRule " + item.getId() + " does not belong to policy " + policy.getId());
            }
            if (rule == null) {
                rule = new FleetHealthRule();
                rule.setId(UUID.randomUUID().toString());
                rule.setFleetHealthPolicy(policy);
                basicService.updateBasicNoMerge(item, rule);
                BaseclassService.createSecurityObjectNoMerge(rule, securityContext);
            } else {
                basicService.updateBasicNoMerge(item, rule);
            }
            rule.setPriority(item.getPriority() == null ? 0 : item.getPriority());
            rule.setEnabled(item.getEnabled() == null || item.getEnabled());
            rule.setConditionJoinType(item.getConditionJoinType() == null ? ConditionJoinType.ALL : item.getConditionJoinType());
            rule.setResultingSeverityName(item.getResultingSeverityName());
            rule.setResultingSeverityValue(item.getResultingSeverityValue());
            rule.setHumanInterventionRequired(Boolean.TRUE.equals(item.getHumanInterventionRequired()));
            rule.setMinimumStableMillis(item.getMinimumStableMillis() == null ? 0L : item.getMinimumStableMillis());
            rule.setRecoveryStableMillis(item.getRecoveryStableMillis() == null ? 0L : item.getRecoveryStableMillis());
            rule.setSoftDelete(false);
            retained.add(rule.getId());
            toMerge.add(rule);
            if (item.getConditions() != null) {
                syncConditions(rule, item.getConditions(), securityContext, toMerge);
            }
        }

        for (FleetHealthRule old : existing) {
            if (!retained.contains(old.getId())) {
                old.setSoftDelete(true);
                toMerge.add(old);
            }
        }
        repository.massMerge(toMerge);
    }

    private void syncConditions(FleetHealthRule rule, List<FleetHealthRuleConditionCreate> requested, SecurityContext securityContext, List<Object> toMerge) {
        List<FleetHealthRuleCondition> existing = repository.listConditions(List.of(rule.getId()));
        Map<String, FleetHealthRuleCondition> existingById = existing.stream().collect(Collectors.toMap(Baseclass::getId, Function.identity()));
        Set<String> retained = new HashSet<>();

        for (FleetHealthRuleConditionCreate item : requested) {
            RemoteRoleDefinition role = null;
            if (item.getMemberRoleId() != null && !item.getMemberRoleId().isBlank()) {
                role = repository.getByIdOrNull(item.getMemberRoleId(), RemoteRoleDefinition.class, securityContext);
                if (role == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible RemoteRoleDefinition with id " + item.getMemberRoleId());
                }
            }
            FleetHealthRuleCondition condition = item.getId() == null ? null : existingById.get(item.getId());
            if (item.getId() != null && condition == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FleetHealthRuleCondition " + item.getId() + " does not belong to rule " + rule.getId());
            }
            if (condition == null) {
                condition = new FleetHealthRuleCondition();
                condition.setId(UUID.randomUUID().toString());
                condition.setFleetHealthRule(rule);
                basicService.updateBasicNoMerge(item, condition);
                BaseclassService.createSecurityObjectNoMerge(condition, securityContext);
            } else {
                basicService.updateBasicNoMerge(item, condition);
            }
            condition.setMetricType(item.getMetricType());
            condition.setOperator(item.getOperator());
            condition.setThreshold(item.getThreshold());
            condition.setSecondThreshold(item.getSecondThreshold());
            condition.setSeverityThresholdValue(item.getSeverityThresholdValue());
            condition.setStaleAfterSeconds(item.getStaleAfterSeconds());
            condition.setRequiredMembersOnly(Boolean.TRUE.equals(item.getRequiredMembersOnly()));
            condition.setMemberRole(role);
            condition.setDenominatorPolicy(item.getDenominatorPolicy() == null ? FleetDenominatorPolicy.ALL_ELIGIBLE_MEMBERS : item.getDenominatorPolicy());
            condition.setSoftDelete(false);
            retained.add(condition.getId());
            toMerge.add(condition);
        }

        for (FleetHealthRuleCondition old : existing) {
            if (!retained.contains(old.getId())) {
                old.setSoftDelete(true);
                toMerge.add(old);
            }
        }
    }

    public PaginationResponse<FleetHealthPolicy> getAll(SecurityContext securityContext, FleetHealthPolicyFilter filter) {
        List<FleetHealthPolicy> list = repository.list(securityContext, filter);
        list.forEach(this::populate);
        return new PaginationResponse<>(list, filter, repository.count(securityContext, filter));
    }

    public FleetHealthPolicy populate(FleetHealthPolicy policy) {
        List<FleetHealthRule> rules = repository.listRules(policy.getId());
        Map<String, List<FleetHealthRuleCondition>> byRule = new HashMap<>();
        for (FleetHealthRuleCondition condition : repository.listConditions(rules.stream().map(Baseclass::getId).toList())) {
            byRule.computeIfAbsent(condition.getFleetHealthRule().getId(), key -> new ArrayList<>()).add(condition);
        }
        for (FleetHealthRule rule : rules) {
            rule.setConditions(byRule.getOrDefault(rule.getId(), List.of()));
        }
        policy.setRules(rules);
        return policy;
    }
}
