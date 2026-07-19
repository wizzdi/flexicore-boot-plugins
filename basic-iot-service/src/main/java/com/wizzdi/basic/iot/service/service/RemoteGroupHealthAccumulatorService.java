package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.Connectivity;
import com.wizzdi.basic.iot.model.FleetDenominatorPolicy;
import com.wizzdi.basic.iot.model.FleetHealthMetricType;
import com.wizzdi.basic.iot.model.FleetHealthRuleCondition;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroupHealthAccumulator;
import com.wizzdi.basic.iot.model.RemoteGroupMemberHealthState;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipAction;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipSource;
import com.wizzdi.basic.iot.model.RemoteGroupSeverityBucket;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote;
import com.wizzdi.basic.iot.service.data.RemoteGroupHealthAccumulatorRepository;
import com.wizzdi.basic.iot.service.data.RemoteGroupRepository;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Extension
@Component
public class RemoteGroupHealthAccumulatorService implements Plugin {

    @Autowired
    private RemoteGroupHealthAccumulatorRepository repository;
    @Autowired
    private RemoteGroupRepository remoteGroupRepository;
    @Autowired
    private DerivedEntitySecurityService derivedEntitySecurityService;

    private final ConcurrentHashMap<String, ReentrantLock> groupLocks = new ConcurrentHashMap<>();

    @Transactional
    public GroupAccumulatorSnapshot ensureCurrent(RemoteGroup group, OffsetDateTime now) {
        RemoteGroupHealthAccumulator all = repository.findAccumulator(group.getId(), null, false);
        boolean due = group.getNextHealthEvaluationAt() != null && !group.getNextHealthEvaluationAt().isAfter(now);
        if (all == null || all.getAccumulatorVersion() != group.getHealthInputVersion() || due) {
            return reconcile(group, now);
        }
        return snapshot(group, now);
    }

    @Transactional
    public GroupAccumulatorSnapshot reconcileById(String groupId, OffsetDateTime now) {
        RemoteGroup group = repository.findByIdOrNull(RemoteGroup.class, groupId);
        if (group == null || group.isSoftDelete() || !group.isHealthEnabled()) {
            return null;
        }
        return reconcile(group, now == null ? OffsetDateTime.now() : now);
    }

