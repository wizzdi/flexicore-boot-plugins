package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.ConditionJoinType;
import com.wizzdi.basic.iot.model.Connectivity;
import com.wizzdi.basic.iot.model.FleetDenominatorPolicy;
import com.wizzdi.basic.iot.model.FleetHealthMetricType;
import com.wizzdi.basic.iot.model.FleetHealthPolicy;
import com.wizzdi.basic.iot.model.FleetHealthRule;
import com.wizzdi.basic.iot.model.FleetHealthRuleCondition;
import com.wizzdi.basic.iot.model.FleetUnknownPolicy;
import com.wizzdi.basic.iot.model.HealthComparisonOperator;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipAction;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote;
import com.wizzdi.basic.iot.service.data.RemoteGroupRepository;
import com.wizzdi.basic.iot.service.events.RemoteGroupHealthChangedEvent;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteFilter;
import com.wizzdi.basic.iot.service.response.RemoteGroupHealthSnapshot;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Extension
@Component
public class RemoteGroupFleetHealthService implements Plugin {

    @Autowired
    private RemoteGroupRepository repository;
    @Autowired
    private FleetHealthPolicyService fleetHealthPolicyService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public RemoteGroupHealthSnapshot evaluate(String remoteGroupId, SecurityContext securityContext) {
        RemoteGroup group = repository.getByIdOrNull(remoteGroupId, RemoteGroup.class, securityContext);
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible RemoteGroup with id " + remoteGroupId);
        }
        if (!group.isHealthEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fleet health is disabled for RemoteGroup " + remoteGroupId);
        }
        FleetHealthPolicy policy = group.getFleetHealthPolicy();
        if (policy == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RemoteGroup has no FleetHealthPolicy");
        }
        fleetHealthPolicyService.populate(policy);
        OffsetDateTime now = OffsetDateTime.now();
        MemberResolution resolution = resolveMembers(group, securityContext, now);
        List<Member> members = resolution.members();

        int population = members.size();
        int unknown = (int) members.stream().filter(member -> member.remote().getCurrentSeverityValue() == null).count();
        int offline = (int) members.stream().filter(this::isOffline).count();
        int intervention = (int) members.stream().filter(member -> member.remote().isHumanInterventionRequired()).count();

        Map<String, Double> metrics = new LinkedHashMap<>();
        metrics.put("population", (double) population);
        metrics.put("unknownSeverity", (double) unknown);
        metrics.put("offline", (double) offline);
        metrics.put("humanIntervention", (double) intervention);
        metrics.put("weightedSeverityAverage", weightedSeverityAverage(members));

        FleetHealthRule selected = selectRule(policy, members, now, metrics);
        boolean belowMinimum = policy.getMinimumPopulation() != null && population < policy.getMinimumPopulation();
        GroupHealthOutcome defaultOutcome = new GroupHealthOutcome(
                policy.getDefaultSeverityName(),
                policy.getDefaultSeverityValue(),
                null,
                false);
        GroupHealthOutcome candidate;
        if (belowMinimum && policy.getUnknownPolicy() == FleetUnknownPolicy.RESULT_UNKNOWN) {
            candidate = new GroupHealthOutcome("UNKNOWN", null, null, false);
        } else if (selected != null) {
            candidate = new GroupHealthOutcome(
                    selected.getResultingSeverityName(),
                    selected.getResultingSeverityValue(),
                    selected.getId(),
                    selected.isHumanInterventionRequired());
        } else {
            candidate = defaultOutcome;
        }

        String previousSeverityName = group.getCurrentSeverityName();
        Integer previousSeverityValue = group.getCurrentSeverityValue();
        String previousRuleId = group.getCurrentSeverityRuleId();
        boolean previousIntervention = group.isHumanInterventionRequired();

        boolean definitionChanged = !Objects.equals(group.getEvaluatedFleetHealthPolicyId(), policy.getId())
                || !Objects.equals(group.getFleetHealthEvaluationVersion(), policy.getEvaluationVersion())
                || !Objects.equals(group.getEvaluatedHealthInputVersion(), group.getHealthInputVersion());
        if (definitionChanged) {
            clearPendingTransition(group);
        }

