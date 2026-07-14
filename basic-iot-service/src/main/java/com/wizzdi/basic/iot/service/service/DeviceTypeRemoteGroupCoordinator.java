package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.service.data.DeviceTypeRemoteGroupRepository;
import com.wizzdi.basic.iot.service.events.DeviceGroupMembershipChangedEvent;
import com.wizzdi.basic.iot.service.events.DeviceTypeGroupDefinitionChangedEvent;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicCreated;
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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Extension
@Component
public class DeviceTypeRemoteGroupCoordinator implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(DeviceTypeRemoteGroupCoordinator.class);

    @Autowired
    private DeviceTypeRemoteGroupRepository repository;
    @Autowired
    private DeviceTypeRemoteGroupMembershipService membershipService;
    @Autowired
    @Qualifier("healthEvaluationExecutor")
    private ExecutorService executor;

    @Value("${basic.iot.deviceTypeGroups.enabled:true}")
    private boolean enabled;
    @Value("${basic.iot.deviceTypeGroups.reconciliationBatchSize:1000}")
    private int reconciliationBatchSize;

    private final AtomicBoolean reconciliationRunning = new AtomicBoolean();

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onDeviceCreated(BasicCreated<Device> event) {
        submitDevice(event.getBaseclass());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onDeviceMembershipChanged(DeviceGroupMembershipChangedEvent event) {
        if (enabled && event != null && event.deviceId() != null) {
            executor.execute(() -> membershipService.synchronizeDevice(event.deviceId()));
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onDeviceTypeChanged(DeviceTypeGroupDefinitionChangedEvent event) {
        if (enabled && event != null && event.deviceTypeId() != null) {
            executor.execute(() -> membershipService.ensureDeviceTypeGroup(event.deviceTypeId()));
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        requestReconciliation();
    }

    @Scheduled(
            initialDelayString = "${basic.iot.deviceTypeGroups.reconciliationInitialDelayMs:30000}",
            fixedDelayString = "${basic.iot.deviceTypeGroups.reconciliationIntervalMs:900000}")
    public void scheduledReconciliation() {
        requestReconciliation();
    }

    private void submitDevice(Device device) {
        if (enabled && device != null) {
            executor.execute(() -> membershipService.synchronizeDevice(device.getId()));
        }
    }

    private void requestReconciliation() {
        if (!enabled || !reconciliationRunning.compareAndSet(false, true)) {
            return;
        }
        executor.execute(() -> {
            try {
                reconcileAll();
            } catch (Exception e) {
                logger.error("failed to reconcile DeviceType RemoteGroups", e);
            } finally {
                reconciliationRunning.set(false);
            }
        });
    }

    private void reconcileAll() {
        int batchSize = Math.max(1, reconciliationBatchSize);
        String afterTypeId = null;
        while (true) {
            List<String> ids = repository.listDeviceTypeIdsAfter(afterTypeId, batchSize);
            if (ids.isEmpty()) {
                break;
            }
            ids.forEach(membershipService::ensureDeviceTypeGroup);
            afterTypeId = ids.getLast();
        }

        String afterDeviceId = null;
        while (true) {
            List<String> ids = repository.listDeviceIdsAfter(afterDeviceId, batchSize);
            if (ids.isEmpty()) {
                break;
            }
            ids.forEach(membershipService::synchronizeDevice);
            afterDeviceId = ids.getLast();
        }
    }
}