    @Transactional
    public GroupAccumulatorSnapshot reconcile(RemoteGroup group, OffsetDateTime now) {
        ReentrantLock lock = groupLocks.computeIfAbsent(group.getId(), ignored -> new ReentrantLock());
        lock.lock();
        try {
            RemoteGroupToRemoteFilter filter = new RemoteGroupToRemoteFilter()
                    .setRemoteGroupIds(Set.of(group.getId()));
            List<RemoteGroupToRemote> memberships = remoteGroupRepository.listAllMemberships(null, filter);
            Map<String, RemoteGroupToRemote> effectiveMemberships = resolveEffectiveMemberships(memberships, now);

            List<RemoteGroupMemberHealthState> existingStates = repository.listMemberStates(group.getId());
            Map<String, RemoteGroupMemberHealthState> existingByMembership = existingStates.stream()
                    .filter(state -> state.getRemoteGroupToRemote() != null)
                    .collect(Collectors.toMap(
                            state -> state.getRemoteGroupToRemote().getId(),
                            state -> state,
                            (one, two) -> one,
                            LinkedHashMap::new));

            List<RemoteGroupMemberHealthState> activeStates = new ArrayList<>();
            List<Object> toMerge = new ArrayList<>();
            Set<String> retainedStateIds = new HashSet<>();
            for (RemoteGroupToRemote membership : effectiveMemberships.values()) {
                RemoteGroupMemberHealthState state = existingByMembership.get(membership.getId());
                if (state == null) {
                    state = new RemoteGroupMemberHealthState();
                    state.setId(UUID.randomUUID().toString());
                    state.setName("health-state-" + membership.getId());
                    state.setRemoteGroup(group);
                    state.setRemoteGroupToRemote(membership);
                    derivedEntitySecurityService.inheritFromRemote(state, membership.getRemote());
                }
                capture(state, membership, now, true);
                state.setSoftDelete(false);
                retainedStateIds.add(state.getId());
                activeStates.add(state);
                toMerge.add(state);
            }
            for (RemoteGroupMemberHealthState state : existingStates) {
                if (!retainedStateIds.contains(state.getId()) && state.isActive()) {
                    state.setActive(false).setCapturedAt(now);
                    toMerge.add(state);
                }
            }

            Map<DimensionKey, RemoteGroupHealthAccumulator> accumulators = loadAccumulatorMap(group.getId());
            Set<DimensionKey> dimensions = dimensions(activeStates);
            dimensions.add(new DimensionKey(null, false));
            for (DimensionKey key : dimensions) {
                RemoteGroupHealthAccumulator accumulator = accumulators.computeIfAbsent(key,
                        ignored -> newAccumulator(group, key));
                reset(accumulator, group.getHealthInputVersion(), now);
            }
            for (RemoteGroupHealthAccumulator accumulator : accumulators.values()) {
                reset(accumulator, group.getHealthInputVersion(), now);
            }
            toMerge.addAll(accumulators.values());

            Map<String, Map<Integer, RemoteGroupSeverityBucket>> bucketsByAccumulator = new HashMap<>();
            for (RemoteGroupHealthAccumulator accumulator : accumulators.values()) {
                BucketLoad bucketLoad = loadBuckets(accumulator.getId());
                for (RemoteGroupSeverityBucket duplicate : bucketLoad.duplicates()) {
                    duplicate.setSoftDelete(true);
                    toMerge.add(duplicate);
                }
                for (RemoteGroupSeverityBucket bucket : bucketLoad.buckets().values()) {
                    bucket.setMemberCount(0).setTotalWeight(0).setSoftDelete(false);
                    toMerge.add(bucket);
                }
                bucketsByAccumulator.put(accumulator.getId(), bucketLoad.buckets());
            }

            for (RemoteGroupMemberHealthState state : activeStates) {
                for (DimensionKey key : dimensionsForState(state)) {
                    RemoteGroupHealthAccumulator accumulator = accumulators.get(key);
                    addContribution(accumulator, state, 1);
                    updateBucket(accumulator, state, 1, bucketsByAccumulator, toMerge);
                }
            }
            repository.massMerge(toMerge);
            return new GroupAccumulatorSnapshot(
                    group,
                    accumulators,
                    bucketsByAccumulator,
                    activeStates,
                    nextMembershipChange(memberships, now));
        } finally {
            lock.unlock();
            if (!lock.hasQueuedThreads()) {
                groupLocks.remove(group.getId(), lock);
            }
        }
    }

    @Transactional
    public void refreshRemote(String remoteId, OffsetDateTime now) {
        List<RemoteGroupMemberHealthState> states = repository.listMemberStatesByRemote(remoteId);
        Map<String, List<RemoteGroupMemberHealthState>> byGroup = states.stream()
                .filter(state -> state.getRemoteGroup() != null)
                .collect(Collectors.groupingBy(state -> state.getRemoteGroup().getId()));
        for (List<RemoteGroupMemberHealthState> groupStates : byGroup.values()) {
            refreshGroupStates(groupStates, now);
        }
    }

