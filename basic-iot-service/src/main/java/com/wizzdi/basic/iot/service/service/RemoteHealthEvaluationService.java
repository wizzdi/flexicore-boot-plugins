package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.ConditionJoinType;
import com.wizzdi.basic.iot.model.Connectivity;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.HealthComparisonOperator;
import com.wizzdi.basic.iot.model.HealthSignalDefinition;
import com.wizzdi.basic.iot.model.HealthSignalMapping;
import com.wizzdi.basic.iot.model.HealthSignalMappingOperation;
import com.wizzdi.basic.iot.model.HealthSignalSourceType;
import com.wizzdi.basic.iot.model.HealthSignalValueType;
import com.wizzdi.basic.iot.model.MissingSignalBehavior;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteHealthProfile;
import com.wizzdi.basic.iot.model.RemoteHealthRule;
import com.wizzdi.basic.iot.model.RemoteHealthRuleCondition;
import com.wizzdi.basic.iot.model.StatePropertyDefinition;
import com.wizzdi.basic.iot.service.events.RemoteHealthChangedEvent;
import com.wizzdi.basic.iot.service.events.SeverityChangedEvent;
import com.wizzdi.basic.iot.service.request.EvaluateRemoteHealthRequest;
import com.wizzdi.basic.iot.service.response.RemoteHealthSnapshot;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Extension
@Component
public class RemoteHealthEvaluationService implements Plugin {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private RemoteHealthProfileService profileService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private RemoteService remoteService;

