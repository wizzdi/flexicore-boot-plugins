package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipAction;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipSource;
import com.wizzdi.basic.iot.model.RemoteGroupPopulationType;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote;
import com.wizzdi.basic.iot.service.data.DeviceTypeRemoteGroupRepository;
import com.wizzdi.basic.iot.service.events.RemoteGroupDefinitionChangedEvent;
import com.wizzdi.basic.iot.service.events.RemoteGroupMembershipChangedEvent;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Extension
@Component
public class DeviceTypeRemoteGroupMembershipService implements Plugin {

    private static final String GROUP_EXTERNAL_ID_PREFIX = "device-type-group:";

    @Autowired
    private DeviceTypeRemoteGroupRepository repository;
    @Autowired
    private DerivedEntitySecurityService derivedEntitySecurityService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final ConcurrentHashMap<String, ReentrantLock> deviceTypeLocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> deviceLocks = new ConcurrentHashMap<>();

    @Transactional
    public RemoteGroup ensureDeviceTypeGroup(String deviceTypeId) {
        DeviceType deviceType = repository.findDeviceType(deviceTypeId);
        if (deviceType == null || deviceType.isSoftDelete()) {
            return null;
        }
        ReentrantLock lock = deviceTypeLocks.computeIfAbsent(deviceTypeId, ignored -> new ReentrantLock());
        lock.lock();
        boolean releaseAfterMethod = deferUnlockUntilTransactionCompletion(deviceTypeLocks, deviceTypeId, lock);
        try {
            RemoteGroup group = repository.findManagedGroupByDeviceType(deviceTypeId);
            boolean changed = false;
            if (group == null) {
                group = new RemoteGroup();
                group.setId(UUID.randomUUID().toString());
                group.setName("All " + deviceType.getName());
                group.setExternalId(GROUP_EXTERNAL_ID_PREFIX + deviceType.getId());
                group.setPopulationType(RemoteGroupPopulationType.DEVICE_TYPE);
                group.setSourceDeviceType(deviceType);
                group.setSystemManaged(true);
                group.setHealthInputVersion(1);
                group.setHealthEnabled(false);
                derivedEntitySecurityService.inherit(group, deviceType);
                changed = true;
            } else {
                if (group.isSoftDelete()) {
                    group.setSoftDelete(false);
                    changed = true;
                }
                if (group.getPopulationType() != RemoteGroupPopulationType.DEVICE_TYPE) {
                    group.setPopulationType(RemoteGroupPopulationType.DEVICE_TYPE);
                    changed = true;
                }
                if (!same(group.getSourceDeviceType(), deviceType)) {
                    group.setSourceDeviceType(deviceType);
                    changed = true;
                }
                if (!group.isSystemManaged()) {
                    group.setSystemManaged(true);
                    changed = true;
                }
                String expectedName = "All " + deviceType.getName();
                if (!Objects.equals(expectedName, group.getName())) {
                    group.setName(expectedName);
                    changed = true;
                }
                String expectedExternalId = GROUP_EXTERNAL_ID_PREFIX + deviceType.getId();
                if (!Objects.equals(expectedExternalId, group.getExternalId())) {
                    group.setExternalId(expectedExternalId);
                    changed = true;
                }
                derivedEntitySecurityService.setTenantFrom(group, deviceType);
            }
            if (changed) {
                repository.merge(group);
                eventPublisher.publishEvent(new RemoteGroupDefinitionChangedEvent(group.getId(), OffsetDateTime.now()));
            }
            return group;
        } finally {
            if (releaseAfterMethod) {
                unlock(deviceTypeLocks, deviceTypeId, lock);
            }
        }
    }

