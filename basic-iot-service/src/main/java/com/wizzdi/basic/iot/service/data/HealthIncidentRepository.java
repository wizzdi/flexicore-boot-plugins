package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.flexicore.model.SecurityUser_;
import com.wizzdi.basic.iot.model.HealthIncident;
import com.wizzdi.basic.iot.model.HealthIncidentAction;
import com.wizzdi.basic.iot.model.HealthIncidentAction_;
import com.wizzdi.basic.iot.model.HealthIncidentStatus;
import com.wizzdi.basic.iot.model.HealthIncident_;
import com.wizzdi.basic.iot.model.RemoteGroup_;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.basic.iot.service.request.HealthIncidentActionFilter;
import com.wizzdi.basic.iot.service.request.HealthIncidentFilter;
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
import java.util.Set;

@Extension
@Component
public class HealthIncidentRepository implements Plugin {
    private static final Set<HealthIncidentStatus> ACTIVE_STATUSES = Set.of(
            HealthIncidentStatus.OPEN,
            HealthIncidentStatus.ACKNOWLEDGED,
            HealthIncidentStatus.IN_PROGRESS);

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public HealthIncident findActiveForRemote(String remoteId) {
        if (remoteId == null) return null;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthIncident> q = cb.createQuery(HealthIncident.class);
        Root<HealthIncident> r = q.from(HealthIncident.class);
        q.select(r).where(
                cb.equal(r.get(HealthIncident_.remote).get(Remote_.id), remoteId),
                r.get(HealthIncident_.status).in(ACTIVE_STATUSES),
                cb.isFalse(r.get(Baseclass_.softDelete)))
                .orderBy(cb.desc(r.get(HealthIncident_.openedAt)));
        List<HealthIncident> result = em.createQuery(q).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public HealthIncident findActiveForGroup(String groupId) {
        if (groupId == null) return null;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthIncident> q = cb.createQuery(HealthIncident.class);
        Root<HealthIncident> r = q.from(HealthIncident.class);
        q.select(r).where(
                cb.equal(r.get(HealthIncident_.remoteGroup).get(RemoteGroup_.id), groupId),
                r.get(HealthIncident_.status).in(ACTIVE_STATUSES),
                cb.isFalse(r.get(Baseclass_.softDelete)))
                .orderBy(cb.desc(r.get(HealthIncident_.openedAt)));
        List<HealthIncident> result = em.createQuery(q).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<HealthIncident> list(SecurityContext securityContext, HealthIncidentFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthIncident> q = cb.createQuery(HealthIncident.class);
        Root<HealthIncident> r = q.from(HealthIncident.class);
        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new))
                .orderBy(cb.desc(r.get(HealthIncident_.openedAt)));
        TypedQuery<HealthIncident> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long count(SecurityContext securityContext, HealthIncidentFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<HealthIncident> r = q.from(HealthIncident.class);
        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private <T extends HealthIncident> void addPredicates(HealthIncidentFilter filter,
                                                           CriteriaBuilder cb,
                                                           CommonAbstractCriteria q,
                                                           From<?, T> r,
                                                           List<Predicate> predicates,
                                                           SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getHealthIncidentIds() != null && !filter.getHealthIncidentIds().isEmpty()) predicates.add(r.get(HealthIncident_.id).in(filter.getHealthIncidentIds()));
        if (filter.getRemoteIds() != null && !filter.getRemoteIds().isEmpty()) predicates.add(r.get(HealthIncident_.remote).get(Remote_.id).in(filter.getRemoteIds()));
        if (filter.getRemoteGroupIds() != null && !filter.getRemoteGroupIds().isEmpty()) predicates.add(r.get(HealthIncident_.remoteGroup).get(RemoteGroup_.id).in(filter.getRemoteGroupIds()));
        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) predicates.add(r.get(HealthIncident_.status).in(filter.getStatuses()));
        if (filter.getAssignedUserIds() != null && !filter.getAssignedUserIds().isEmpty()) predicates.add(r.get(HealthIncident_.assignedTo).get(SecurityUser_.id).in(filter.getAssignedUserIds()));
        if (filter.getMinimumSeverityValue() != null) predicates.add(cb.greaterThanOrEqualTo(r.get(HealthIncident_.severityValue), filter.getMinimumSeverityValue()));
        if (filter.getActionRequired() != null) predicates.add(cb.equal(r.get(HealthIncident_.actionRequired), filter.getActionRequired()));
        if (filter.getHealthRecovered() != null) predicates.add(cb.equal(r.get(HealthIncident_.healthRecovered), filter.getHealthRecovered()));
        if (filter.getOpenedAfter() != null) predicates.add(cb.greaterThanOrEqualTo(r.get(HealthIncident_.openedAt), filter.getOpenedAfter()));
        if (filter.getOpenedBefore() != null) predicates.add(cb.lessThan(r.get(HealthIncident_.openedAt), filter.getOpenedBefore()));
    }

    public List<HealthIncidentAction> listActions(SecurityContext securityContext, HealthIncidentActionFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthIncidentAction> q = cb.createQuery(HealthIncidentAction.class);
        Root<HealthIncidentAction> r = q.from(HealthIncidentAction.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getHealthIncidentIds() != null && !filter.getHealthIncidentIds().isEmpty()) predicates.add(r.get(HealthIncidentAction_.healthIncident).get(HealthIncident_.id).in(filter.getHealthIncidentIds()));
        if (filter.getActionTypes() != null && !filter.getActionTypes().isEmpty()) predicates.add(r.get(HealthIncidentAction_.actionType).in(filter.getActionTypes()));
        if (filter.getPerformedByUserIds() != null && !filter.getPerformedByUserIds().isEmpty()) predicates.add(r.get(HealthIncidentAction_.performedBy).get(SecurityUser_.id).in(filter.getPerformedByUserIds()));
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.desc(r.get(HealthIncidentAction_.performedAt)));
        TypedQuery<HealthIncidentAction> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long countActions(SecurityContext securityContext, HealthIncidentActionFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<HealthIncidentAction> r = q.from(HealthIncidentAction.class);
        List<Predicate> predicates = new ArrayList<>();
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getHealthIncidentIds() != null && !filter.getHealthIncidentIds().isEmpty()) predicates.add(r.get(HealthIncidentAction_.healthIncident).get(HealthIncident_.id).in(filter.getHealthIncidentIds()));
        if (filter.getActionTypes() != null && !filter.getActionTypes().isEmpty()) predicates.add(r.get(HealthIncidentAction_.actionType).in(filter.getActionTypes()));
        if (filter.getPerformedByUserIds() != null && !filter.getPerformedByUserIds().isEmpty()) predicates.add(r.get(HealthIncidentAction_.performedBy).get(SecurityUser_.id).in(filter.getPerformedByUserIds()));
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> type, SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, type, securityContext);
    }

    @Transactional
    public void massMerge(Collection<?> entities) {
        if (entities != null && !entities.isEmpty()) securedBasicRepository.massMerge(new ArrayList<>(entities));
    }
}
