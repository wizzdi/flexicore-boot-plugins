package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.HealthSignalSourceType;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipAction;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote;
import com.wizzdi.basic.iot.service.data.RemoteGroupRepository;
import com.wizzdi.basic.iot.service.events.RemoteHealthChangedEvent;
import com.wizzdi.basic.iot.service.events.RemoteHealthInputChangedEvent;
import com.wizzdi.basic.iot.service.events.RemoteUpdatedEvent;
import com.wizzdi.basic.iot.service.request.RemoteCreate;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Coalesces high-frequency state/connectivity changes into bounded Remote and
 * RemoteGroup health evaluations. This component deliberately stores only
 * pending identifiers and change metadata; the latest committed entities are
 * loaded when evaluation actually runs.
 */
@Extension
@Component
public class RemoteHealthEvaluationCoordinator implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger("basic-iot");
    private static final String ALL_STATE_PATHS = "*";

    @Autowired
    private RemoteService remoteService;
    @Autowired
    private RemoteHealthEvaluationService remoteHealthEvaluationService;
    @Autowired
    private RemoteGroupFleetHealthService remoteGroupFleetHealthService;
    @Autowired
    private RemoteGroupRepository remoteGroupRepository;
    @Autowired
    @Qualifier("healthEvaluationExecutor")
    private ExecutorService healthEvaluationExecutor;
    @Autowired
    @Qualifier("healthRemoteEvaluationPermits")
    private Semaphore remoteEvaluationPermits;
    @Autowired
    @Qualifier("healthGroupEvaluationPermits")
    private Semaphore groupEvaluationPermits;

    @Value("${basic.iot.health.remoteDebounceMs:500}")
    private long remoteDebounceMs;
    @Value("${basic.iot.health.groupDebounceMs:2000}")
    private long groupDebounceMs;
    @Value("${basic.iot.health.remoteMaxCoalesceDelayMs:5000}")
    private long remoteMaxCoalesceDelayMs;
    @Value("${basic.iot.health.groupMaxCoalesceDelayMs:10000}")
    private long groupMaxCoalesceDelayMs;
    @Value("${basic.iot.health.maxPendingRemotes:100000}")
    private int maxPendingRemotes;
    @Value("${basic.iot.health.maxPendingGroups:50000}")
    private int maxPendingGroups;
    @Value("${basic.iot.health.maxRemoteDispatchPerCycle:2048}")
    private int maxRemoteDispatchPerCycle;
    @Value("${basic.iot.health.maxGroupDispatchPerCycle:512}")
    private int maxGroupDispatchPerCycle;

    private final Map<String, PendingRemoteEvaluation> pendingRemotes = new ConcurrentHashMap<>();
    private final Map<String, PendingGroupEvaluation> pendingGroups = new ConcurrentHashMap<>();
    private final Set<String> remotesInFlight = ConcurrentHashMap.newKeySet();
    private final Set<String> groupsInFlight = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean dispatching = new AtomicBoolean();

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRemoteUpdated(RemoteUpdatedEvent event) {
        Remote remote = event.getBaseclass();
        if (remote == null) {
            return;
        }
        RemoteCreate previous = event.getPreviousState();
        Set<String> changedStatePaths = event.isStateUpdated()
                ? findChangedPaths(previous == null ? null : previous.getDeviceProperties(), remote.getDeviceProperties())
                : Set.of();
        if (event.isStateUpdated() && changedStatePaths.isEmpty()) {
            changedStatePaths = Set.of(ALL_STATE_PATHS);
        }

        Set<HealthSignalSourceType> sourceTypes = new HashSet<>();
        boolean force = previous == null;
        if (previous != null) {
            if (!Objects.equals(previous.getVersion(), remote.getVersion())) {
                sourceTypes.add(HealthSignalSourceType.REMOTE_VERSION);
            }
            if (!Objects.equals(previous.getLastSeen(), remote.getLastSeen())) {
                sourceTypes.add(HealthSignalSourceType.REMOTE_LAST_SEEN_AGE_SECONDS);
            }
            String previousProfileId = previous.getHealthProfile() == null ? null : previous.getHealthProfile().getId();
            String currentProfileId = remote.getHealthProfile() == null ? null : remote.getHealthProfile().getId();
            String previousSchemaId = previous.getCurrentSchema() == null ? null : previous.getCurrentSchema().getId();
            String currentSchemaId = remote.getCurrentSchema() == null ? null : remote.getCurrentSchema().getId();
            force = !Objects.equals(previousProfileId, currentProfileId)
                    || !Objects.equals(previousSchemaId, currentSchemaId);
        }

        if (!changedStatePaths.isEmpty() || !sourceTypes.isEmpty() || force) {
            OffsetDateTime occurredAt = OffsetDateTime.now();
            enqueueRemote(new RemoteHealthInputChangedEvent(
                    remote.getId(),
                    changedStatePaths,
                    sourceTypes,
                    force,
                    occurredAt));
            if (containsGroupAggregateInput(sourceTypes)) {
                enqueueGroupsForRemote(remote.getId(), occurredAt);
            }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onDeviceCreated(BasicCreated<Device> event) {
        enqueueCreatedRemote(event.getBaseclass());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onGatewayCreated(BasicCreated<Gateway> event) {
        enqueueCreatedRemote(event.getBaseclass());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMembershipCreated(BasicCreated<RemoteGroupToRemote> event) {
        enqueueMembershipGroup(event.getBaseclass());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMembershipUpdated(BasicUpdated<RemoteGroupToRemote> event) {
        enqueueMembershipGroup(event.getBaseclass());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRemoteHealthInputChanged(RemoteHealthInputChangedEvent event) {
        enqueueRemote(event);
        if (event != null && containsGroupAggregateInput(event.changedSourceTypes())) {
            enqueueGroupsForRemote(event.remoteId(), event.occurredAt());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRemoteHealthChanged(RemoteHealthChangedEvent event) {
        Remote remote = event.remote();
        if (remote == null) {
            return;
        }
        OffsetDateTime now = event.occurredAt() == null ? OffsetDateTime.now() : event.occurredAt();
        enqueueGroupsForRemote(remote.getId(), now);
    }

    private void enqueueMembershipGroup(RemoteGroupToRemote membership) {
        if (membership == null || membership.getRemoteGroup() == null || !membership.getRemoteGroup().isHealthEnabled()) {
            return;
        }
        enqueueGroup(
                membership.getRemoteGroup().getId(),
                membership.getRemote() == null ? null : membership.getRemote().getId(),
                OffsetDateTime.now());
    }

    private boolean containsGroupAggregateInput(Set<HealthSignalSourceType> sourceTypes) {
        return sourceTypes != null && (sourceTypes.contains(HealthSignalSourceType.REMOTE_CONNECTIVITY)
                || sourceTypes.contains(HealthSignalSourceType.REMOTE_LAST_SEEN_AGE_SECONDS));
    }

    private void enqueueGroupsForRemote(String remoteId, OffsetDateTime occurredAt) {
        if (remoteId == null || remoteId.isBlank()) {
            return;
        }
        OffsetDateTime activeAt = occurredAt == null ? OffsetDateTime.now() : occurredAt;
        RemoteGroupToRemoteFilter filter = new RemoteGroupToRemoteFilter()
                .setRemoteIds(Set.of(remoteId))
                .setMembershipActions(Set.of(RemoteGroupMembershipAction.INCLUDE))
                .setActiveAt(activeAt);
        for (RemoteGroupToRemote membership : remoteGroupRepository.listAllMemberships(null, filter)) {
            if (membership.getRemoteGroup() != null && membership.getRemoteGroup().isHealthEnabled()) {
                enqueueGroup(membership.getRemoteGroup().getId(), remoteId, activeAt);
            }
        }
    }

    private void enqueueCreatedRemote(Remote remote) {
        if (remote != null) {
            enqueueRemote(new RemoteHealthInputChangedEvent(
                    remote.getId(),
                    Set.of(ALL_STATE_PATHS),
                    Set.of(
                            HealthSignalSourceType.REMOTE_CONNECTIVITY,
                            HealthSignalSourceType.REMOTE_LAST_SEEN_AGE_SECONDS,
                            HealthSignalSourceType.REMOTE_VERSION),
                    true,
                    OffsetDateTime.now()));
        }
    }

    public void requestRemoteEvaluation(String remoteId, OffsetDateTime occurredAt) {
        if (remoteId == null || remoteId.isBlank()) {
            return;
        }
        enqueueRemote(new RemoteHealthInputChangedEvent(
                remoteId,
                Set.of(ALL_STATE_PATHS),
                Set.of(
                        HealthSignalSourceType.REMOTE_CONNECTIVITY,
                        HealthSignalSourceType.REMOTE_LAST_SEEN_AGE_SECONDS,
                        HealthSignalSourceType.REMOTE_VERSION,
                        HealthSignalSourceType.REMOTE_CURRENT_SEVERITY,
                        HealthSignalSourceType.REMOTE_HUMAN_INTERVENTION),
                true,
                occurredAt == null ? OffsetDateTime.now() : occurredAt));
    }

    public void requestGroupEvaluation(String remoteGroupId, String changedRemoteId, OffsetDateTime occurredAt) {
        enqueueGroup(remoteGroupId, changedRemoteId, occurredAt == null ? OffsetDateTime.now() : occurredAt);
    }

    private void enqueueRemote(RemoteHealthInputChangedEvent event) {
        if (event == null || event.remoteId() == null || event.remoteId().isBlank()) {
            return;
        }
        makeRemoteCapacity();
        long requestedAt = System.currentTimeMillis();
        pendingRemotes.compute(event.remoteId(), (id, current) -> current == null
                ? PendingRemoteEvaluation.from(event, requestedAt)
                : current.merge(event, requestedAt));
    }

    private void enqueueGroup(String groupId, String changedRemoteId, OffsetDateTime occurredAt) {
        if (groupId == null || groupId.isBlank()) {
            return;
        }
        makeGroupCapacity();
        long requestedAt = System.currentTimeMillis();
        pendingGroups.compute(groupId, (id, current) -> current == null
                ? PendingGroupEvaluation.create(groupId, changedRemoteId, occurredAt, requestedAt)
                : current.merge(changedRemoteId, occurredAt, requestedAt));
    }

    private void makeRemoteCapacity() {
        int maximum = Math.max(1, maxPendingRemotes);
        if (pendingRemotes.size() < maximum) {
            return;
        }
        PendingRemoteEvaluation oldest = pendingRemotes.values().stream()
                .min(java.util.Comparator.comparingLong(PendingRemoteEvaluation::requestedAtMillis))
                .orElse(null);
        if (oldest != null && pendingRemotes.remove(oldest.remoteId(), oldest)) {
            logger.warn("health Remote pending limit {} reached; dispatching oldest Remote {} immediately", maximum, oldest.remoteId());
            dispatchRemote(oldest);
        }
    }

    private void makeGroupCapacity() {
        int maximum = Math.max(1, maxPendingGroups);
        if (pendingGroups.size() < maximum) {
            return;
        }
        PendingGroupEvaluation oldest = pendingGroups.values().stream()
                .min(java.util.Comparator.comparingLong(PendingGroupEvaluation::requestedAtMillis))
                .orElse(null);
        if (oldest != null && pendingGroups.remove(oldest.groupId(), oldest)) {
            logger.warn("health RemoteGroup pending limit {} reached; dispatching oldest group {} immediately", maximum, oldest.groupId());
            dispatchGroup(oldest);
        }
    }

    @Scheduled(fixedDelayString = "${basic.iot.health.dispatchIntervalMs:100}")
    public void dispatchDueEvaluations() {
        if (!dispatching.compareAndSet(false, true)) {
            return;
        }
        try {
            long now = System.currentTimeMillis();
            int remoteRemaining = Math.min(
                    Math.max(1, maxRemoteDispatchPerCycle),
                    remoteEvaluationPermits.availablePermits());
            for (PendingRemoteEvaluation pending : new ArrayList<>(pendingRemotes.values())) {
                if (remoteRemaining <= 0) {
                    break;
                }
                if (isDue(now, pending.firstRequestedAtMillis(), pending.requestedAtMillis(), remoteDebounceMs, remoteMaxCoalesceDelayMs)
                        && pendingRemotes.remove(pending.remoteId(), pending)
                        && dispatchRemote(pending)) {
                    remoteRemaining--;
                }
            }
            int groupRemaining = Math.min(
                    Math.max(1, maxGroupDispatchPerCycle),
                    groupEvaluationPermits.availablePermits());
            for (PendingGroupEvaluation pending : new ArrayList<>(pendingGroups.values())) {
                if (groupRemaining <= 0) {
                    break;
                }
                if (isDue(now, pending.firstRequestedAtMillis(), pending.requestedAtMillis(), groupDebounceMs, groupMaxCoalesceDelayMs)
                        && pendingGroups.remove(pending.groupId(), pending)
                        && dispatchGroup(pending)) {
                    groupRemaining--;
                }
            }
        } finally {
            dispatching.set(false);
        }
    }

    private boolean isDue(long now, long firstRequestedAt, long lastRequestedAt, long debounceMs, long maxCoalesceDelayMs) {
        return now - lastRequestedAt >= Math.max(0, debounceMs)
                || now - firstRequestedAt >= Math.max(Math.max(0, debounceMs), maxCoalesceDelayMs);
    }

    private boolean dispatchRemote(PendingRemoteEvaluation pending) {
        if (!remotesInFlight.add(pending.remoteId())) {
            pendingRemotes.merge(pending.remoteId(), pending, PendingRemoteEvaluation::mergePending);
            return false;
        }
        if (!remoteEvaluationPermits.tryAcquire()) {
            remotesInFlight.remove(pending.remoteId());
            pendingRemotes.merge(pending.remoteId(), pending, PendingRemoteEvaluation::mergePending);
            return false;
        }
        try {
            healthEvaluationExecutor.execute(() -> {
                try {
                    Remote remote = remoteService.findByIdOrNull(Remote.class, pending.remoteId());
                    if (remote == null || remote.isSoftDelete()) {
                        return;
                    }
                    if (!remoteHealthEvaluationService.isEvaluationRelevant(
                            remote,
                            pending.changedStatePaths(),
                            pending.changedSourceTypes(),
                            pending.force())) {
                        return;
                    }
                    remoteHealthEvaluationService.evaluate(remote, OffsetDateTime.now());
                } catch (Throwable e) {
                    logger.error("failed evaluating health for Remote {}", pending.remoteId(), e);
                } finally {
                    remoteEvaluationPermits.release();
                    remotesInFlight.remove(pending.remoteId());
                }
            });
            return true;
        } catch (RejectedExecutionException e) {
            remoteEvaluationPermits.release();
            remotesInFlight.remove(pending.remoteId());
            pendingRemotes.merge(pending.remoteId(), pending, PendingRemoteEvaluation::mergePending);
            logger.debug("health evaluation executor is shutting down; Remote {} was returned to the pending queue", pending.remoteId());
            return false;
        }
    }

    private boolean dispatchGroup(PendingGroupEvaluation pending) {
        if (!groupsInFlight.add(pending.groupId())) {
            pendingGroups.merge(pending.groupId(), pending, PendingGroupEvaluation::mergePending);
            return false;
        }
        if (!groupEvaluationPermits.tryAcquire()) {
            groupsInFlight.remove(pending.groupId());
            pendingGroups.merge(pending.groupId(), pending, PendingGroupEvaluation::mergePending);
            return false;
        }
        try {
            healthEvaluationExecutor.execute(() -> {
                try {
                    remoteGroupFleetHealthService.evaluateAutomatic(pending.groupId());
                } catch (org.springframework.web.server.ResponseStatusException e) {
                    logger.debug("skipping automatic health evaluation for RemoteGroup {}: {}", pending.groupId(), e.getReason());
                } catch (Throwable e) {
                    logger.error("failed evaluating fleet health for RemoteGroup {}", pending.groupId(), e);
                } finally {
                    groupEvaluationPermits.release();
                    groupsInFlight.remove(pending.groupId());
                }
            });
            return true;
        } catch (RejectedExecutionException e) {
            groupEvaluationPermits.release();
            groupsInFlight.remove(pending.groupId());
            pendingGroups.merge(pending.groupId(), pending, PendingGroupEvaluation::mergePending);
            logger.debug("health evaluation executor is shutting down; RemoteGroup {} was returned to the pending queue", pending.groupId());
            return false;
        }
    }

    private Set<String> findChangedPaths(Map<String, Object> previous, Map<String, Object> current) {
        Set<String> changed = new LinkedHashSet<>();
        collectChangedPaths(previous, current, "", changed);
        return Set.copyOf(changed);
    }

    private void collectChangedPaths(Object previous, Object current, String path, Set<String> changed) {
        if (Objects.equals(previous, current)) {
            return;
        }
        if (previous instanceof Map<?, ?> previousMap && current instanceof Map<?, ?> currentMap) {
            Set<String> keys = new LinkedHashSet<>();
            previousMap.keySet().forEach(key -> keys.add(String.valueOf(key)));
            currentMap.keySet().forEach(key -> keys.add(String.valueOf(key)));
            for (String key : keys) {
                String childPath = path.isEmpty() ? key : path + "." + key;
                collectChangedPaths(previousMap.get(key), currentMap.get(key), childPath, changed);
            }
            return;
        }
        if (!path.isEmpty()) {
            changed.add(path);
        } else {
            changed.add(ALL_STATE_PATHS);
        }
    }

    private record PendingRemoteEvaluation(
            String remoteId,
            Set<String> changedStatePaths,
            Set<HealthSignalSourceType> changedSourceTypes,
            boolean force,
            OffsetDateTime occurredAt,
            long firstRequestedAtMillis,
            long requestedAtMillis) {

        private static PendingRemoteEvaluation from(RemoteHealthInputChangedEvent event, long requestedAtMillis) {
            return new PendingRemoteEvaluation(
                    event.remoteId(),
                    event.changedStatePaths(),
                    event.changedSourceTypes(),
                    event.force(),
                    event.occurredAt(),
                    requestedAtMillis,
                    requestedAtMillis);
        }

        private PendingRemoteEvaluation merge(RemoteHealthInputChangedEvent event, long requestedAtMillis) {
            Set<String> paths = new HashSet<>(changedStatePaths);
            paths.addAll(event.changedStatePaths());
            Set<HealthSignalSourceType> sources = new HashSet<>(changedSourceTypes);
            sources.addAll(event.changedSourceTypes());
            OffsetDateTime earliest = occurredAt.isBefore(event.occurredAt()) ? occurredAt : event.occurredAt();
            return new PendingRemoteEvaluation(remoteId, Set.copyOf(paths), Set.copyOf(sources), force || event.force(), earliest, firstRequestedAtMillis, requestedAtMillis);
        }

        private static PendingRemoteEvaluation mergePending(PendingRemoteEvaluation one, PendingRemoteEvaluation two) {
            Set<String> paths = new HashSet<>(one.changedStatePaths);
            paths.addAll(two.changedStatePaths);
            Set<HealthSignalSourceType> sources = new HashSet<>(one.changedSourceTypes);
            sources.addAll(two.changedSourceTypes);
            OffsetDateTime earliest = one.occurredAt.isBefore(two.occurredAt) ? one.occurredAt : two.occurredAt;
            return new PendingRemoteEvaluation(
                    one.remoteId,
                    Set.copyOf(paths),
                    Set.copyOf(sources),
                    one.force || two.force,
                    earliest,
                    Math.min(one.firstRequestedAtMillis, two.firstRequestedAtMillis),
                    Math.max(one.requestedAtMillis, two.requestedAtMillis));
        }
    }

    private record PendingGroupEvaluation(
            String groupId,
            Set<String> changedRemoteIds,
            OffsetDateTime occurredAt,
            long firstRequestedAtMillis,
            long requestedAtMillis) {

        private static PendingGroupEvaluation create(String groupId, String remoteId, OffsetDateTime occurredAt, long requestedAtMillis) {
            return new PendingGroupEvaluation(
                    groupId,
                    remoteId == null ? Set.of() : Set.of(remoteId),
                    occurredAt == null ? OffsetDateTime.now() : occurredAt,
                    requestedAtMillis,
                    requestedAtMillis);
        }

        private PendingGroupEvaluation merge(String remoteId, OffsetDateTime newOccurredAt, long newRequestedAtMillis) {
            Set<String> ids = new HashSet<>(changedRemoteIds);
            if (remoteId != null) {
                ids.add(remoteId);
            }
            OffsetDateTime candidate = newOccurredAt == null ? OffsetDateTime.now() : newOccurredAt;
            OffsetDateTime earliest = occurredAt.isBefore(candidate) ? occurredAt : candidate;
            return new PendingGroupEvaluation(groupId, Set.copyOf(ids), earliest, firstRequestedAtMillis, newRequestedAtMillis);
        }

        private static PendingGroupEvaluation mergePending(PendingGroupEvaluation one, PendingGroupEvaluation two) {
            Set<String> ids = new HashSet<>(one.changedRemoteIds);
            ids.addAll(two.changedRemoteIds);
            OffsetDateTime earliest = one.occurredAt.isBefore(two.occurredAt) ? one.occurredAt : two.occurredAt;
            return new PendingGroupEvaluation(
                    one.groupId,
                    Set.copyOf(ids),
                    earliest,
                    Math.min(one.firstRequestedAtMillis, two.firstRequestedAtMillis),
                    Math.max(one.requestedAtMillis, two.requestedAtMillis));
        }
    }
}