        GroupHealthOutcome current = hasCurrentProjection(group)
                ? new GroupHealthOutcome(
                        group.getCurrentSeverityName(),
                        group.getCurrentSeverityValue(),
                        group.getCurrentSeverityRuleId(),
                        group.isHumanInterventionRequired())
                : selected == null ? candidate : defaultOutcome;
        FleetHealthRule currentRule = findRule(policy.getRules(), current.ruleId());
        GroupTransitionDecision transition = decideTransition(
                group,
                current,
                candidate,
                currentRule,
                selected,
                now);
        GroupHealthOutcome applied = transition.applyCandidate() ? candidate : current;

        boolean changed = !Objects.equals(previousSeverityName, applied.severityName())
                || !Objects.equals(previousSeverityValue, applied.severityValue())
                || !Objects.equals(previousRuleId, applied.ruleId())
                || previousIntervention != applied.interventionRequired();

        OffsetDateTime nextStaleEvaluation = nextStaleEvaluation(policy, members, now);
        group.setCurrentSeverityName(applied.severityName());
        group.setCurrentSeverityValue(applied.severityValue());
        group.setCurrentSeverityRuleId(applied.ruleId());
        group.setHumanInterventionRequired(applied.interventionRequired());
        group.setCurrentPopulationCount(population);
        group.setCurrentUnknownCount(unknown);
        group.setCurrentOfflineCount(offline);
        group.setCurrentHumanInterventionCount(intervention);
        group.setEvaluatedFleetHealthPolicyId(policy.getId());
        group.setFleetHealthEvaluationVersion(policy.getEvaluationVersion());
        group.setEvaluatedHealthInputVersion(group.getHealthInputVersion());
        group.setHealthCalculatedAt(now);
        group.setNextHealthEvaluationAt(earliest(
                transition.nextEvaluationAt(),
                earliest(nextStaleEvaluation, resolution.nextMembershipChangeAt())));
        repository.merge(group);

        if (changed) {
            eventPublisher.publishEvent(new RemoteGroupHealthChangedEvent(
                    group,
                    previousSeverityName,
                    previousSeverityValue,
                    applied.severityName(),
                    applied.severityValue(),
                    applied.ruleId(),
                    population,
                    unknown,
                    offline,
                    intervention,
                    now));
        }

