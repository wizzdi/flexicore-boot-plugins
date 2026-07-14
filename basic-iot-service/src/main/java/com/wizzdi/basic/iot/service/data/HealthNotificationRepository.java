package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.flexicore.model.SecurityTenant_;
import com.flexicore.model.SecurityUser_;
import com.wizzdi.basic.iot.model.DeviceType_;
import com.wizzdi.basic.iot.model.HealthNotificationChannelPreference;
import com.wizzdi.basic.iot.model.HealthNotificationChannelPreference_;
import com.wizzdi.basic.iot.model.HealthNotificationDelivery;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryStatus;
import com.wizzdi.basic.iot.model.HealthNotificationDelivery_;
import com.wizzdi.basic.iot.model.HealthNotificationOutbox_;
import com.wizzdi.basic.iot.model.HealthNotificationPolicy;
import com.wizzdi.basic.iot.model.HealthNotificationPolicy_;
import com.wizzdi.basic.iot.model.HealthNotificationScopeType;
import com.wizzdi.basic.iot.model.RemoteGroup_;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.basic.iot.service.request.HealthNotificationDeliveryFilter;
import com.wizzdi.basic.iot.service.request.HealthNotificationPolicyFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Extension
@Component
public class HealthNotificationRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<HealthNotificationPolicy> listPolicies(SecurityContext securityContext, HealthNotificationPolicyFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthNotificationPolicy> q = cb.createQuery(HealthNotificationPolicy.class);
        Root<HealthNotificationPolicy> r = q.from(HealthNotificationPolicy.class);
        List<Predicate> predicates = new ArrayList<>();
        addPolicyPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Baseclass_.name)));
        TypedQuery<HealthNotificationPolicy> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long countPolicies(SecurityContext securityContext, HealthNotificationPolicyFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<HealthNotificationPolicy> r = q.from(HealthNotificationPolicy.class);
        List<Predicate> predicates = new ArrayList<>();
        addPolicyPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private <T extends HealthNotificationPolicy> void addPolicyPredicates(HealthNotificationPolicyFilter filter,
                                                                           CriteriaBuilder cb,
                                                                           CommonAbstractCriteria q,
                                                                           From<?, T> r,
                                                                           List<Predicate> predicates,
                                                                           SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getHealthNotificationPolicyIds() != null && !filter.getHealthNotificationPolicyIds().isEmpty()) predicates.add(r.get(HealthNotificationPolicy_.id).in(filter.getHealthNotificationPolicyIds()));
        if (filter.getUserIds() != null && !filter.getUserIds().isEmpty()) predicates.add(r.get(HealthNotificationPolicy_.user).get(SecurityUser_.id).in(filter.getUserIds()));
        if (filter.getScopeTypes() != null && !filter.getScopeTypes().isEmpty()) predicates.add(r.get(HealthNotificationPolicy_.scopeType).in(filter.getScopeTypes()));
        if (filter.getRemoteIds() != null && !filter.getRemoteIds().isEmpty()) predicates.add(r.get(HealthNotificationPolicy_.remote).get(Remote_.id).in(filter.getRemoteIds()));
        if (filter.getRemoteGroupIds() != null && !filter.getRemoteGroupIds().isEmpty()) predicates.add(r.get(HealthNotificationPolicy_.remoteGroup).get(RemoteGroup_.id).in(filter.getRemoteGroupIds()));
        if (filter.getDeviceTypeIds() != null && !filter.getDeviceTypeIds().isEmpty()) predicates.add(r.get(HealthNotificationPolicy_.deviceType).get(DeviceType_.id).in(filter.getDeviceTypeIds()));
        if (filter.getEnabled() != null) predicates.add(cb.equal(r.get(HealthNotificationPolicy_.enabled), filter.getEnabled()));
    }

    public HealthNotificationPolicy findTenantPolicy(String userId, String tenantId) {
        if (userId == null || tenantId == null) return null;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthNotificationPolicy> q = cb.createQuery(HealthNotificationPolicy.class);
        Root<HealthNotificationPolicy> r = q.from(HealthNotificationPolicy.class);
        q.select(r).where(
                cb.equal(r.get(HealthNotificationPolicy_.user).get(SecurityUser_.id), userId),
                cb.equal(r.get(Baseclass_.tenant).get(SecurityTenant_.id), tenantId),
                cb.equal(r.get(HealthNotificationPolicy_.scopeType), HealthNotificationScopeType.TENANT),
                cb.isFalse(r.get(Baseclass_.softDelete)))
                .orderBy(cb.asc(r.get(Baseclass_.creationDate)));
        List<HealthNotificationPolicy> result = em.createQuery(q).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<HealthNotificationChannelPreference> listPreferences(Collection<String> policyIds) {
        if (policyIds == null || policyIds.isEmpty()) return List.of();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthNotificationChannelPreference> q = cb.createQuery(HealthNotificationChannelPreference.class);
        Root<HealthNotificationChannelPreference> r = q.from(HealthNotificationChannelPreference.class);
        q.select(r).where(
                r.get(HealthNotificationChannelPreference_.healthNotificationPolicy).get(HealthNotificationPolicy_.id).in(policyIds),
                cb.isFalse(r.get(Baseclass_.softDelete)))
                .orderBy(cb.asc(r.get(HealthNotificationChannelPreference_.channel)));
        return em.createQuery(q).getResultList();
    }

    public List<HealthNotificationPolicy> findMatchingPolicies(String tenantId,
                                                                String remoteId,
                                                                String remoteGroupId,
                                                                String deviceTypeId) {
        if (tenantId == null) return List.of();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthNotificationPolicy> q = cb.createQuery(HealthNotificationPolicy.class);
        Root<HealthNotificationPolicy> r = q.from(HealthNotificationPolicy.class);
        List<Predicate> scopes = new ArrayList<>();
        scopes.add(cb.equal(r.get(HealthNotificationPolicy_.scopeType), HealthNotificationScopeType.TENANT));
        if (remoteId != null) scopes.add(cb.and(
                cb.equal(r.get(HealthNotificationPolicy_.scopeType), HealthNotificationScopeType.REMOTE),
                cb.equal(r.get(HealthNotificationPolicy_.remote).get(Remote_.id), remoteId)));
        if (remoteGroupId != null) scopes.add(cb.and(
                cb.equal(r.get(HealthNotificationPolicy_.scopeType), HealthNotificationScopeType.REMOTE_GROUP),
                cb.equal(r.get(HealthNotificationPolicy_.remoteGroup).get(RemoteGroup_.id), remoteGroupId)));
        if (deviceTypeId != null) scopes.add(cb.and(
                cb.equal(r.get(HealthNotificationPolicy_.scopeType), HealthNotificationScopeType.DEVICE_TYPE),
                cb.equal(r.get(HealthNotificationPolicy_.deviceType).get(DeviceType_.id), deviceTypeId)));
        q.select(r).where(
                cb.equal(r.get(Baseclass_.tenant).get(SecurityTenant_.id), tenantId),
                cb.isTrue(r.get(HealthNotificationPolicy_.enabled)),
                cb.isFalse(r.get(Baseclass_.softDelete)),
                cb.or(scopes.toArray(Predicate[]::new)))
                .orderBy(cb.asc(r.get(Baseclass_.name)));
        return em.createQuery(q).getResultList();
    }

    public List<HealthNotificationDelivery> listDeliveries(SecurityContext securityContext, HealthNotificationDeliveryFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthNotificationDelivery> q = cb.createQuery(HealthNotificationDelivery.class);
        Root<HealthNotificationDelivery> r = q.from(HealthNotificationDelivery.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getHealthNotificationDeliveryIds() != null && !filter.getHealthNotificationDeliveryIds().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.id).in(filter.getHealthNotificationDeliveryIds()));
        if (filter.getUserIds() != null && !filter.getUserIds().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.user).get(SecurityUser_.id).in(filter.getUserIds()));
        if (filter.getChannels() != null && !filter.getChannels().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.channel).in(filter.getChannels()));
        if (filter.getDeliveryModes() != null && !filter.getDeliveryModes().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.deliveryMode).in(filter.getDeliveryModes()));
        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.status).in(filter.getStatuses()));
        if (filter.getEventTypes() != null && !filter.getEventTypes().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.healthNotificationOutbox).get(HealthNotificationOutbox_.eventType).in(filter.getEventTypes()));
        if (Boolean.TRUE.equals(filter.getUnreadOnly())) predicates.add(cb.isNull(r.get(HealthNotificationDelivery_.readAt)));
        if (filter.getOccurredAfter() != null) predicates.add(cb.greaterThanOrEqualTo(r.get(HealthNotificationDelivery_.healthNotificationOutbox).get(HealthNotificationOutbox_.occurredAt), filter.getOccurredAfter()));
        if (filter.getOccurredBefore() != null) predicates.add(cb.lessThan(r.get(HealthNotificationDelivery_.healthNotificationOutbox).get(HealthNotificationOutbox_.occurredAt), filter.getOccurredBefore()));
        q.select(r).where(predicates.toArray(Predicate[]::new))
                .orderBy(cb.desc(r.get(HealthNotificationDelivery_.healthNotificationOutbox).get(HealthNotificationOutbox_.occurredAt)));
        TypedQuery<HealthNotificationDelivery> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long countDeliveries(SecurityContext securityContext, HealthNotificationDeliveryFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<HealthNotificationDelivery> r = q.from(HealthNotificationDelivery.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getHealthNotificationDeliveryIds() != null && !filter.getHealthNotificationDeliveryIds().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.id).in(filter.getHealthNotificationDeliveryIds()));
        if (filter.getUserIds() != null && !filter.getUserIds().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.user).get(SecurityUser_.id).in(filter.getUserIds()));
        if (filter.getChannels() != null && !filter.getChannels().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.channel).in(filter.getChannels()));
        if (filter.getDeliveryModes() != null && !filter.getDeliveryModes().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.deliveryMode).in(filter.getDeliveryModes()));
        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.status).in(filter.getStatuses()));
        if (filter.getEventTypes() != null && !filter.getEventTypes().isEmpty()) predicates.add(r.get(HealthNotificationDelivery_.healthNotificationOutbox).get(HealthNotificationOutbox_.eventType).in(filter.getEventTypes()));
        if (Boolean.TRUE.equals(filter.getUnreadOnly())) predicates.add(cb.isNull(r.get(HealthNotificationDelivery_.readAt)));
        if (filter.getOccurredAfter() != null) predicates.add(cb.greaterThanOrEqualTo(r.get(HealthNotificationDelivery_.healthNotificationOutbox).get(HealthNotificationOutbox_.occurredAt), filter.getOccurredAfter()));
        if (filter.getOccurredBefore() != null) predicates.add(cb.lessThan(r.get(HealthNotificationDelivery_.healthNotificationOutbox).get(HealthNotificationOutbox_.occurredAt), filter.getOccurredBefore()));
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    @Transactional
    public List<HealthNotificationDelivery> claimDue(OffsetDateTime now,
                                                       OffsetDateTime staleProcessingBefore,
                                                       int maxResults) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthNotificationDelivery> q = cb.createQuery(HealthNotificationDelivery.class);
        Root<HealthNotificationDelivery> r = q.from(HealthNotificationDelivery.class);
        Predicate retryable = cb.or(
                cb.equal(r.get(HealthNotificationDelivery_.status), HealthNotificationDeliveryStatus.PENDING),
                cb.and(
                        r.get(HealthNotificationDelivery_.status).in(
                                HealthNotificationDeliveryStatus.WAITING_FOR_ADAPTER,
                                HealthNotificationDeliveryStatus.FAILED),
                        cb.isNotNull(r.get(HealthNotificationDelivery_.nextAttemptAt)),
                        cb.lessThanOrEqualTo(r.get(HealthNotificationDelivery_.nextAttemptAt), now)),
                cb.and(
                        cb.equal(r.get(HealthNotificationDelivery_.status), HealthNotificationDeliveryStatus.PROCESSING),
                        cb.isNotNull(r.get(HealthNotificationDelivery_.processingStartedAt)),
                        cb.lessThanOrEqualTo(r.get(HealthNotificationDelivery_.processingStartedAt), staleProcessingBefore)));
        q.select(r).where(
                retryable,
                cb.lessThanOrEqualTo(r.get(HealthNotificationDelivery_.scheduledAt), now),
                cb.isFalse(r.get(Baseclass_.softDelete)))
                .orderBy(cb.asc(r.get(HealthNotificationDelivery_.scheduledAt)), cb.asc(r.get(Baseclass_.creationDate)));
        List<HealthNotificationDelivery> claimed = em.createQuery(q)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(maxResults)
                .getResultList();
        for (HealthNotificationDelivery delivery : claimed) {
            delivery.setStatus(HealthNotificationDeliveryStatus.PROCESSING);
            delivery.setProcessingStartedAt(now);
            delivery.setAttemptCount(delivery.getAttemptCount() + 1);
            delivery.setNextAttemptAt(null);
        }
        em.flush();
        return claimed;
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> type, SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, type, securityContext);
    }

    @Transactional
    public void massMerge(Collection<?> entities) {
        if (entities != null && !entities.isEmpty()) securedBasicRepository.massMerge(new ArrayList<>(entities));
    }
}
