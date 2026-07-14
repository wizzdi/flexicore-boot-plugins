package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass_;
import com.wizzdi.basic.iot.model.FleetHealthPolicy_;
import com.wizzdi.basic.iot.model.FleetHealthRule_;
import com.wizzdi.basic.iot.model.RemoteGroupHealthHistory;
import com.wizzdi.basic.iot.model.RemoteGroupHealthHistory_;
import com.wizzdi.basic.iot.model.RemoteGroupHealthMetricHistory;
import com.wizzdi.basic.iot.model.RemoteGroupHealthMetricHistory_;
import com.wizzdi.basic.iot.model.RemoteGroup_;
import com.wizzdi.basic.iot.model.RemoteHealthHistory;
import com.wizzdi.basic.iot.model.RemoteHealthHistory_;
import com.wizzdi.basic.iot.model.RemoteHealthProfile_;
import com.wizzdi.basic.iot.model.RemoteHealthRule_;
import com.wizzdi.basic.iot.model.RemoteHealthSignalEvidence;
import com.wizzdi.basic.iot.model.RemoteHealthSignalEvidence_;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.basic.iot.service.request.RemoteGroupHealthHistoryFilter;
import com.wizzdi.basic.iot.service.request.RemoteHealthHistoryFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Extension
@Component
public class HealthHistoryRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public RemoteHealthHistory findOpenRemoteHistory(String remoteId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteHealthHistory> q = cb.createQuery(RemoteHealthHistory.class);
        Root<RemoteHealthHistory> r = q.from(RemoteHealthHistory.class);
        q.select(r).where(
                cb.equal(r.get(RemoteHealthHistory_.remote).get(Remote_.id), remoteId),
                cb.isNull(r.get(RemoteHealthHistory_.validUntil)),
                cb.isFalse(r.get(Baseclass_.softDelete))
        ).orderBy(cb.desc(r.get(RemoteHealthHistory_.validFrom)));
        List<RemoteHealthHistory> result = em.createQuery(q).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public RemoteGroupHealthHistory findOpenGroupHistory(String groupId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupHealthHistory> q = cb.createQuery(RemoteGroupHealthHistory.class);
        Root<RemoteGroupHealthHistory> r = q.from(RemoteGroupHealthHistory.class);
        q.select(r).where(
                cb.equal(r.get(RemoteGroupHealthHistory_.remoteGroup).get(RemoteGroup_.id), groupId),
                cb.isNull(r.get(RemoteGroupHealthHistory_.validUntil)),
                cb.isFalse(r.get(Baseclass_.softDelete))
        ).orderBy(cb.desc(r.get(RemoteGroupHealthHistory_.validFrom)));
        List<RemoteGroupHealthHistory> result = em.createQuery(q).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<RemoteHealthHistory> listRemoteHistory(SecurityContext securityContext, RemoteHealthHistoryFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteHealthHistory> q = cb.createQuery(RemoteHealthHistory.class);
        Root<RemoteHealthHistory> r = q.from(RemoteHealthHistory.class);
        List<Predicate> predicates = new ArrayList<>();
        addRemotePredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new))
                .orderBy(cb.desc(r.get(RemoteHealthHistory_.validFrom)));
        TypedQuery<RemoteHealthHistory> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long countRemoteHistory(SecurityContext securityContext, RemoteHealthHistoryFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<RemoteHealthHistory> r = q.from(RemoteHealthHistory.class);
        List<Predicate> predicates = new ArrayList<>();
        addRemotePredicates(filter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private <T extends RemoteHealthHistory> void addRemotePredicates(RemoteHealthHistoryFilter filter,
                                                                      CriteriaBuilder cb,
                                                                      CommonAbstractCriteria q,
                                                                      From<?, T> r,
                                                                      List<Predicate> predicates,
                                                                      SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(
                filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getRemoteHealthHistoryIds() != null && !filter.getRemoteHealthHistoryIds().isEmpty()) {
            predicates.add(r.get(RemoteHealthHistory_.id).in(filter.getRemoteHealthHistoryIds()));
        }
        if (filter.getRemoteIds() != null && !filter.getRemoteIds().isEmpty()) {
            predicates.add(r.get(RemoteHealthHistory_.remote).get(Remote_.id).in(filter.getRemoteIds()));
        }
        if (filter.getRemoteHealthProfileIds() != null && !filter.getRemoteHealthProfileIds().isEmpty()) {
            predicates.add(r.get(RemoteHealthHistory_.remoteHealthProfile)
                    .get(RemoteHealthProfile_.id).in(filter.getRemoteHealthProfileIds()));
        }
        if (filter.getMatchedRuleIds() != null && !filter.getMatchedRuleIds().isEmpty()) {
            predicates.add(r.get(RemoteHealthHistory_.matchedRule)
                    .get(RemoteHealthRule_.id).in(filter.getMatchedRuleIds()));
        }
        if (filter.getMinimumSeverityValue() != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                    r.get(RemoteHealthHistory_.severityValue), filter.getMinimumSeverityValue()));
        }
        if (filter.getMaximumSeverityValue() != null) {
            predicates.add(cb.lessThanOrEqualTo(
                    r.get(RemoteHealthHistory_.severityValue), filter.getMaximumSeverityValue()));
        }
        if (filter.getHumanInterventionRequired() != null) {
            predicates.add(cb.equal(r.get(RemoteHealthHistory_.humanInterventionRequired),
                    filter.getHumanInterventionRequired()));
        }
        if (filter.getIntervalStart() != null) {
            predicates.add(cb.or(
                    cb.isNull(r.get(RemoteHealthHistory_.validUntil)),
                    cb.greaterThan(r.get(RemoteHealthHistory_.validUntil), filter.getIntervalStart())));
        }
        if (filter.getIntervalEnd() != null) {
            predicates.add(cb.lessThan(r.get(RemoteHealthHistory_.validFrom), filter.getIntervalEnd()));
        }
    }

    public List<RemoteGroupHealthHistory> listGroupHistory(SecurityContext securityContext,
                                                            RemoteGroupHealthHistoryFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupHealthHistory> q = cb.createQuery(RemoteGroupHealthHistory.class);
        Root<RemoteGroupHealthHistory> r = q.from(RemoteGroupHealthHistory.class);
        List<Predicate> predicates = new ArrayList<>();
        addGroupPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new))
                .orderBy(cb.desc(r.get(RemoteGroupHealthHistory_.validFrom)));
        TypedQuery<RemoteGroupHealthHistory> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long countGroupHistory(SecurityContext securityContext, RemoteGroupHealthHistoryFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<RemoteGroupHealthHistory> r = q.from(RemoteGroupHealthHistory.class);
        List<Predicate> predicates = new ArrayList<>();
        addGroupPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private <T extends RemoteGroupHealthHistory> void addGroupPredicates(RemoteGroupHealthHistoryFilter filter,
                                                                          CriteriaBuilder cb,
                                                                          CommonAbstractCriteria q,
                                                                          From<?, T> r,
                                                                          List<Predicate> predicates,
                                                                          SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(
                filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getRemoteGroupHealthHistoryIds() != null && !filter.getRemoteGroupHealthHistoryIds().isEmpty()) {
            predicates.add(r.get(RemoteGroupHealthHistory_.id).in(filter.getRemoteGroupHealthHistoryIds()));
        }
        if (filter.getRemoteGroupIds() != null && !filter.getRemoteGroupIds().isEmpty()) {
            predicates.add(r.get(RemoteGroupHealthHistory_.remoteGroup)
                    .get(RemoteGroup_.id).in(filter.getRemoteGroupIds()));
        }
        if (filter.getFleetHealthPolicyIds() != null && !filter.getFleetHealthPolicyIds().isEmpty()) {
            predicates.add(r.get(RemoteGroupHealthHistory_.fleetHealthPolicy)
                    .get(FleetHealthPolicy_.id).in(filter.getFleetHealthPolicyIds()));
        }
        if (filter.getMatchedRuleIds() != null && !filter.getMatchedRuleIds().isEmpty()) {
            predicates.add(r.get(RemoteGroupHealthHistory_.matchedRule)
                    .get(FleetHealthRule_.id).in(filter.getMatchedRuleIds()));
        }
        if (filter.getMinimumSeverityValue() != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                    r.get(RemoteGroupHealthHistory_.severityValue), filter.getMinimumSeverityValue()));
        }
        if (filter.getMaximumSeverityValue() != null) {
            predicates.add(cb.lessThanOrEqualTo(
                    r.get(RemoteGroupHealthHistory_.severityValue), filter.getMaximumSeverityValue()));
        }
        if (filter.getHumanInterventionRequired() != null) {
            predicates.add(cb.equal(r.get(RemoteGroupHealthHistory_.humanInterventionRequired),
                    filter.getHumanInterventionRequired()));
        }
        if (filter.getIntervalStart() != null) {
            predicates.add(cb.or(
                    cb.isNull(r.get(RemoteGroupHealthHistory_.validUntil)),
                    cb.greaterThan(r.get(RemoteGroupHealthHistory_.validUntil), filter.getIntervalStart())));
        }
        if (filter.getIntervalEnd() != null) {
            predicates.add(cb.lessThan(r.get(RemoteGroupHealthHistory_.validFrom), filter.getIntervalEnd()));
        }
    }

    public List<RemoteHealthSignalEvidence> listSignalEvidence(Collection<String> historyIds) {
        if (historyIds == null || historyIds.isEmpty()) {
            return List.of();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteHealthSignalEvidence> q = cb.createQuery(RemoteHealthSignalEvidence.class);
        Root<RemoteHealthSignalEvidence> r = q.from(RemoteHealthSignalEvidence.class);
        q.select(r).where(
                r.get(RemoteHealthSignalEvidence_.remoteHealthHistory)
                        .get(RemoteHealthHistory_.id).in(historyIds),
                cb.isFalse(r.get(Baseclass_.softDelete))
        ).orderBy(
                cb.asc(r.get(RemoteHealthSignalEvidence_.remoteHealthHistory).get(RemoteHealthHistory_.id)),
                cb.asc(r.get(RemoteHealthSignalEvidence_.priority)));
        return em.createQuery(q).getResultList();
    }

    public List<RemoteGroupHealthMetricHistory> listMetrics(Collection<String> historyIds) {
        if (historyIds == null || historyIds.isEmpty()) {
            return List.of();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupHealthMetricHistory> q = cb.createQuery(RemoteGroupHealthMetricHistory.class);
        Root<RemoteGroupHealthMetricHistory> r = q.from(RemoteGroupHealthMetricHistory.class);
        q.select(r).where(
                r.get(RemoteGroupHealthMetricHistory_.remoteGroupHealthHistory)
                        .get(RemoteGroupHealthHistory_.id).in(historyIds),
                cb.isFalse(r.get(Baseclass_.softDelete))
        ).orderBy(cb.asc(r.get(RemoteGroupHealthMetricHistory_.metricKey)));
        return em.createQuery(q).getResultList();
    }

    @Transactional
    public void massMerge(Collection<?> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        securedBasicRepository.massMerge(new ArrayList<>(entities));
    }
}