    @Transactional
    public void synchronizeDevice(String deviceId) {
        ReentrantLock lock = deviceLocks.computeIfAbsent(deviceId, ignored -> new ReentrantLock());
        lock.lock();
        boolean releaseAfterMethod = deferUnlockUntilTransactionCompletion(deviceLocks, deviceId, lock);
        try {
            Device device = repository.findDevice(deviceId);
            List<RemoteGroupToRemote> automaticMemberships = repository.listAutomaticMembershipsForRemote(deviceId);
            RemoteGroup targetGroup = device == null || device.isSoftDelete() || device.getDeviceType() == null
                    ? null
                    : ensureDeviceTypeGroup(device.getDeviceType().getId());

            Set<String> affectedGroupIds = new LinkedHashSet<>();
            List<Object> toMerge = new ArrayList<>();
            RemoteGroupToRemote targetMembership = null;
            for (RemoteGroupToRemote membership : automaticMemberships) {
                boolean belongsToTarget = targetGroup != null
                        && membership.getRemoteGroup() != null
                        && Objects.equals(targetGroup.getId(), membership.getRemoteGroup().getId());
                if (belongsToTarget && targetMembership == null) {
                    targetMembership = membership;
                    boolean changed = membership.isSoftDelete()
                            || membership.getMembershipAction() != RemoteGroupMembershipAction.INCLUDE
                            || membership.getMembershipSource() != RemoteGroupMembershipSource.DEVICE_TYPE
                            || !same(membership.getRemote(), device)
                            || !same(membership.getRemoteGroup(), targetGroup)
                            || membership.isRequiredMember()
                            || !Objects.equals(membership.getWeight(), 1D)
                            || membership.getActiveFrom() != null
                            || membership.getActiveUntil() != null
                            || !same(membership.getTenant(), device.getTenant());
                    membership.setSoftDelete(false);
                    membership.setMembershipAction(RemoteGroupMembershipAction.INCLUDE);
                    membership.setMembershipSource(RemoteGroupMembershipSource.DEVICE_TYPE);
                    membership.setRemote(device);
                    membership.setRemoteGroup(targetGroup);
                    membership.setWeight(1D);
                    membership.setRequiredMember(false);
                    membership.setActiveFrom(null);
                    membership.setActiveUntil(null);
                    derivedEntitySecurityService.inheritFromRemote(membership, device);
                    if (changed) {
                        toMerge.add(membership);
                        affectedGroupIds.add(targetGroup.getId());
                    }
                } else if (!membership.isSoftDelete()) {
                    membership.setSoftDelete(true);
                    toMerge.add(membership);
                    if (membership.getRemoteGroup() != null) {
                        affectedGroupIds.add(membership.getRemoteGroup().getId());
                    }
                }
            }

            if (targetGroup != null && targetMembership == null) {
                RemoteGroupToRemote membership = new RemoteGroupToRemote();
                membership.setId(UUID.randomUUID().toString());
                membership.setName("device-type-membership-" + targetGroup.getId() + "-" + device.getId());
                membership.setRemoteGroup(targetGroup);
                membership.setRemote(device);
                membership.setMembershipAction(RemoteGroupMembershipAction.INCLUDE);
                membership.setMembershipSource(RemoteGroupMembershipSource.DEVICE_TYPE);
                membership.setRequiredMember(false);
                membership.setWeight(1D);
                derivedEntitySecurityService.inheritFromRemote(membership, device);
                toMerge.add(membership);
                affectedGroupIds.add(targetGroup.getId());
            }

            if (!toMerge.isEmpty()) {
                repository.massMerge(toMerge);
                incrementGroupVersions(automaticMemberships, targetGroup, affectedGroupIds);
                eventPublisher.publishEvent(new RemoteGroupMembershipChangedEvent(
                        Set.copyOf(affectedGroupIds),
                        Set.of(deviceId),
                        OffsetDateTime.now()));
            }
        } finally {
            if (releaseAfterMethod) {
                unlock(deviceLocks, deviceId, lock);
            }
        }
    }

    private boolean deferUnlockUntilTransactionCompletion(ConcurrentHashMap<String, ReentrantLock> locks,
                                                           String key,
                                                           ReentrantLock lock) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return true;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                unlock(locks, key, lock);
            }
        });
        return false;
    }

    private void unlock(ConcurrentHashMap<String, ReentrantLock> locks, String key, ReentrantLock lock) {
        lock.unlock();
        if (!lock.hasQueuedThreads()) {
            locks.remove(key, lock);
        }
    }

    private void incrementGroupVersions(List<RemoteGroupToRemote> memberships,
                                        RemoteGroup targetGroup,
                                        Set<String> affectedGroupIds) {
        List<Object> groups = new ArrayList<>();
        Set<String> handled = new LinkedHashSet<>();
        if (targetGroup != null && affectedGroupIds.contains(targetGroup.getId())) {
            targetGroup.setHealthInputVersion(Math.max(1, targetGroup.getHealthInputVersion() + 1));
            groups.add(targetGroup);
            handled.add(targetGroup.getId());
        }
        for (RemoteGroupToRemote membership : memberships) {
            RemoteGroup group = membership.getRemoteGroup();
            if (group != null && affectedGroupIds.contains(group.getId()) && handled.add(group.getId())) {
                group.setHealthInputVersion(Math.max(1, group.getHealthInputVersion() + 1));
                groups.add(group);
            }
        }
        repository.massMerge(groups);
    }

    private boolean same(com.flexicore.model.Baseclass left, com.flexicore.model.Baseclass right) {
        return left == null ? right == null : right != null && Objects.equals(left.getId(), right.getId());
    }
}
