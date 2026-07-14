package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.DeviceType_;
import com.wizzdi.basic.iot.model.Device_;
import com.wizzdi.basic.iot.model.FleetHealthPolicy;
import com.wizzdi.basic.iot.model.FleetHealthPolicy_;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroup_;
import com.wizzdi.basic.iot.model.RemoteHealthProfile;
import com.wizzdi.basic.iot.model.RemoteHealthProfile_;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Extension
@Component
public class HealthReconciliationRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;

    public List<String> listDirectRemoteIdsForProfile(String profileId, String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Remote> remote = query.from(Remote.class);
        Join<Remote, RemoteHealthProfile> profile = remote.join(Remote_.healthProfile);
        List<Predicate> predicates = activeAfter(cb, remote, afterId);
        predicates.add(cb.equal(profile.get(Baseclass_.id), profileId));
        return ids(query, remote, predicates, limit);
    }

    public List<String> listInheritedDeviceIdsForProfile(String profileId, String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Device> device = query.from(Device.class);
        Join<Device, DeviceType> deviceType = device.join(Device_.deviceType);
        Join<DeviceType, RemoteHealthProfile> profile = deviceType.join(DeviceType_.defaultHealthProfile);
        List<Predicate> predicates = activeAfter(cb, device, afterId);
        predicates.add(cb.isNull(device.get(Remote_.healthProfile)));
        predicates.add(cb.equal(profile.get(Baseclass_.id), profileId));
        return ids(query, device, predicates, limit);
    }

    public List<String> listInheritedDeviceIdsForDeviceType(String deviceTypeId, String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Device> device = query.from(Device.class);
        Join<Device, DeviceType> deviceType = device.join(Device_.deviceType);
        List<Predicate> predicates = activeAfter(cb, device, afterId);
        predicates.add(cb.isNull(device.get(Remote_.healthProfile)));
        predicates.add(cb.equal(deviceType.get(Baseclass_.id), deviceTypeId));
        return ids(query, device, predicates, limit);
    }

    public List<String> listStaleDirectRemoteIds(String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Remote> remote = query.from(Remote.class);
        Join<Remote, RemoteHealthProfile> profile = remote.join(Remote_.healthProfile);
        List<Predicate> predicates = activeAfter(cb, remote, afterId);
        predicates.add(staleRemoteProjection(cb, remote, profile));
        return ids(query, remote, predicates, limit);
    }

    public List<String> listStaleInheritedDeviceIds(String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Device> device = query.from(Device.class);
        Join<Device, DeviceType> deviceType = device.join(Device_.deviceType);
        Join<DeviceType, RemoteHealthProfile> profile = deviceType.join(DeviceType_.defaultHealthProfile);
        List<Predicate> predicates = activeAfter(cb, device, afterId);
        predicates.add(cb.isNull(device.get(Remote_.healthProfile)));
        predicates.add(staleRemoteProjection(cb, device, profile));
        return ids(query, device, predicates, limit);
    }

    public List<String> listUnconfiguredDeviceIdsWithProjection(String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Device> device = query.from(Device.class);
        Join<Device, DeviceType> deviceType = device.join(Device_.deviceType, JoinType.LEFT);
        List<Predicate> predicates = activeAfter(cb, device, afterId);
        predicates.add(cb.isNull(device.get(Remote_.healthProfile)));
        predicates.add(cb.or(
                cb.isNull(device.get(Device_.deviceType)),
                cb.isNull(deviceType.get(DeviceType_.defaultHealthProfile))));
        predicates.add(hasRemoteProjection(cb, device));
        return ids(query, device, predicates, limit);
    }

    public List<String> listUnconfiguredGatewayIdsWithProjection(String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Gateway> gateway = query.from(Gateway.class);
        List<Predicate> predicates = activeAfter(cb, gateway, afterId);
        predicates.add(cb.isNull(gateway.get(Remote_.healthProfile)));
        predicates.add(hasRemoteProjection(cb, gateway));
        return ids(query, gateway, predicates, limit);
    }

    public List<String> listGroupIdsForPolicy(String policyId, String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<RemoteGroup> group = query.from(RemoteGroup.class);
        Join<RemoteGroup, FleetHealthPolicy> policy = group.join(RemoteGroup_.fleetHealthPolicy);
        List<Predicate> predicates = activeAfter(cb, group, afterId);
        predicates.add(cb.equal(policy.get(Baseclass_.id), policyId));
        return ids(query, group, predicates, limit);
    }

    public List<String> listStaleGroupIds(String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<RemoteGroup> group = query.from(RemoteGroup.class);
        Join<RemoteGroup, FleetHealthPolicy> policy = group.join(RemoteGroup_.fleetHealthPolicy);
        List<Predicate> predicates = activeAfter(cb, group, afterId);
        predicates.add(cb.isTrue(group.get(RemoteGroup_.healthEnabled)));
        predicates.add(cb.or(
                cb.isNull(group.get(RemoteGroup_.healthCalculatedAt)),
                cb.isNull(group.get(RemoteGroup_.evaluatedFleetHealthPolicyId)),
                cb.notEqual(group.get(RemoteGroup_.evaluatedFleetHealthPolicyId), policy.get(Baseclass_.id)),
                cb.isNull(group.get(RemoteGroup_.fleetHealthEvaluationVersion)),
                cb.notEqual(group.get(RemoteGroup_.fleetHealthEvaluationVersion), policy.get(FleetHealthPolicy_.evaluationVersion)),
                cb.isNull(group.get(RemoteGroup_.evaluatedHealthInputVersion)),
                cb.notEqual(group.get(RemoteGroup_.evaluatedHealthInputVersion), group.get(RemoteGroup_.healthInputVersion))));
        return ids(query, group, predicates, limit);
    }

    public List<String> listDueRemoteHealthIds(OffsetDateTime dueAt, String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Remote> remote = query.from(Remote.class);
        List<Predicate> predicates = activeAfter(cb, remote, afterId);
        predicates.add(cb.isNotNull(remote.get(Remote_.nextHealthEvaluationAt)));
        predicates.add(cb.lessThanOrEqualTo(remote.get(Remote_.nextHealthEvaluationAt), dueAt));
        return ids(query, remote, predicates, limit);
    }

    public List<String> listDueGroupHealthIds(OffsetDateTime dueAt, String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<RemoteGroup> group = query.from(RemoteGroup.class);
        List<Predicate> predicates = activeAfter(cb, group, afterId);
        predicates.add(cb.isNotNull(group.get(RemoteGroup_.nextHealthEvaluationAt)));
        predicates.add(cb.lessThanOrEqualTo(group.get(RemoteGroup_.nextHealthEvaluationAt), dueAt));
        return ids(query, group, predicates, limit);
    }

    public List<String> listDisabledOrUnconfiguredGroupIdsWithProjection(String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<RemoteGroup> group = query.from(RemoteGroup.class);
        List<Predicate> predicates = activeAfter(cb, group, afterId);
        predicates.add(cb.or(
                cb.isFalse(group.get(RemoteGroup_.healthEnabled)),
                cb.isNull(group.get(RemoteGroup_.fleetHealthPolicy))));
        predicates.add(cb.or(
                cb.isNotNull(group.get(RemoteGroup_.evaluatedFleetHealthPolicyId)),
                cb.isNotNull(group.get(RemoteGroup_.currentSeverityName)),
                cb.isNotNull(group.get(RemoteGroup_.currentSeverityValue)),
                cb.isNotNull(group.get(RemoteGroup_.currentSeverityRuleId)),
                cb.isTrue(group.get(RemoteGroup_.humanInterventionRequired)),
                cb.isNotNull(group.get(RemoteGroup_.currentPopulationCount))));
        return ids(query, group, predicates, limit);
    }

    private <T extends Remote> Predicate staleRemoteProjection(CriteriaBuilder cb,
                                                                 From<?, T> remote,
                                                                 Join<?, RemoteHealthProfile> profile) {
        return cb.or(
                cb.isNull(remote.get(Remote_.healthCalculatedAt)),
                cb.isNull(remote.get(Remote_.evaluatedHealthProfileId)),
                cb.notEqual(remote.get(Remote_.evaluatedHealthProfileId), profile.get(Baseclass_.id)),
                cb.isNull(remote.get(Remote_.healthEvaluationVersion)),
                cb.notEqual(remote.get(Remote_.healthEvaluationVersion), profile.get(RemoteHealthProfile_.evaluationVersion)));
    }

    private <T extends Remote> Predicate hasRemoteProjection(CriteriaBuilder cb, From<?, T> remote) {
        return cb.or(
                cb.isNotNull(remote.get(Remote_.evaluatedHealthProfileId)),
                cb.isNotNull(remote.get(Remote_.currentSeverityName)),
                cb.isNotNull(remote.get(Remote_.currentSeverityValue)),
                cb.isNotNull(remote.get(Remote_.currentSeverityRuleId)),
                cb.isTrue(remote.get(Remote_.humanInterventionRequired)));
    }

    private <T extends Baseclass> List<Predicate> activeAfter(CriteriaBuilder cb, Root<T> root, String afterId) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(root.get(Baseclass_.softDelete)));
        if (afterId != null && !afterId.isBlank()) {
            predicates.add(cb.greaterThan(root.get(Baseclass_.id), afterId));
        }
        return predicates;
    }

    private <T extends Baseclass> List<String> ids(CriteriaQuery<String> query,
                                 Root<T> root,
                                 List<Predicate> predicates,
                                 int limit) {
        query.select(root.get(Baseclass_.id))
                .where(predicates.toArray(Predicate[]::new))
                .orderBy(em.getCriteriaBuilder().asc(root.get(Baseclass_.id)));
        TypedQuery<String> typedQuery = em.createQuery(query);
        typedQuery.setMaxResults(Math.max(1, limit));
        return typedQuery.getResultList();
    }
}
