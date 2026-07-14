package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.service.data.HealthReconciliationRepository;
import com.wizzdi.basic.iot.service.events.DeviceTypeHealthProfileChangedEvent;
import com.wizzdi.basic.iot.service.events.FleetHealthPolicyChangedEvent;
import com.wizzdi.basic.iot.service.events.RemoteGroupDefinitionChangedEvent;
import com.wizzdi.basic.iot.service.events.RemoteGroupMembershipChangedEvent;
import com.wizzdi.basic.iot.service.events.RemoteHealthProfileChangedEvent;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

@Extension
@Component
public class HealthDefinitionReevaluationCoordinator implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger("basic-iot");

    @Autowired
    private HealthReconciliationRepository reconciliationRepository;
    @Autowired
    private RemoteHealthEvaluationCoordinator evaluationCoordinator;
    @Autowired
    @Qualifier("healthEvaluationExecutor")
    private ExecutorService healthEvaluationExecutor;

    @Value("${basic.iot.health.reconciliationBatchSize:1000}")
    private int reconciliationBatchSize;

    private final AtomicBoolean reconciliationRunning = new AtomicBoolean();

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRemoteHealthProfileChanged(RemoteHealthProfileChangedEvent event) {
        if (event == null || event.remoteHealthProfileId() == null) {
            return;
        }
        submitFanOut("RemoteHealthProfile " + event.remoteHealthProfileId(), () -> {
            enqueueProfileRemotes(event.remoteHealthProfileId(), event.occurredAt());
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onFleetHealthPolicyChanged(FleetHealthPolicyChangedEvent event) {
        if (event == null || event.fleetHealthPolicyId() == null) {
            return;
        }
        submitFanOut("FleetHealthPolicy " + event.fleetHealthPolicyId(), () ->
                forEachGroupByPolicy(event.fleetHealthPolicyId(), event.occurredAt()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onDeviceTypeHealthProfileChanged(DeviceTypeHealthProfileChangedEvent event) {
        if (event == null || event.deviceTypeId() == null) {
            return;
        }
        submitFanOut("DeviceType " + event.deviceTypeId(), () ->
                forEachInheritedDeviceByDeviceType(event.deviceTypeId(), event.occurredAt()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRemoteGroupDefinitionChanged(RemoteGroupDefinitionChangedEvent event) {
        if (event != null) {
            evaluationCoordinator.requestGroupEvaluation(event.remoteGroupId(), null, event.occurredAt());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRemoteGroupMembershipChanged(RemoteGroupMembershipChangedEvent event) {
        if (event == null || event.remoteGroupIds() == null) {
            return;
        }
        for (String groupId : event.remoteGroupIds()) {
            evaluationCoordinator.requestGroupEvaluation(groupId, null, event.occurredAt());
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        submitReconciliation("application ready");
    }

    @Scheduled(
            initialDelayString = "${basic.iot.health.reconciliationInitialDelayMs:30000}",
            fixedDelayString = "${basic.iot.health.reconciliationIntervalMs:900000}")
    public void scheduledReconciliation() {
        submitReconciliation("scheduled");
    }

    private void submitReconciliation(String reason) {
        if (!reconciliationRunning.compareAndSet(false, true)) {
            return;
        }
        try {
            healthEvaluationExecutor.execute(() -> {
                try {
                    reconcileStaleHealth();
                } catch (Throwable e) {
                    logger.error("health reconciliation failed ({})", reason, e);
                } finally {
                    reconciliationRunning.set(false);
                }
            });
        } catch (RejectedExecutionException e) {
            reconciliationRunning.set(false);
            logger.debug("health reconciliation executor is shutting down");
        }
    }

    private void submitFanOut(String source, Runnable task) {
        try {
            healthEvaluationExecutor.execute(() -> {
                try {
                    task.run();
                } catch (Throwable e) {
                    logger.error("failed propagating health definition change for {}", source, e);
                }
            });
        } catch (RejectedExecutionException e) {
            logger.debug("health evaluation executor is shutting down; skipped propagation for {}", source);
        }
    }

    private void enqueueProfileRemotes(String profileId, OffsetDateTime occurredAt) {
        int count = 0;
        String afterId = null;
        while (true) {
            List<String> ids = reconciliationRepository.listDirectRemoteIdsForProfile(
                    profileId, afterId, batchSize());
            for (String id : ids) {
                evaluationCoordinator.requestRemoteEvaluation(id, occurredAt);
                count++;
            }
            if (ids.size() < batchSize()) {
                break;
            }
            afterId = ids.get(ids.size() - 1);
        }
        afterId = null;
        while (true) {
            List<String> ids = reconciliationRepository.listInheritedDeviceIdsForProfile(
                    profileId, afterId, batchSize());
            for (String id : ids) {
                evaluationCoordinator.requestRemoteEvaluation(id, occurredAt);
                count++;
            }
            if (ids.size() < batchSize()) {
                break;
            }
            afterId = ids.get(ids.size() - 1);
        }
        logger.info("queued {} Remote health evaluations for changed profile {}", count, profileId);
    }

    private void forEachInheritedDeviceByDeviceType(String deviceTypeId, OffsetDateTime occurredAt) {
        int count = 0;
        String afterId = null;
        while (true) {
            List<String> ids = reconciliationRepository.listInheritedDeviceIdsForDeviceType(
                    deviceTypeId, afterId, batchSize());
            for (String id : ids) {
                evaluationCoordinator.requestRemoteEvaluation(id, occurredAt);
                count++;
            }
            if (ids.size() < batchSize()) {
                break;
            }
            afterId = ids.get(ids.size() - 1);
        }
        logger.info("queued {} inherited Device health evaluations for changed DeviceType {}", count, deviceTypeId);
    }

    private void forEachGroupByPolicy(String policyId, OffsetDateTime occurredAt) {
        int count = 0;
        String afterId = null;
        while (true) {
            List<String> ids = reconciliationRepository.listGroupIdsForPolicy(
                    policyId, afterId, batchSize());
            for (String id : ids) {
                evaluationCoordinator.requestGroupEvaluation(id, null, occurredAt);
                count++;
            }
            if (ids.size() < batchSize()) {
                break;
            }
            afterId = ids.get(ids.size() - 1);
        }
        logger.info("queued {} RemoteGroup evaluations for changed policy {}", count, policyId);
    }

    private void reconcileStaleHealth() {
        OffsetDateTime now = OffsetDateTime.now();
        int remotes = 0;
        remotes += enqueueRemoteBatches(reconciliationRepository::listStaleDirectRemoteIds, now);
        remotes += enqueueRemoteBatches(reconciliationRepository::listStaleInheritedDeviceIds, now);
        remotes += enqueueRemoteBatches(reconciliationRepository::listUnconfiguredDeviceIdsWithProjection, now);
        remotes += enqueueRemoteBatches(reconciliationRepository::listUnconfiguredGatewayIdsWithProjection, now);

        int groups = 0;
        groups += enqueueGroupBatches(reconciliationRepository::listStaleGroupIds, now);
        groups += enqueueGroupBatches(reconciliationRepository::listDisabledOrUnconfiguredGroupIdsWithProjection, now);
        logger.info("health reconciliation queued {} Remote and {} RemoteGroup evaluations", remotes, groups);
    }

    private int enqueueRemoteBatches(IdBatchLoader loader, OffsetDateTime occurredAt) {
        int count = 0;
        String afterId = null;
        while (true) {
            List<String> ids = loader.load(afterId, batchSize());
            for (String id : ids) {
                evaluationCoordinator.requestRemoteEvaluation(id, occurredAt);
                count++;
            }
            if (ids.size() < batchSize()) {
                return count;
            }
            afterId = ids.get(ids.size() - 1);
        }
    }

    private int enqueueGroupBatches(IdBatchLoader loader, OffsetDateTime occurredAt) {
        int count = 0;
        String afterId = null;
        while (true) {
            List<String> ids = loader.load(afterId, batchSize());
            for (String id : ids) {
                evaluationCoordinator.requestGroupEvaluation(id, null, occurredAt);
                count++;
            }
            if (ids.size() < batchSize()) {
                return count;
            }
            afterId = ids.get(ids.size() - 1);
        }
    }

    private int batchSize() {
        return Math.max(1, reconciliationBatchSize);
    }

    @FunctionalInterface
    private interface IdBatchLoader {
        List<String> load(String afterId, int limit);
    }
}