        return new RemoteGroupHealthSnapshot(
                group.getId(),
                population,
                unknown,
                offline,
                intervention,
                applied.severityName(),
                applied.severityValue(),
                applied.ruleId(),
                applied.interventionRequired(),
                metrics,
                now);
    }

    @Transactional
    public RemoteGroupHealthSnapshot evaluateAutomatic(String remoteGroupId) {
        RemoteGroup group = repository.getByIdOrNull(remoteGroupId, RemoteGroup.class, null);
        if (group == null || group.isSoftDelete()) {
            return null;
        }
        if (!group.isHealthEnabled() || group.getFleetHealthPolicy() == null) {
            return clearHealthProjection(group, OffsetDateTime.now());
        }
        return evaluate(remoteGroupId, null);
    }

    private RemoteGroupHealthSnapshot clearHealthProjection(RemoteGroup group, OffsetDateTime now) {
        String previousSeverityName = group.getCurrentSeverityName();
        Integer previousSeverityValue = group.getCurrentSeverityValue();
        boolean changed = previousSeverityName != null
                || previousSeverityValue != null
                || group.getCurrentSeverityRuleId() != null
                || group.isHumanInterventionRequired()
                || group.getCurrentPopulationCount() != null
                || group.getEvaluatedFleetHealthPolicyId() != null;

        clearPendingTransition(group);
        group.setCurrentSeverityName(null);
        group.setCurrentSeverityValue(null);
        group.setCurrentSeverityRuleId(null);
        group.setHumanInterventionRequired(false);
        group.setCurrentPopulationCount(null);
        group.setCurrentUnknownCount(null);
        group.setCurrentOfflineCount(null);
        group.setCurrentHumanInterventionCount(null);
        group.setEvaluatedFleetHealthPolicyId(null);
        group.setFleetHealthEvaluationVersion(null);
        group.setEvaluatedHealthInputVersion(group.getHealthInputVersion());
        group.setHealthCalculatedAt(now);
        group.setNextHealthEvaluationAt(null);
        repository.merge(group);

        if (changed) {
            eventPublisher.publishEvent(new RemoteGroupHealthChangedEvent(
                    group,
                    previousSeverityName,
                    previousSeverityValue,
                    null,
                    null,
                    null,
                    0,
                    0,
                    0,
                    0,
                    now));
        }
        return new RemoteGroupHealthSnapshot(
                group.getId(),
                0,
                0,
                0,
                0,
                null,
                null,
                null,
                false,
                Map.of(),
                now);
    }

    private GroupTransitionDecision decideTransition(RemoteGroup group,
                                                     GroupHealthOutcome current,
                                                     GroupHealthOutcome candidate,
                                                     FleetHealthRule currentRule,
                                                     FleetHealthRule candidateRule,
                                                     OffsetDateTime now) {
        if (sameOutcome(current, candidate)) {
            clearPendingTransition(group);
            return new GroupTransitionDecision(true, null);
        }
        long stableMillis = requiredStableMillis(current, candidate, currentRule, candidateRule);
        if (stableMillis <= 0) {
            clearPendingTransition(group);
            return new GroupTransitionDecision(true, null);
        }
        if (!pendingMatches(group, candidate)) {
            group.setHealthTransitionPending(true)
                    .setPendingSeverityName(candidate.severityName())
                    .setPendingSeverityValue(candidate.severityValue())
                    .setPendingSeverityRuleId(candidate.ruleId())
                    .setPendingHumanInterventionRequired(candidate.interventionRequired())
                    .setHealthTransitionPendingSince(now);
        }
        OffsetDateTime pendingSince = group.getHealthTransitionPendingSince() == null
                ? now
                : group.getHealthTransitionPendingSince();
        OffsetDateTime deadline = pendingSince.plus(Duration.ofMillis(stableMillis));
        if (!now.isBefore(deadline)) {
            clearPendingTransition(group);
            return new GroupTransitionDecision(true, null);
        }
        return new GroupTransitionDecision(false, deadline);
    }

    private long requiredStableMillis(GroupHealthOutcome current,
                                      GroupHealthOutcome candidate,
                                      FleetHealthRule currentRule,
                                      FleetHealthRule candidateRule) {
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

    private boolean pendingMatches(RemoteGroup group, GroupHealthOutcome candidate) {
        return group.isHealthTransitionPending()
                && Objects.equals(group.getPendingSeverityName(), candidate.severityName())
                && Objects.equals(group.getPendingSeverityValue(), candidate.severityValue())
                && Objects.equals(group.getPendingSeverityRuleId(), candidate.ruleId())
                && group.isPendingHumanInterventionRequired() == candidate.interventionRequired();
    }

    private boolean sameOutcome(GroupHealthOutcome one, GroupHealthOutcome two) {
        return Objects.equals(one.severityName(), two.severityName())
                && Objects.equals(one.severityValue(), two.severityValue())
                && Objects.equals(one.ruleId(), two.ruleId())
                && one.interventionRequired() == two.interventionRequired();
    }

    private boolean hasCurrentProjection(RemoteGroup group) {
        return group.getCurrentSeverityName() != null
                || group.getCurrentSeverityValue() != null
                || group.getCurrentSeverityRuleId() != null
                || group.isHumanInterventionRequired();
    }

    private void clearPendingTransition(RemoteGroup group) {
        group.setHealthTransitionPending(false)
                .setPendingSeverityName(null)
                .setPendingSeverityValue(null)
                .setPendingSeverityRuleId(null)
                .setPendingHumanInterventionRequired(false)
                .setHealthTransitionPendingSince(null);
    }

    private FleetHealthRule findRule(List<FleetHealthRule> rules, String ruleId) {
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

    private MemberResolution resolveMembers(RemoteGroup group, SecurityContext securityContext, OffsetDateTime now) {
        RemoteGroupToRemoteFilter filter = new RemoteGroupToRemoteFilter();
        filter.setRemoteGroupIds(Set.of(group.getId()));
        List<RemoteGroupToRemote> memberships = repository.listAllMemberships(securityContext, filter);
        OffsetDateTime nextMembershipChange = null;
        Set<String> excludedRemoteIds = new java.util.HashSet<>();
        for (RemoteGroupToRemote membership : memberships) {
            nextMembershipChange = earliest(nextMembershipChange, nextMembershipChange(membership, now));
            if (isActive(membership, now)
                    && membership.getMembershipAction() == RemoteGroupMembershipAction.EXCLUDE
                    && membership.getRemote() != null) {
                excludedRemoteIds.add(membership.getRemote().getId());
            }
        }
        Map<String, Member> included = new LinkedHashMap<>();
        for (RemoteGroupToRemote membership : memberships) {
            Remote remote = membership.getRemote();
            if (!isActive(membership, now)
                    || remote == null
                    || remote.isSoftDelete()
                    || excludedRemoteIds.contains(remote.getId())) {
                continue;
            }
            if (membership.getMembershipAction() != RemoteGroupMembershipAction.EXCLUDE) {
                included.put(remote.getId(), new Member(remote, membership));
            }
        }
        return new MemberResolution(new ArrayList<>(included.values()), nextMembershipChange);
    }

    private boolean isActive(RemoteGroupToRemote membership, OffsetDateTime now) {
        return (membership.getActiveFrom() == null || !membership.getActiveFrom().isAfter(now))
                && (membership.getActiveUntil() == null || membership.getActiveUntil().isAfter(now));
    }

    private OffsetDateTime nextMembershipChange(RemoteGroupToRemote membership, OffsetDateTime now) {
        OffsetDateTime next = null;
        if (membership.getActiveFrom() != null && membership.getActiveFrom().isAfter(now)) {
            next = membership.getActiveFrom();
        }
        if (membership.getActiveUntil() != null && membership.getActiveUntil().isAfter(now)) {
            next = earliest(next, membership.getActiveUntil());
        }
        return next;
    }

    private OffsetDateTime nextStaleEvaluation(FleetHealthPolicy policy, List<Member> members, OffsetDateTime now) {
        OffsetDateTime next = null;
        if (policy.getRules() == null) {
            return null;
        }
        for (FleetHealthRule rule : policy.getRules()) {
            if (rule == null || rule.isSoftDelete() || !rule.isEnabled() || rule.getConditions() == null) {
                continue;
            }
            for (FleetHealthRuleCondition condition : rule.getConditions()) {
                if (condition == null || condition.isSoftDelete()
                        || condition.getStaleAfterSeconds() == null
                        || condition.getStaleAfterSeconds() < 0
                        || condition.getMetricType() != FleetHealthMetricType.STALE_COUNT
                        && condition.getMetricType() != FleetHealthMetricType.STALE_PERCENT) {
                    continue;
                }
                for (Member member : eligibleMembers(condition, members)) {
                    if (member.remote().getLastSeen() == null) {
                        continue;
                    }
                    OffsetDateTime candidate = member.remote().getLastSeen().plusSeconds(condition.getStaleAfterSeconds());
                    if (candidate.isAfter(now)) {
                        next = earliest(next, candidate);
                    }
                }
            }
        }
        return next;
    }

    private FleetHealthRule selectRule(FleetHealthPolicy policy, List<Member> members, OffsetDateTime now, Map<String, Double> metrics) {
        if (!policy.isEnabled()) {
            return null;
        }
        return policy.getRules().stream()
                .filter(rule -> !rule.isSoftDelete() && rule.isEnabled())
                .sorted(Comparator.comparingInt(FleetHealthRule::getPriority).reversed()
                        .thenComparing(rule -> Optional.ofNullable(rule.getResultingSeverityValue()).orElse(Integer.MIN_VALUE), Comparator.reverseOrder()))
                .filter(rule -> ruleMatches(rule, members, now, metrics))
                .findFirst()
                .orElse(null);
    }

    private boolean ruleMatches(FleetHealthRule rule, List<Member> members, OffsetDateTime now, Map<String, Double> metrics) {
        List<FleetHealthRuleCondition> conditions = rule.getConditions();
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }
        boolean any = rule.getConditionJoinType() == ConditionJoinType.ANY;
        for (FleetHealthRuleCondition condition : conditions) {
            double value = metricValue(condition, members, now);
            metrics.put("condition:" + condition.getId(), value);
            boolean matches = compare(value, condition.getOperator(), condition.getThreshold(), condition.getSecondThreshold());
            if (any && matches) {
                return true;
            }
            if (!any && !matches) {
                return false;
            }
        }
        return !any;
    }

    private double metricValue(FleetHealthRuleCondition condition, List<Member> allMembers, OffsetDateTime now) {
        List<Member> members = eligibleMembers(condition, allMembers);
        int denominator = denominator(condition, members);
        return switch (condition.getMetricType()) {
            case MEMBER_COUNT -> members.size();
            case OFFLINE_COUNT -> members.stream().filter(this::isOffline).count();
            case OFFLINE_PERCENT -> percent(members.stream().filter(this::isOffline).count(), denominator);
            case STALE_COUNT -> members.stream().filter(member -> isStale(member, condition.getStaleAfterSeconds(), now)).count();
            case STALE_PERCENT -> percent(members.stream().filter(member -> isStale(member, condition.getStaleAfterSeconds(), now)).count(), denominator);
            case SEVERITY_AT_OR_ABOVE_COUNT -> members.stream().filter(member -> severityAtOrAbove(member, condition.getSeverityThresholdValue())).count();
            case SEVERITY_AT_OR_ABOVE_PERCENT -> percent(members.stream().filter(member -> severityAtOrAbove(member, condition.getSeverityThresholdValue())).count(), denominator);
            case WEIGHTED_SEVERITY_AVERAGE -> weightedSeverityAverage(members);
            case REQUIRED_UNHEALTHY_COUNT -> members.stream().filter(member -> member.membership().isRequiredMember() && severityAtOrAbove(member, condition.getSeverityThresholdValue())).count();
            case HUMAN_INTERVENTION_COUNT -> members.stream().filter(member -> member.remote().isHumanInterventionRequired()).count();
            case HUMAN_INTERVENTION_PERCENT -> percent(members.stream().filter(member -> member.remote().isHumanInterventionRequired()).count(), denominator);
            case UNKNOWN_SEVERITY_COUNT -> members.stream().filter(member -> member.remote().getCurrentSeverityValue() == null).count();
            case UNKNOWN_SEVERITY_PERCENT -> percent(members.stream().filter(member -> member.remote().getCurrentSeverityValue() == null).count(), members.size());
        };
    }

    private List<Member> eligibleMembers(FleetHealthRuleCondition condition, List<Member> members) {
        return members.stream()
                .filter(member -> !condition.isRequiredMembersOnly() || member.membership().isRequiredMember())
                .filter(member -> condition.getMemberRole() == null
                        || member.membership().getRole() != null
                        && Objects.equals(condition.getMemberRole().getId(), member.membership().getRole().getId()))
                .toList();
    }

    private int denominator(FleetHealthRuleCondition condition, List<Member> members) {
        if (condition.getDenominatorPolicy() == FleetDenominatorPolicy.MEMBERS_WITH_KNOWN_SEVERITY) {
            return (int) members.stream().filter(member -> member.remote().getCurrentSeverityValue() != null).count();
        }
        return members.size();
    }

    private boolean isOffline(Member member) {
        return member.remote().getLastConnectivityChange() == null
                || member.remote().getLastConnectivityChange().getConnectivity() != Connectivity.ON;
    }

    private boolean isStale(Member member, Long staleAfterSeconds, OffsetDateTime now) {
        if (member.remote().getLastSeen() == null) {
            return true;
        }
        long threshold = staleAfterSeconds == null ? 0 : Math.max(0, staleAfterSeconds);
        return Duration.between(member.remote().getLastSeen(), now).getSeconds() >= threshold;
    }

    private boolean severityAtOrAbove(Member member, Integer threshold) {
        return member.remote().getCurrentSeverityValue() != null
                && threshold != null
                && member.remote().getCurrentSeverityValue() >= threshold;
    }

    private double weightedSeverityAverage(List<Member> members) {
        double weightedTotal = 0;
        double totalWeight = 0;
        for (Member member : members) {
            Integer severity = member.remote().getCurrentSeverityValue();
            if (severity == null) {
                continue;
            }
            double weight = member.membership().getWeight() == null ? 1D : member.membership().getWeight();
            weightedTotal += severity * weight;
            totalWeight += weight;
        }
        return totalWeight == 0 ? 0 : weightedTotal / totalWeight;
    }

    private double percent(long numerator, int denominator) {
        return denominator <= 0 ? 0 : numerator * 100D / denominator;
    }

    private boolean compare(double actual, HealthComparisonOperator operator, Double threshold, Double secondThreshold) {
        if (operator == null || threshold == null) {
            return false;
        }
        return switch (operator) {
            case EQ -> Double.compare(actual, threshold) == 0;
            case NE -> Double.compare(actual, threshold) != 0;
            case GT -> actual > threshold;
            case GE -> actual >= threshold;
            case LT -> actual < threshold;
            case LE -> actual <= threshold;
            case BETWEEN -> secondThreshold != null && actual >= Math.min(threshold, secondThreshold) && actual <= Math.max(threshold, secondThreshold);
        };
    }

    private record Member(Remote remote, RemoteGroupToRemote membership) {
    }

    private record MemberResolution(List<Member> members, OffsetDateTime nextMembershipChangeAt) {
    }

    private record GroupHealthOutcome(
            String severityName,
            Integer severityValue,
            String ruleId,
            boolean interventionRequired) {
    }

    private record GroupTransitionDecision(boolean applyCandidate, OffsetDateTime nextEvaluationAt) {
    }
}