    private void refreshGroupStates(List<RemoteGroupMemberHealthState> states, OffsetDateTime now) {
        if (states.isEmpty() || states.get(0).getRemoteGroup() == null) {
            return;
        }
        String groupId = states.get(0).getRemoteGroup().getId();
        ReentrantLock lock = groupLocks.computeIfAbsent(groupId, ignored -> new ReentrantLock());
        lock.lock();
        try {
            Map<DimensionKey, RemoteGroupHealthAccumulator> accumulators = loadAccumulatorMap(groupId);
            if (accumulators.isEmpty()) {
                return;
            }
            Map<String, Map<Integer, RemoteGroupSeverityBucket>> bucketsByAccumulator = new HashMap<>();
            List<Object> toMerge = new ArrayList<>();
            for (RemoteGroupHealthAccumulator accumulator : accumulators.values()) {
                BucketLoad bucketLoad = loadBuckets(accumulator.getId());
                if (!bucketLoad.duplicates().isEmpty()) {
                    reconcile(states.get(0).getRemoteGroup(), now);
                    return;
                }
                bucketsByAccumulator.put(accumulator.getId(), bucketLoad.buckets());
            }
            for (RemoteGroupMemberHealthState state : states) {
                if (!state.isActive() || state.getRemote() == null || state.getRemoteGroupToRemote() == null) {
                    continue;
                }
                MemberContribution before = MemberContribution.from(state);
                capture(state, state.getRemoteGroupToRemote(), now, true);
                MemberContribution after = MemberContribution.from(state);
                if (before.equals(after)) {
                    continue;
                }
                applyContributionDelta(before, after, accumulators, bucketsByAccumulator, toMerge);
                state.setCapturedAt(now);
                toMerge.add(state);
            }
            toMerge.addAll(accumulators.values());
            repository.massMerge(toMerge);
        } finally {
            lock.unlock();
            if (!lock.hasQueuedThreads()) {
                groupLocks.remove(groupId, lock);
            }
        }
    }

    public double metricValue(GroupAccumulatorSnapshot snapshot,
                              FleetHealthRuleCondition condition,
                              OffsetDateTime now) {
        String roleId = condition.getMemberRole() == null ? null : condition.getMemberRole().getId();
        boolean requiredOnly = condition.isRequiredMembersOnly();
        RemoteGroupHealthAccumulator accumulator = snapshot.accumulators().get(new DimensionKey(roleId, requiredOnly));
        if (condition.getMetricType() == FleetHealthMetricType.REQUIRED_UNHEALTHY_COUNT) {
            accumulator = snapshot.accumulators().get(new DimensionKey(roleId, true));
        }
        if (accumulator == null) {
            return 0;
        }
        long denominator = condition.getDenominatorPolicy() == FleetDenominatorPolicy.MEMBERS_WITH_KNOWN_SEVERITY
                ? accumulator.getKnownSeverityMembers()
                : accumulator.getTotalMembers();
        return switch (condition.getMetricType()) {
            case MEMBER_COUNT -> accumulator.getTotalMembers();
            case OFFLINE_COUNT -> accumulator.getOfflineMembers();
            case OFFLINE_PERCENT -> percent(accumulator.getOfflineMembers(), denominator);
            case STALE_COUNT -> staleCount(snapshot, roleId, requiredOnly, condition.getStaleAfterSeconds(), now);
            case STALE_PERCENT -> percent(
                    staleCount(snapshot, roleId, requiredOnly, condition.getStaleAfterSeconds(), now), denominator);
            case SEVERITY_AT_OR_ABOVE_COUNT -> severityAtOrAbove(snapshot, accumulator, condition.getSeverityThresholdValue());
            case SEVERITY_AT_OR_ABOVE_PERCENT -> percent(
                    severityAtOrAbove(snapshot, accumulator, condition.getSeverityThresholdValue()), denominator);
            case WEIGHTED_SEVERITY_AVERAGE -> accumulator.getTotalWeight() <= 0
                    ? 0
                    : accumulator.getWeightedSeveritySum() / accumulator.getTotalWeight();
            case REQUIRED_UNHEALTHY_COUNT -> severityAtOrAbove(snapshot, accumulator, condition.getSeverityThresholdValue());
            case HUMAN_INTERVENTION_COUNT -> accumulator.getHumanInterventionMembers();
            case HUMAN_INTERVENTION_PERCENT -> percent(accumulator.getHumanInterventionMembers(), denominator);
            case UNKNOWN_SEVERITY_COUNT -> accumulator.getTotalMembers() - accumulator.getKnownSeverityMembers();
            case UNKNOWN_SEVERITY_PERCENT -> percent(
                    accumulator.getTotalMembers() - accumulator.getKnownSeverityMembers(), accumulator.getTotalMembers());
        };
    }

