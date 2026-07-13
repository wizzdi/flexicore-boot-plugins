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
        List<Member> members = resolveMembers(group, securityContext, now);

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
        String severityName;
        Integer severityValue;
        String matchedRuleId;
        boolean humanInterventionRequired;

        boolean belowMinimum = policy.getMinimumPopulation() != null && population < policy.getMinimumPopulation();
        if (belowMinimum && policy.getUnknownPolicy() == FleetUnknownPolicy.RESULT_UNKNOWN) {
            severityName = "UNKNOWN";
            severityValue = null;
            matchedRuleId = null;
            humanInterventionRequired = false;
        } else if (selected != null) {
            severityName = selected.getResultingSeverityName();
            severityValue = selected.getResultingSeverityValue();
            matchedRuleId = selected.getId();
            humanInterventionRequired = selected.isHumanInterventionRequired();
        } else {
            severityName = policy.getDefaultSeverityName();
            severityValue = policy.getDefaultSeverityValue();
            matchedRuleId = null;
            humanInterventionRequired = false;
        }

        String previousSeverityName = group.getCurrentSeverityName();
        Integer previousSeverityValue = group.getCurrentSeverityValue();
        boolean changed = !Objects.equals(previousSeverityName, severityName)
                || !Objects.equals(previousSeverityValue, severityValue)
                || !Objects.equals(group.getCurrentSeverityRuleId(), matchedRuleId)
                || group.isHumanInterventionRequired() != humanInterventionRequired;

        group.setCurrentSeverityName(severityName);
        group.setCurrentSeverityValue(severityValue);
        group.setCurrentSeverityRuleId(matchedRuleId);
        group.setHumanInterventionRequired(humanInterventionRequired);
        group.setCurrentPopulationCount(population);
        group.setCurrentUnknownCount(unknown);
        group.setCurrentOfflineCount(offline);
        group.setCurrentHumanInterventionCount(intervention);
        group.setHealthCalculatedAt(now);
        repository.merge(group);

        if (changed) {
            eventPublisher.publishEvent(new RemoteGroupHealthChangedEvent(
                    group,
                    previousSeverityName,
                    previousSeverityValue,
                    severityName,
                    severityValue,
                    matchedRuleId,
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
                severityName,
                severityValue,
                matchedRuleId,
                humanInterventionRequired,
                metrics,
                now);
    }

    private List<Member> resolveMembers(RemoteGroup group, SecurityContext securityContext, OffsetDateTime now) {
        RemoteGroupToRemoteFilter filter = new RemoteGroupToRemoteFilter();
        filter.setRemoteGroupIds(Set.of(group.getId()));
        filter.setActiveAt(now);
        List<RemoteGroupToRemote> memberships = repository.listAllMemberships(securityContext, filter);
        Set<String> excludedRemoteIds = memberships.stream()
                .filter(membership -> membership.getMembershipAction() == RemoteGroupMembershipAction.EXCLUDE)
                .filter(membership -> membership.getRemote() != null)
                .map(membership -> membership.getRemote().getId())
                .collect(java.util.stream.Collectors.toSet());
        Map<String, Member> included = new LinkedHashMap<>();
        for (RemoteGroupToRemote membership : memberships) {
            Remote remote = membership.getRemote();
            if (remote == null || remote.isSoftDelete() || excludedRemoteIds.contains(remote.getId())) {
                continue;
            }
            if (membership.getMembershipAction() != RemoteGroupMembershipAction.EXCLUDE) {
                included.put(remote.getId(), new Member(remote, membership));
            }
        }
        return new ArrayList<>(included.values());
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
}
