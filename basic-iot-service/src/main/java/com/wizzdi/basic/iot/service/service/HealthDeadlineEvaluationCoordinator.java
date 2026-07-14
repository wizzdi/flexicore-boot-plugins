package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.service.data.HealthReconciliationRepository;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Dispatches only persisted health deadlines that are due. The Spring scheduler
 * performs no database or evaluation work itself; it submits one virtual-thread
 * task that queries the indexed deadline columns and feeds the existing
 * coalescing coordinator.
 */
@Extension
@Component
public class HealthDeadlineEvaluationCoordinator implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger("basic-iot");

    @Autowired
    private HealthReconciliationRepository repository;
    @Autowired
    private RemoteHealthEvaluationCoordinator evaluationCoordinator;
    @Autowired
    @Qualifier("healthEvaluationExecutor")
    private ExecutorService healthEvaluationExecutor;

    @Value("${basic.iot.health.deadlineBatchSize:2000}")
    private int deadlineBatchSize;
    @Value("${basic.iot.health.maxRemoteDeadlineDispatchPerCycle:16000}")
    private int maxRemoteDeadlineDispatchPerCycle;
    @Value("${basic.iot.health.maxGroupDeadlineDispatchPerCycle:4000}")
    private int maxGroupDeadlineDispatchPerCycle;

    private final AtomicBoolean dispatchRunning = new AtomicBoolean();

    @Scheduled(
            initialDelayString = "${basic.iot.health.deadlineInitialDelayMs:1000}",
            fixedDelayString = "${basic.iot.health.deadlineDispatchIntervalMs:500}")
    public void dispatchDueDeadlines() {
        if (!dispatchRunning.compareAndSet(false, true)) {
            return;
        }
        try {
            healthEvaluationExecutor.execute(() -> {
                try {
                    dispatchDue();
                } catch (Throwable e) {
                    logger.error("health deadline dispatch failed", e);
                } finally {
                    dispatchRunning.set(false);
                }
            });
        } catch (RejectedExecutionException e) {
            dispatchRunning.set(false);
            logger.debug("health evaluation executor is shutting down; deadline dispatch skipped");
        }
    }

    private void dispatchDue() {
        OffsetDateTime now = OffsetDateTime.now();
        int remoteMaximum = Math.max(1, maxRemoteDeadlineDispatchPerCycle);
        int groupMaximum = Math.max(1, maxGroupDeadlineDispatchPerCycle);
        int remotes = dispatchRemoteDeadlines(now, remoteMaximum);
        int groups = dispatchGroupDeadlines(now, groupMaximum);
        if (remotes >= remoteMaximum) {
            logger.warn("Remote health deadline dispatch reached per-cycle limit {}", remoteMaximum);
        }
        if (groups >= groupMaximum) {
            logger.warn("RemoteGroup health deadline dispatch reached per-cycle limit {}", groupMaximum);
        }
    }

    private int dispatchRemoteDeadlines(OffsetDateTime now, int maximum) {
        int count = 0;
        String afterId = null;
        int batchSize = Math.max(1, deadlineBatchSize);
        while (count < maximum) {
            int limit = Math.min(batchSize, maximum - count);
            List<String> ids = repository.listDueRemoteHealthIds(now, afterId, limit);
            for (String id : ids) {
                evaluationCoordinator.requestRemoteEvaluation(id, now);
                count++;
            }
            if (ids.size() < limit) {
                return count;
            }
            afterId = ids.get(ids.size() - 1);
        }
        return count;
    }

    private int dispatchGroupDeadlines(OffsetDateTime now, int maximum) {
        int count = 0;
        String afterId = null;
        int batchSize = Math.max(1, deadlineBatchSize);
        while (count < maximum) {
            int limit = Math.min(batchSize, maximum - count);
            List<String> ids = repository.listDueGroupHealthIds(now, afterId, limit);
            for (String id : ids) {
                evaluationCoordinator.requestGroupEvaluation(id, null, now);
                count++;
            }
            if (ids.size() < limit) {
                return count;
            }
            afterId = ids.get(ids.size() - 1);
        }
        return count;
    }
}