    private long staleCount(GroupAccumulatorSnapshot snapshot,
                            String roleId,
                            boolean requiredOnly,
                            Long staleAfterSeconds,
                            OffsetDateTime now) {
        long threshold = staleAfterSeconds == null ? 0 : Math.max(0, staleAfterSeconds);
        return snapshot.memberStates().stream()
                .filter(RemoteGroupMemberHealthState::isActive)
                .filter(state -> !requiredOnly || state.isRequiredMember())
                .filter(state -> roleId == null || state.getRole() != null && Objects.equals(roleId, state.getRole().getId()))
                .filter(state -> state.getLastSeen() == null
                        || Duration.between(state.getLastSeen(), now).getSeconds() >= threshold)
                .count();
    }

    private long severityAtOrAbove(GroupAccumulatorSnapshot snapshot,
                                   RemoteGroupHealthAccumulator accumulator,
                                   Integer threshold) {
        if (threshold == null || accumulator == null) {
            return 0;
        }
        return snapshot.bucketsByAccumulator()
                .getOrDefault(accumulator.getId(), Map.of())
                .entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getKey() >= threshold)
                .mapToLong(entry -> entry.getValue().getMemberCount())
                .sum();
    }

    private GroupAccumulatorSnapshot snapshot(RemoteGroup group, OffsetDateTime now) {
        Map<DimensionKey, RemoteGroupHealthAccumulator> accumulators = loadAccumulatorMap(group.getId());
        Map<String, Map<Integer, RemoteGroupSeverityBucket>> buckets = new HashMap<>();
        for (RemoteGroupHealthAccumulator accumulator : accumulators.values()) {
            BucketLoad bucketLoad = loadBuckets(accumulator.getId());
            if (!bucketLoad.duplicates().isEmpty()) {
                return reconcile(group, now);
            }
            buckets.put(accumulator.getId(), bucketLoad.buckets());
        }
        List<RemoteGroupMemberHealthState> states = repository.listActiveMemberStates(group.getId(), null, false);
        List<RemoteGroupToRemote> memberships = states.stream()
                .map(RemoteGroupMemberHealthState::getRemoteGroupToRemote)
                .filter(Objects::nonNull)
                .toList();
        return new GroupAccumulatorSnapshot(group, accumulators, buckets, states, nextMembershipChange(memberships, now));
    }

    private Map<String, RemoteGroupToRemote> resolveEffectiveMemberships(List<RemoteGroupToRemote> memberships,
                                                                         OffsetDateTime now) {
        Set<String> excluded = memberships.stream()
                .filter(membership -> isActive(membership, now))
                .filter(membership -> membership.getMembershipAction() == RemoteGroupMembershipAction.EXCLUDE)
                .filter(membership -> membership.getRemote() != null)
                .map(membership -> membership.getRemote().getId())
                .collect(Collectors.toSet());
        Map<String, RemoteGroupToRemote> included = new LinkedHashMap<>();
        for (RemoteGroupToRemote membership : memberships) {
            Remote remote = membership.getRemote();
            if (!isActive(membership, now)
                    || membership.getMembershipAction() == RemoteGroupMembershipAction.EXCLUDE
                    || remote == null
                    || remote.isSoftDelete()
                    || excluded.contains(remote.getId())) {
                continue;
            }
            RemoteGroupToRemote current = included.get(remote.getId());
            if (current == null || isManualOverride(membership, current)) {
                included.put(remote.getId(), membership);
            }
        }
        return included;
    }

    private boolean isManualOverride(RemoteGroupToRemote candidate, RemoteGroupToRemote current) {
        return isManual(candidate) && !isManual(current);
    }

    private boolean isManual(RemoteGroupToRemote membership) {
        return membership.getMembershipSource() == null
                || membership.getMembershipSource() == RemoteGroupMembershipSource.MANUAL;
    }

    private void capture(RemoteGroupMemberHealthState state,
                         RemoteGroupToRemote membership,
                         OffsetDateTime now,
                         boolean active) {
        Remote remote = membership.getRemote();
        derivedEntitySecurityService.setTenantFromRemote(state, remote);
        state.setRemoteGroup(membership.getRemoteGroup())
                .setRemoteGroupToRemote(membership)
                .setRemote(remote)
                .setRole(membership.getRole())
                .setRequiredMember(membership.isRequiredMember())
                .setActive(active)
                .setWeight(membership.getWeight() == null ? 1D : membership.getWeight())
                .setOffline(remote == null
                        || remote.getLastConnectivityChange() == null
                        || remote.getLastConnectivityChange().getConnectivity() != Connectivity.ON)
                .setSeverityValue(remote == null ? null : remote.getCurrentSeverityValue())
                .setHumanInterventionRequired(remote != null && remote.isHumanInterventionRequired())
                .setLastSeen(remote == null ? null : remote.getLastSeen())
                .setCapturedAt(now);
    }

    private Map<DimensionKey, RemoteGroupHealthAccumulator> loadAccumulatorMap(String groupId) {
        return repository.listAccumulators(groupId).stream().collect(Collectors.toMap(
                accumulator -> new DimensionKey(
                        accumulator.getRole() == null ? null : accumulator.getRole().getId(),
                        accumulator.isRequiredMembersOnly()),
                accumulator -> accumulator,
                (one, two) -> one,
                LinkedHashMap::new));
    }

    private Set<DimensionKey> dimensions(List<RemoteGroupMemberHealthState> states) {
        Set<DimensionKey> dimensions = new HashSet<>();
        for (RemoteGroupMemberHealthState state : states) {
            dimensions.addAll(dimensionsForState(state));
        }
        return dimensions;
    }

    private List<DimensionKey> dimensionsForState(RemoteGroupMemberHealthState state) {
        if (!state.isActive()) {
            return List.of();
        }
        List<DimensionKey> keys = new ArrayList<>();
        keys.add(new DimensionKey(null, false));
        if (state.isRequiredMember()) {
            keys.add(new DimensionKey(null, true));
        }
        if (state.getRole() != null) {
            keys.add(new DimensionKey(state.getRole().getId(), false));
            if (state.isRequiredMember()) {
                keys.add(new DimensionKey(state.getRole().getId(), true));
            }
        }
        return keys;
    }

    private RemoteGroupHealthAccumulator newAccumulator(RemoteGroup group, DimensionKey key) {
        RemoteGroupHealthAccumulator accumulator = new RemoteGroupHealthAccumulator();
        accumulator.setId(UUID.randomUUID().toString());
        accumulator.setName("health-accumulator-" + group.getId() + "-"
                + (key.roleId() == null ? "all" : key.roleId()) + "-" + key.requiredOnly());
        accumulator.setRemoteGroup(group);
        derivedEntitySecurityService.inheritFromGroup(accumulator, group);
        accumulator.setRequiredMembersOnly(key.requiredOnly());
        if (key.roleId() != null) {
            accumulator.setRole(repository.findByIdOrNull(com.wizzdi.basic.iot.model.RemoteRoleDefinition.class, key.roleId()));
        }
        return accumulator;
    }

    private void reset(RemoteGroupHealthAccumulator accumulator, long version, OffsetDateTime now) {
        derivedEntitySecurityService.setTenantFromGroup(accumulator, accumulator.getRemoteGroup());
        accumulator.setTotalMembers(0)
                .setKnownSeverityMembers(0)
                .setOfflineMembers(0)
                .setHumanInterventionMembers(0)
                .setTotalWeight(0)
                .setWeightedSeveritySum(0)
                .setAccumulatorVersion(version)
                .setReconciledAt(now)
                .setSoftDelete(false);
    }

    private void addContribution(RemoteGroupHealthAccumulator accumulator,
                                 RemoteGroupMemberHealthState state,
                                 int sign) {
        accumulator.setTotalMembers(clamp(accumulator.getTotalMembers() + sign));
        if (state.isOffline()) {
            accumulator.setOfflineMembers(clamp(accumulator.getOfflineMembers() + sign));
        }
        if (state.isHumanInterventionRequired()) {
            accumulator.setHumanInterventionMembers(clamp(accumulator.getHumanInterventionMembers() + sign));
        }
        if (state.getSeverityValue() != null) {
            accumulator.setKnownSeverityMembers(clamp(accumulator.getKnownSeverityMembers() + sign));
            accumulator.setTotalWeight(Math.max(0, accumulator.getTotalWeight() + sign * state.getWeight()));
            accumulator.setWeightedSeveritySum(Math.max(0,
                    accumulator.getWeightedSeveritySum() + sign * state.getWeight() * state.getSeverityValue()));
        }
    }

    private void updateBucket(RemoteGroupHealthAccumulator accumulator,
                              RemoteGroupMemberHealthState state,
                              int sign,
                              Map<String, Map<Integer, RemoteGroupSeverityBucket>> bucketsByAccumulator,
                              List<Object> toMerge) {
        if (state.getSeverityValue() == null) {
            return;
        }
        Map<Integer, RemoteGroupSeverityBucket> buckets = bucketsByAccumulator.computeIfAbsent(
                accumulator.getId(), ignored -> new HashMap<>());
        RemoteGroupSeverityBucket bucket = buckets.computeIfAbsent(state.getSeverityValue(), ignored -> {
            RemoteGroupSeverityBucket created = new RemoteGroupSeverityBucket();
            created.setId(deterministicBucketId(accumulator.getId(), state.getSeverityValue()));
            created.setName("severity-" + state.getSeverityValue());
            created.setAccumulator(accumulator);
            derivedEntitySecurityService.inheritFromGroup(created, accumulator.getRemoteGroup());
            created.setSeverityValue(state.getSeverityValue());
            return created;
        });
        derivedEntitySecurityService.setTenantFromGroup(bucket, accumulator.getRemoteGroup());
        bucket.setMemberCount(clamp(bucket.getMemberCount() + sign));
        bucket.setTotalWeight(Math.max(0, bucket.getTotalWeight() + sign * state.getWeight()));
        bucket.setSoftDelete(false);
        toMerge.add(bucket);
    }

    private void applyContributionDelta(MemberContribution before,
                                        MemberContribution after,
                                        Map<DimensionKey, RemoteGroupHealthAccumulator> accumulators,
                                        Map<String, Map<Integer, RemoteGroupSeverityBucket>> bucketsByAccumulator,
                                        List<Object> toMerge) {
        RemoteGroupMemberHealthState beforeState = before.toState();
        RemoteGroupMemberHealthState afterState = after.toState();
        for (DimensionKey key : dimensionsForState(beforeState)) {
            RemoteGroupHealthAccumulator accumulator = accumulators.get(key);
            if (accumulator != null) {
                addContribution(accumulator, beforeState, -1);
                updateBucket(accumulator, beforeState, -1, bucketsByAccumulator, toMerge);
            }
        }
        for (DimensionKey key : dimensionsForState(afterState)) {
            RemoteGroupHealthAccumulator accumulator = accumulators.get(key);
            if (accumulator != null) {
                addContribution(accumulator, afterState, 1);
                updateBucket(accumulator, afterState, 1, bucketsByAccumulator, toMerge);
            }
        }
    }


    private BucketLoad loadBuckets(String accumulatorId) {
        Map<Integer, RemoteGroupSeverityBucket> buckets = new LinkedHashMap<>();
        List<RemoteGroupSeverityBucket> duplicates = new ArrayList<>();
        for (RemoteGroupSeverityBucket bucket : repository.listBuckets(accumulatorId)) {
            Integer severityValue = bucket.getSeverityValue();
            if (severityValue == null) {
                duplicates.add(bucket);
                continue;
            }
            RemoteGroupSeverityBucket existing = buckets.get(severityValue);
            if (existing == null) {
                buckets.put(severityValue, bucket);
                continue;
            }
            RemoteGroupSeverityBucket canonical = canonicalBucket(existing, bucket);
            RemoteGroupSeverityBucket duplicate = canonical == existing ? bucket : existing;
            buckets.put(severityValue, canonical);
            duplicates.add(duplicate);
        }
        return new BucketLoad(buckets, duplicates);
    }

    private RemoteGroupSeverityBucket canonicalBucket(RemoteGroupSeverityBucket one,
                                                      RemoteGroupSeverityBucket two) {
        String oneId = one.getId();
        String twoId = two.getId();
        if (oneId == null) {
            return two;
        }
        if (twoId == null) {
            return one;
        }
        return oneId.compareTo(twoId) <= 0 ? one : two;
    }

    private String deterministicBucketId(String accumulatorId, Integer severityValue) {
        String key = "remote-group-severity-bucket:" + accumulatorId + ":" + severityValue;
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private boolean isActive(RemoteGroupToRemote membership, OffsetDateTime now) {
        return membership != null
                && !membership.isSoftDelete()
                && (membership.getActiveFrom() == null || !membership.getActiveFrom().isAfter(now))
                && (membership.getActiveUntil() == null || membership.getActiveUntil().isAfter(now));
    }

    private OffsetDateTime nextMembershipChange(Collection<RemoteGroupToRemote> memberships, OffsetDateTime now) {
        OffsetDateTime next = null;
        for (RemoteGroupToRemote membership : memberships) {
            if (membership.getActiveFrom() != null && membership.getActiveFrom().isAfter(now)) {
                next = earliest(next, membership.getActiveFrom());
            }
            if (membership.getActiveUntil() != null && membership.getActiveUntil().isAfter(now)) {
                next = earliest(next, membership.getActiveUntil());
            }
        }
        return next;
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

    private long clamp(long value) {
        return Math.max(0, value);
    }

    private double percent(long numerator, long denominator) {
        return denominator <= 0 ? 0 : numerator * 100D / denominator;
    }

    public record DimensionKey(String roleId, boolean requiredOnly) {
    }

    private record BucketLoad(Map<Integer, RemoteGroupSeverityBucket> buckets,
                              List<RemoteGroupSeverityBucket> duplicates) {
    }

    public record GroupAccumulatorSnapshot(
            RemoteGroup remoteGroup,
            Map<DimensionKey, RemoteGroupHealthAccumulator> accumulators,
            Map<String, Map<Integer, RemoteGroupSeverityBucket>> bucketsByAccumulator,
            List<RemoteGroupMemberHealthState> memberStates,
            OffsetDateTime nextMembershipChangeAt) {

        public RemoteGroupHealthAccumulator allMembers() {
            return accumulators.get(new DimensionKey(null, false));
        }
    }

    private record MemberContribution(
            boolean active,
            String roleId,
            boolean required,
            double weight,
            boolean offline,
            Integer severity,
            boolean intervention) {

        static MemberContribution from(RemoteGroupMemberHealthState state) {
            return new MemberContribution(
                    state.isActive(),
                    state.getRole() == null ? null : state.getRole().getId(),
                    state.isRequiredMember(),
                    state.getWeight(),
                    state.isOffline(),
                    state.getSeverityValue(),
                    state.isHumanInterventionRequired());
        }

        RemoteGroupMemberHealthState toState() {
            RemoteGroupMemberHealthState state = new RemoteGroupMemberHealthState();
            state.setActive(active)
                    .setRequiredMember(required)
                    .setWeight(weight)
                    .setOffline(offline)
                    .setSeverityValue(severity)
                    .setHumanInterventionRequired(intervention);
            if (roleId != null) {
                com.wizzdi.basic.iot.model.RemoteRoleDefinition role = new com.wizzdi.basic.iot.model.RemoteRoleDefinition();
                role.setId(roleId);
                state.setRole(role);
            }
            return state;
        }
    }
}
