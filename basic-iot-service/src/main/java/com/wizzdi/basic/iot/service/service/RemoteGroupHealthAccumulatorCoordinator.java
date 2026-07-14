package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.HealthSignalSourceType;
import com.wizzdi.basic.iot.service.data.RemoteGroupHealthAccumulatorRepository;
import com.wizzdi.basic.iot.service.events.RemoteGroupDefinitionChangedEvent;
import com.wizzdi.basic.iot.service.events.RemoteGroupMembershipChangedEvent;
import com.wizzdi.basic.iot.service.events.RemoteHealthChangedEvent;
import com.wizzdi.basic.iot.service.events.RemoteHealthInputChangedEvent;
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
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

@Extension
@Component
public class RemoteGroupHealthAccumulatorCoordinator implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger("basic-iot");

    @Autowired
    private RemoteGroupHealthAccumulatorService accumulatorService;
    @Autowired
    private RemoteGroupHealthAccumulatorRepository repository;
    @Autowired
    @Qualifier("healthEvaluationExecutor")
    private ExecutorService healthEvaluationExecutor;

    @Value("${basic.iot.health.accumulatorReconciliationBatchSize:500}")
    private int reconciliationBatchSize;

    private final AtomicBoolean reconciliationRunning = new AtomicBoolean();

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRemoteHealthChanged(RemoteHealthChangedEvent event) {
        if (event != null && event.remote() != null) {
            submit(() -> accumulatorService.refreshRemote(event.remote().getId(), event.occurredAt()));
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onRemoteHealthInputChanged(RemoteHealthInputChangedEvent event) {
        if (event == null || event.remoteId() == null || event.changedSourceTypes() == null) {
            return;
        }
        Set<HealthSignalSourceType> sources = event.changedSourceTypes();
        if (sources.contains(HealthSignalSourceType.REMOTE_CONNECTIVITY)
                || sources.contains(HealthSignalSourceType.REMOTE_LAST_SEEN_AGE_SECONDS)) {
            submit(() -> accumulatorService.refreshRemote(event.remoteId(), event.occurredAt()));
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMembershipChanged(RemoteGroupMembershipChangedEvent event) {
        if (event == null || event.remoteGroupIds() == null) {
            return;
        }
        for (String groupId : event.remoteGroupIds()) {
            submit(() -> accumulatorService.reconcileById(groupId, event.occurredAt()));
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onGroupDefinitionChanged(RemoteGroupDefinitionChangedEvent event) {
        if (event != null && event.remoteGroupId() != null) {
            submit(() -> accumulatorService.reconcileById(event.remoteGroupId(), event.occurredAt()));
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        submitReconciliation();
    }

    @Scheduled(
            initialDelayString = "${basic.iot.health.accumulatorReconciliationInitialDelayMs:60000}",
            fixedDelayString = "${basic.iot.health.accumulatorReconciliationIntervalMs:1800000}")
    public void scheduledReconciliation() {
        submitReconciliation();
    }

    private void submitReconciliation() {
        if (!reconciliationRunning.compareAndSet(false, true)) {
            return;
        }
        submit(() -> {
            try {
                reconcileAllGroups();
            } finally {
                reconciliationRunning.set(false);
            }
        });
    }

    private void reconcileAllGroups() {
        int total = 0;
        String afterId = null;
        int batchSize = Math.max(1, reconciliationBatchSize);
        while (true) {
            List<String> ids = repository.listHealthEnabledGroupIds(afterId, batchSize);
            for (String id : ids) {
                accumulatorService.reconcileById(id, OffsetDateTime.now());
                total++;
            }
            if (ids.size() < batchSize) {
                break;
            }
            afterId = ids.get(ids.size() - 1);
        }
        logger.info("reconciled RemoteGroup health accumulators for {} groups", total);
    }

    private void submit(Runnable runnable) {
        try {
            healthEvaluationExecutor.execute(() -> {
                try {
                    runnable.run();
                } catch (Throwable e) {
                    logger.error("RemoteGroup health accumulator task failed", e);
                }
            });
        } catch (RejectedExecutionException e) {
            logger.debug("health evaluation executor is shutting down; skipped accumulator task");
        }
    }
}