    public void validate(EvaluateRemoteHealthRequest request, SecurityContext securityContext) {
        if (request.getRemoteId() == null || request.getRemoteId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "remoteId is required");
        }
        Remote remote = remoteService.getByIdOrNull(request.getRemoteId(), Remote.class, securityContext);
        if (remote == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible Remote with id " + request.getRemoteId());
        }
        request.setRemote(remote);
    }

    public RemoteHealthProfile getEffectiveProfile(Remote remote) {
        if (remote.getHealthProfile() != null) {
            return remote.getHealthProfile();
        }
        if (remote instanceof Device device && device.getDeviceType() != null) {
            return device.getDeviceType().getDefaultHealthProfile();
        }
        return null;
    }

    /**
     * Returns true only when at least one mapping in the effective profile can
     * be affected by the coalesced input changes. A force request is used for
     * profile/schema changes and initial evaluation.
     */
    public boolean isEvaluationRelevant(Remote remote,
                                        Set<String> changedStatePaths,
                                        Set<HealthSignalSourceType> changedSourceTypes,
                                        boolean force) {
        RemoteHealthProfile profile = getEffectiveProfile(remote);
        if (force) {
            return true;
        }
        if (profile == null || !profile.isEnabled()) {
            return false;
        }
        profileService.populate(profile);
        Set<String> paths = changedStatePaths == null ? Set.of() : changedStatePaths;
        Set<HealthSignalSourceType> sourceTypes = changedSourceTypes == null ? Set.of() : changedSourceTypes;
        for (HealthSignalMapping mapping : profile.getMappings()) {
            if (mapping == null || mapping.isSoftDelete() || !mappingApplies(remote, mapping)) {
                continue;
            }
            if (mapping.getSourceType() == HealthSignalSourceType.STATE_PROPERTY) {
                StatePropertyDefinition property = mapping.getStateProperty();
                if (property != null && pathChanged(property.getPropertyPath(), paths)) {
                    return true;
                }
            } else if (sourceTypes.contains(mapping.getSourceType())) {
                return true;
            }
        }
        return false;
    }

    private boolean pathChanged(String mappedPath, Set<String> changedPaths) {
        if (mappedPath == null || mappedPath.isBlank() || changedPaths == null || changedPaths.isEmpty()) {
            return false;
        }
        for (String changedPath : changedPaths) {
            if ("*".equals(changedPath)
                    || mappedPath.equals(changedPath)
                    || mappedPath.startsWith(changedPath + ".")
                    || changedPath.startsWith(mappedPath + ".")) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public RemoteHealthSnapshot evaluate(Remote remote, OffsetDateTime now) {
        RemoteHealthProfile profile = getEffectiveProfile(remote);
        if (profile == null || !profile.isEnabled()) {
            return clearHealthProjection(remote, profile, now);
        }
        profileService.populate(profile);
        ResolvedSignals resolved = resolveSignals(remote, profile.getMappings(), profile.getRules(), now);
        RemoteHealthRule selected = selectRule(profile.getRules(), resolved.values());
        HealthOutcome defaultOutcome = new HealthOutcome(
                profile.getDefaultSeverityName(),
                profile.getDefaultSeverityValue(),
                null,
                false,
                null,
                null);
        HealthOutcome candidate = selected == null
                ? defaultOutcome
                : new HealthOutcome(
                        selected.getResultingSeverityName(),
                        selected.getResultingSeverityValue(),
                        selected.getId(),
                        selected.isHumanInterventionRequired(),
                        selected.getSummary(),
                        selected.getMitigationInstructions());

        String previousName = remote.getCurrentSeverityName();
        Integer previousValue = remote.getCurrentSeverityValue();
        String previousRuleId = remote.getCurrentSeverityRuleId();
        boolean previousIntervention = remote.isHumanInterventionRequired();

        boolean definitionChanged = !Objects.equals(remote.getEvaluatedHealthProfileId(), profile.getId())
                || !Objects.equals(remote.getHealthEvaluationVersion(), profile.getEvaluationVersion());
        if (definitionChanged) {
            clearPendingTransition(remote);
        }

        HealthOutcome current = hasCurrentProjection(remote)
                ? new HealthOutcome(
                        remote.getCurrentSeverityName(),
                        remote.getCurrentSeverityValue(),
                        remote.getCurrentSeverityRuleId(),
                        remote.isHumanInterventionRequired(),
                        remote.getHealthSummary(),
                        remote.getMitigationInstructions())
                : defaultOutcome;
        RemoteHealthRule currentRule = findRule(profile.getRules(), current.ruleId());
        TransitionDecision transition = decideTransition(
                remote,
                current,
                candidate,
                currentRule,
                selected,
                now);
        HealthOutcome applied = transition.applyCandidate() ? candidate : current;

        boolean changed = !Objects.equals(previousName, applied.severityName())
                || !Objects.equals(previousValue, applied.severityValue())
                || !Objects.equals(previousRuleId, applied.ruleId())
                || previousIntervention != applied.interventionRequired();

        remote.setCurrentSeverityName(applied.severityName())
                .setCurrentSeverityValue(applied.severityValue())
                .setCurrentSeverityRuleId(applied.ruleId())
                .setHumanInterventionRequired(applied.interventionRequired())
                .setHealthSummary(applied.summary())
                .setMitigationStatus(applied.interventionRequired() ? "REQUIRED" : "NOT_REQUIRED")
                .setMitigationInstructions(applied.mitigationInstructions())
                .setEvaluatedHealthProfileId(profile.getId())
                .setHealthEvaluationVersion(profile.getEvaluationVersion())
                .setHealthCalculatedAt(now)
                .setNextHealthEvaluationAt(earliest(transition.nextEvaluationAt(), resolved.nextTimeDrivenEvaluationAt()));
        if (changed) {
            remote.setSeveritySince(now);
        }
        em.merge(remote);

        if (changed) {
            eventPublisher.publishEvent(new RemoteHealthChangedEvent(
                    remote,
                    previousName,
                    previousValue,
                    applied.severityName(),
                    applied.severityValue(),
                    applied.ruleId(),
                    applied.interventionRequired(),
                    applied.summary(),
                    applied.mitigationInstructions(),
                    now));
            if (remote instanceof Device device) {
                eventPublisher.publishEvent(new SeverityChangedEvent(
                        device,
                        previousName,
                        previousValue,
                        applied.severityName(),
                        applied.severityValue(),
                        applied.ruleId(),
                        applied.interventionRequired(),
                        applied.mitigationInstructions(),
                        null,
                        now));
            }
        }
        RemoteHealthRule appliedRule = findRule(profile.getRules(), applied.ruleId());
        return snapshot(remote, profile, appliedRule, resolved.values(), now);
    }

    private TransitionDecision decideTransition(Remote remote,
                                                HealthOutcome current,
                                                HealthOutcome candidate,
                                                RemoteHealthRule currentRule,
                                                RemoteHealthRule candidateRule,
                                                OffsetDateTime now) {
        if (sameOutcome(current, candidate)) {
            clearPendingTransition(remote);
            return new TransitionDecision(true, null);
        }
        long stableMillis = requiredStableMillis(current, candidate, currentRule, candidateRule);
        if (stableMillis <= 0) {
            clearPendingTransition(remote);
            return new TransitionDecision(true, null);
        }
        if (!pendingMatches(remote, candidate)) {
            remote.setHealthTransitionPending(true)
                    .setPendingSeverityName(candidate.severityName())
                    .setPendingSeverityValue(candidate.severityValue())
                    .setPendingSeverityRuleId(candidate.ruleId())
                    .setPendingHumanInterventionRequired(candidate.interventionRequired())
                    .setHealthTransitionPendingSince(now);
        }
        OffsetDateTime pendingSince = remote.getHealthTransitionPendingSince() == null
                ? now
                : remote.getHealthTransitionPendingSince();
        OffsetDateTime deadline = pendingSince.plus(Duration.ofMillis(stableMillis));
        if (!now.isBefore(deadline)) {
            clearPendingTransition(remote);
            return new TransitionDecision(true, null);
        }
        return new TransitionDecision(false, deadline);
    }

    private long requiredStableMillis(HealthOutcome current,
                                      HealthOutcome candidate,
                                      RemoteHealthRule currentRule,
                                      RemoteHealthRule candidateRule) {
        int currentValue = current.severityValue() == null ? Integer.MIN_VALUE : current.severityValue();
        int candidateValue = candidate.severityValue() == null ? Integer.MIN_VALUE : candidate.severityValue();
        boolean recovery = candidateValue < currentValue
                || current.interventionRequired() && !candidate.interventionRequired();
        if (recovery) {
            return currentRule == null || currentRule.getRecoveryStableMillis() == null
                    ? 0L
                    : Math.max(0L, currentRule.getRecoveryStableMillis());
        }
        return candidateRule == null || candidateRule.getMinimumStableMillis() == null
                ? 0L
                : Math.max(0L, candidateRule.getMinimumStableMillis());
    }

    private boolean pendingMatches(Remote remote, HealthOutcome candidate) {
        return remote.isHealthTransitionPending()
                && Objects.equals(remote.getPendingSeverityName(), candidate.severityName())
                && Objects.equals(remote.getPendingSeverityValue(), candidate.severityValue())
                && Objects.equals(remote.getPendingSeverityRuleId(), candidate.ruleId())
                && remote.isPendingHumanInterventionRequired() == candidate.interventionRequired();
    }

    private boolean sameOutcome(HealthOutcome one, HealthOutcome two) {
        return Objects.equals(one.severityName(), two.severityName())
                && Objects.equals(one.severityValue(), two.severityValue())
                && Objects.equals(one.ruleId(), two.ruleId())
                && one.interventionRequired() == two.interventionRequired();
    }

    private boolean hasCurrentProjection(Remote remote) {
        return remote.getCurrentSeverityName() != null
                || remote.getCurrentSeverityValue() != null
                || remote.getCurrentSeverityRuleId() != null
                || remote.isHumanInterventionRequired();
    }

    private void clearPendingTransition(Remote remote) {
        remote.setHealthTransitionPending(false)
                .setPendingSeverityName(null)
                .setPendingSeverityValue(null)
                .setPendingSeverityRuleId(null)
                .setPendingHumanInterventionRequired(false)
                .setHealthTransitionPendingSince(null);
    }

    private RemoteHealthRule findRule(List<RemoteHealthRule> rules, String ruleId) {
        if (ruleId == null || rules == null) {
            return null;
        }
        return rules.stream()
                .filter(rule -> Objects.equals(rule.getId(), ruleId))
                .findFirst()
                .orElse(null);
    }

    private OffsetDateTime earliest(OffsetDateTime one, OffsetDateTime two) {
        if (one == null) {
            return two;
        }
        if (two == null) {
            return one;
        }
        return one.isBefore(two) ? one : two;
    }

    private RemoteHealthSnapshot clearHealthProjection(Remote remote, RemoteHealthProfile profile, OffsetDateTime now) {
        String previousName = remote.getCurrentSeverityName();
        Integer previousValue = remote.getCurrentSeverityValue();
        String previousRuleId = remote.getCurrentSeverityRuleId();
        boolean previousIntervention = remote.isHumanInterventionRequired();
        boolean changed = previousName != null
                || previousValue != null
                || previousRuleId != null
                || previousIntervention
                || remote.getHealthSummary() != null
                || remote.getMitigationInstructions() != null;

        clearPendingTransition(remote);
        remote.setCurrentSeverityName(null)
                .setCurrentSeverityValue(null)
                .setCurrentSeverityRuleId(null)
                .setHumanInterventionRequired(false)
                .setHealthSummary(null)
                .setMitigationStatus("NOT_CONFIGURED")
                .setMitigationInstructions(null)
                .setEvaluatedHealthProfileId(profile == null ? null : profile.getId())
                .setHealthEvaluationVersion(profile == null ? null : profile.getEvaluationVersion())
                .setHealthCalculatedAt(now)
                .setNextHealthEvaluationAt(null);
        if (changed) {
            remote.setSeveritySince(now);
        }
        em.merge(remote);

        if (changed) {
            eventPublisher.publishEvent(new RemoteHealthChangedEvent(
                    remote,
                    previousName,
                    previousValue,
                    null,
                    null,
                    null,
                    false,
                    null,
                    null,
                    now));
        }
        return snapshot(remote, profile, null, Map.of(), now);
    }

    private ResolvedSignals resolveSignals(Remote remote,
                                           List<HealthSignalMapping> mappings,
                                           List<RemoteHealthRule> rules,
                                           OffsetDateTime now) {
        Map<String, Object> values = new LinkedHashMap<>();
        Map<String, AgeSignal> ageSignals = new HashMap<>();
        List<HealthSignalMapping> sorted = new ArrayList<>(mappings == null ? List.of() : mappings);
        sorted.sort(Comparator.comparingInt(HealthSignalMapping::getPriority).reversed());
        for (HealthSignalMapping mapping : sorted) {
            if (mapping.isSoftDelete() || mapping.getHealthSignal() == null || values.containsKey(mapping.getHealthSignal().getId())) {
                continue;
            }
            if (!mappingApplies(remote, mapping)) {
                continue;
            }
            Object raw = resolveRawValue(remote, mapping, now);
            Object transformed = transform(raw, mapping, now);
            if (transformed != null) {
                String signalId = mapping.getHealthSignal().getId();
                values.put(signalId, transformed);
                OffsetDateTime ageOrigin = ageOrigin(remote, mapping, raw);
                if (ageOrigin != null) {
                    ageSignals.put(signalId, new AgeSignal(ageOrigin, mapping));
                }
            }
        }
        return new ResolvedSignals(values, nextTimeDrivenEvaluation(ageSignals, rules, now));
    }

    private OffsetDateTime ageOrigin(Remote remote, HealthSignalMapping mapping, Object raw) {
        if (mapping.getSourceType() == HealthSignalSourceType.REMOTE_LAST_SEEN_AGE_SECONDS) {
            return remote.getLastSeen();
        }
        HealthSignalMappingOperation operation = mapping.getOperation() == null
                ? HealthSignalMappingOperation.DIRECT
                : mapping.getOperation();
        return operation == HealthSignalMappingOperation.TIMESTAMP_AGE_SECONDS
                ? asOffsetDateTime(raw)
                : null;
    }

    private OffsetDateTime nextTimeDrivenEvaluation(Map<String, AgeSignal> ageSignals,
                                                    List<RemoteHealthRule> rules,
                                                    OffsetDateTime now) {
        OffsetDateTime next = null;
        if (rules == null || ageSignals.isEmpty()) {
            return null;
        }
        for (RemoteHealthRule rule : rules) {
            if (rule == null || rule.isSoftDelete() || !rule.isEnabled() || rule.getConditions() == null) {
                continue;
            }
            for (RemoteHealthRuleCondition condition : rule.getConditions()) {
                if (condition == null || condition.isSoftDelete() || condition.getHealthSignal() == null) {
                    continue;
                }
                AgeSignal ageSignal = ageSignals.get(condition.getHealthSignal().getId());
                if (ageSignal == null) {
                    continue;
                }
                for (long boundarySeconds : conditionBoundaries(condition, ageSignal.mapping())) {
                    OffsetDateTime candidate = ageSignal.origin().plusSeconds(boundarySeconds);
                    if (candidate.isAfter(now) && (next == null || candidate.isBefore(next))) {
                        next = candidate;
                    }
                }
            }
        }
        return next;
    }

    private List<Long> conditionBoundaries(RemoteHealthRuleCondition condition, HealthSignalMapping mapping) {
        Double first = toRawAgeThreshold(condition.getNumericValue(), mapping);
        if (first == null || condition.getOperator() == null) {
            return List.of();
        }
        java.util.LinkedHashSet<Long> boundaries = new java.util.LinkedHashSet<>();
        addThresholdBoundaries(boundaries, first);
        if (condition.getOperator() == HealthComparisonOperator.BETWEEN) {
            addThresholdBoundaries(boundaries, toRawAgeThreshold(condition.getSecondNumericValue(), mapping));
        }
        return List.copyOf(boundaries);
    }

    private void addThresholdBoundaries(Set<Long> boundaries, Double threshold) {
        if (threshold == null || !Double.isFinite(threshold)) {
            return;
        }
        boundaries.add(Math.max(0L, (long) Math.ceil(threshold)));
        boundaries.add(Math.max(0L, (long) Math.floor(threshold) + 1L));
    }

    private Double toRawAgeThreshold(Double threshold, HealthSignalMapping mapping) {
        if (threshold == null) {
            return null;
        }
        HealthSignalMappingOperation operation = mapping.getOperation() == null
                ? HealthSignalMappingOperation.DIRECT
                : mapping.getOperation();
        return switch (operation) {
            case NUMERIC_SCALE -> {
                double multiplier = mapping.getMultiplier() == null ? 1D : mapping.getMultiplier();
                double offset = mapping.getOffset() == null ? 0D : mapping.getOffset();
                yield multiplier == 0D ? null : (threshold - offset) / multiplier;
            }
            case NUMERIC_OFFSET -> threshold - (mapping.getOffset() == null ? 0D : mapping.getOffset());
            case DIRECT, TIMESTAMP_AGE_SECONDS -> threshold;
            case ENUM_EQUALS, BOOLEAN_NEGATE -> null;
        };
    }

    private boolean mappingApplies(Remote remote, HealthSignalMapping mapping) {
        if (mapping.getSourceType() != HealthSignalSourceType.STATE_PROPERTY) {
            return true;
        }
        StatePropertyDefinition property = mapping.getStateProperty();
        return property != null
                && property.getStateSchema() != null
                && remote.getCurrentSchema() != null
                && Objects.equals(property.getStateSchema().getId(), remote.getCurrentSchema().getId());
    }

    private Object resolveRawValue(Remote remote, HealthSignalMapping mapping, OffsetDateTime now) {
        return switch (mapping.getSourceType()) {
            case STATE_PROPERTY -> resolvePath(remote.getDeviceProperties(), mapping.getStateProperty().getPropertyPath());
            case REMOTE_CONNECTIVITY -> remote.getLastConnectivityChange() == null
                    ? null
                    : remote.getLastConnectivityChange().getConnectivity().name();
            case REMOTE_LAST_SEEN_AGE_SECONDS -> remote.getLastSeen() == null
                    ? null
                    : Math.max(0L, Duration.between(remote.getLastSeen(), now).getSeconds());
            case REMOTE_VERSION -> remote.getVersion();
            case REMOTE_CURRENT_SEVERITY -> remote.getCurrentSeverityValue();
            case REMOTE_HUMAN_INTERVENTION -> remote.isHumanInterventionRequired();
        };
    }

    private Object transform(Object raw, HealthSignalMapping mapping, OffsetDateTime now) {
        if (raw == null) {
            return null;
        }
        HealthSignalMappingOperation operation = mapping.getOperation() == null
                ? HealthSignalMappingOperation.DIRECT
                : mapping.getOperation();
        return switch (operation) {
            case DIRECT -> raw;
            case NUMERIC_SCALE -> {
                Double value = asDouble(raw);
                yield value == null ? null : value * (mapping.getMultiplier() == null ? 1D : mapping.getMultiplier())
                        + (mapping.getOffset() == null ? 0D : mapping.getOffset());
            }
            case NUMERIC_OFFSET -> {
                Double value = asDouble(raw);
                yield value == null ? null : value + (mapping.getOffset() == null ? 0D : mapping.getOffset());
            }
            case ENUM_EQUALS -> Objects.equals(String.valueOf(raw), mapping.getExpectedStringValue());
            case BOOLEAN_NEGATE -> {
                Boolean value = asBoolean(raw);
                yield value == null ? null : !value;
            }
            case TIMESTAMP_AGE_SECONDS -> {
                OffsetDateTime timestamp = asOffsetDateTime(raw);
                yield timestamp == null ? null : Math.max(0L, Duration.between(timestamp, now).getSeconds());
            }
        };
    }

    private RemoteHealthRule selectRule(List<RemoteHealthRule> rules, Map<String, Object> signals) {
        if (rules == null) {
            return null;
        }
        return rules.stream()
                .filter(rule -> !rule.isSoftDelete() && rule.isEnabled())
                .sorted(Comparator.comparingInt(RemoteHealthRule::getPriority).reversed()
                        .thenComparing(rule -> rule.getResultingSeverityValue() == null ? Integer.MIN_VALUE : rule.getResultingSeverityValue(), Comparator.reverseOrder()))
                .filter(rule -> ruleMatches(rule, signals))
                .findFirst()
                .orElse(null);
    }

    private boolean ruleMatches(RemoteHealthRule rule, Map<String, Object> signals) {
        List<RemoteHealthRuleCondition> conditions = rule.getConditions();
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }
        boolean any = rule.getConditionJoinType() == ConditionJoinType.ANY;
        for (RemoteHealthRuleCondition condition : conditions) {
            boolean matched = conditionMatches(condition, signals);
            if (any && matched) {
                return true;
            }
            if (!any && !matched) {
                return false;
            }
        }
        return !any;
    }

    private boolean conditionMatches(RemoteHealthRuleCondition condition, Map<String, Object> signals) {
        HealthSignalDefinition signal = condition.getHealthSignal();
        if (signal == null) {
            return false;
        }
        Object actual = signals.get(signal.getId());
        if (actual == null) {
            MissingSignalBehavior missing = condition.getMissingSignalBehavior() == null
                    ? MissingSignalBehavior.NO_MATCH
                    : condition.getMissingSignalBehavior();
            if (missing == MissingSignalBehavior.MATCH) {
                return true;
            }
            if (missing == MissingSignalBehavior.NO_MATCH) {
                return false;
            }
            actual = defaultValue(condition, signal.getValueType());
            if (actual == null) {
                return false;
            }
        }
        HealthComparisonOperator operator = condition.getOperator();
        if (operator == null) {
            return false;
        }
        return switch (signal.getValueType()) {
            case NUMBER, TIMESTAMP -> compareNumbers(asDouble(actual), condition.getNumericValue(), condition.getSecondNumericValue(), operator);
            case BOOLEAN -> compareObjects(asBoolean(actual), condition.getBooleanValue(), operator);
            case STRING, ENUM -> compareObjects(String.valueOf(actual), condition.getStringValue(), operator);
        };
    }

    private Object defaultValue(RemoteHealthRuleCondition condition, HealthSignalValueType type) {
        return switch (type) {
            case NUMBER, TIMESTAMP -> condition.getDefaultNumericValue();
            case BOOLEAN -> condition.getDefaultBooleanValue();
            case STRING, ENUM -> condition.getDefaultStringValue();
        };
    }

    private boolean compareNumbers(Double actual, Double expected, Double secondExpected, HealthComparisonOperator operator) {
        if (actual == null || expected == null) {
            return false;
        }
        return switch (operator) {
            case EQ -> Double.compare(actual, expected) == 0;
            case NE -> Double.compare(actual, expected) != 0;
            case GT -> actual > expected;
            case GE -> actual >= expected;
            case LT -> actual < expected;
            case LE -> actual <= expected;
            case BETWEEN -> secondExpected != null
                    && actual >= Math.min(expected, secondExpected)
                    && actual <= Math.max(expected, secondExpected);
        };
    }

    private boolean compareObjects(Object actual, Object expected, HealthComparisonOperator operator) {
        if (operator == HealthComparisonOperator.EQ) {
            return Objects.equals(actual, expected);
        }
        if (operator == HealthComparisonOperator.NE) {
            return !Objects.equals(actual, expected);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Object resolvePath(Map<String, Object> root, String path) {
        if (root == null || path == null || path.isBlank()) {
            return null;
        }
        Object current = root;
        for (String segment : path.split("\\.")) {
            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }
            current = ((Map<String, Object>) map).get(segment);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private Double asDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return value == null ? null : Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Boolean asBoolean(Object value) {
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).toLowerCase(Locale.ROOT);
        if ("true".equals(text) || "1".equals(text) || "yes".equals(text) || Connectivity.ON.name().toLowerCase(Locale.ROOT).equals(text)) {
            return true;
        }
        if ("false".equals(text) || "0".equals(text) || "no".equals(text) || Connectivity.OFF.name().toLowerCase(Locale.ROOT).equals(text)) {
            return false;
        }
        return null;
    }

    private OffsetDateTime asOffsetDateTime(Object value) {
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime;
        }
        if (value instanceof Instant instant) {
            return instant.atOffset(ZoneOffset.UTC);
        }
        if (value instanceof Number number) {
            long epoch = number.longValue();
            if (Math.abs(epoch) < 100_000_000_000L) {
                return Instant.ofEpochSecond(epoch).atOffset(ZoneOffset.UTC);
            }
            return Instant.ofEpochMilli(epoch).atOffset(ZoneOffset.UTC);
        }
        if (value != null) {
            try {
                return OffsetDateTime.parse(String.valueOf(value));
            } catch (DateTimeParseException ignored) {
                try {
                    return Instant.parse(String.valueOf(value)).atOffset(ZoneOffset.UTC);
                } catch (DateTimeParseException ignoredAgain) {
                    return null;
                }
            }
        }
        return null;
    }

    private RemoteHealthSnapshot snapshot(Remote remote, RemoteHealthProfile profile, RemoteHealthRule selected, Map<String, Object> signals, OffsetDateTime now) {
        Map<String, Object> byExternalId = new LinkedHashMap<>();
        if (profile != null && profile.getMappings() != null) {
            for (HealthSignalMapping mapping : profile.getMappings()) {
                if (mapping.getHealthSignal() == null) {
                    continue;
                }
                Object value = signals.get(mapping.getHealthSignal().getId());
                if (value != null) {
                    byExternalId.put(mapping.getHealthSignal().getExternalId(), value);
                }
            }
        }
        return new RemoteHealthSnapshot()
                .setRemoteId(remote.getId())
                .setRemoteHealthProfileId(profile == null ? null : profile.getId())
                .setRemoteHealthProfileName(profile == null ? null : profile.getName())
                .setSeverityName(remote.getCurrentSeverityName())
                .setSeverityValue(remote.getCurrentSeverityValue())
                .setMatchedRuleId(selected == null ? null : selected.getId())
                .setMatchedRuleName(selected == null ? null : selected.getName())
                .setHumanInterventionRequired(remote.isHumanInterventionRequired())
                .setSummary(remote.getHealthSummary())
                .setMitigationInstructions(remote.getMitigationInstructions())
                .setCalculatedAt(now)
                .setSignals(byExternalId);
    }
    private record HealthOutcome(
            String severityName,
            Integer severityValue,
            String ruleId,
            boolean interventionRequired,
            String summary,
            String mitigationInstructions) {
    }

    private record TransitionDecision(boolean applyCandidate, OffsetDateTime nextEvaluationAt) {
    }


    private record AgeSignal(OffsetDateTime origin, HealthSignalMapping mapping) {
    }
    private record ResolvedSignals(Map<String, Object> values, OffsetDateTime nextTimeDrivenEvaluationAt) {
    }

}
