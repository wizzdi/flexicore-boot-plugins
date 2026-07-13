package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.wizzdi.basic.iot.model.FleetHealthPolicy_;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote_;
import com.wizzdi.basic.iot.model.RemoteGroup_;
import com.wizzdi.basic.iot.model.RemoteRoleDefinition_;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.basic.iot.service.request.RemoteGroupFilter;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteFilter;
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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Extension
@Component
public class RemoteGroupRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<RemoteGroup> listGroups(SecurityContext securityContext, RemoteGroupFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroup> q = cb.createQuery(RemoteGroup.class);
        Root<RemoteGroup> r = q.from(RemoteGroup.class);
        List<Predicate> predicates = new ArrayList<>();
        addGroupPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Baseclass_.name)));
        TypedQuery<RemoteGroup> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long countGroups(SecurityContext securityContext, RemoteGroupFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<RemoteGroup> r = q.from(RemoteGroup.class);
        List<Predicate> predicates = new ArrayList<>();
        addGroupPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private <T extends RemoteGroup> void addGroupPredicates(RemoteGroupFilter filter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getRemoteGroupIds() != null && !filter.getRemoteGroupIds().isEmpty()) {
            predicates.add(r.get(RemoteGroup_.id).in(filter.getRemoteGroupIds()));
        }
        if (filter.getExternalIds() != null && !filter.getExternalIds().isEmpty()) {
            predicates.add(r.get(RemoteGroup_.externalId).in(filter.getExternalIds()));
        }
        if (filter.getFleetHealthPolicyIds() != null && !filter.getFleetHealthPolicyIds().isEmpty()) {
            predicates.add(r.get(RemoteGroup_.fleetHealthPolicy).get(FleetHealthPolicy_.id).in(filter.getFleetHealthPolicyIds()));
        }
        if (filter.getHealthEnabled() != null) {
            predicates.add(cb.equal(r.get(RemoteGroup_.healthEnabled), filter.getHealthEnabled()));
        }
    }

    public List<RemoteGroupToRemote> listMemberships(SecurityContext securityContext, RemoteGroupToRemoteFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupToRemote> q = cb.createQuery(RemoteGroupToRemote.class);
        Root<RemoteGroupToRemote> r = q.from(RemoteGroupToRemote.class);
        List<Predicate> predicates = new ArrayList<>();
        addMembershipPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Baseclass_.name)));
        TypedQuery<RemoteGroupToRemote> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public List<RemoteGroupToRemote> listAllMemberships(SecurityContext securityContext, RemoteGroupToRemoteFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupToRemote> q = cb.createQuery(RemoteGroupToRemote.class);
        Root<RemoteGroupToRemote> r = q.from(RemoteGroupToRemote.class);
        List<Predicate> predicates = new ArrayList<>();
        addMembershipPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Baseclass_.name)));
        return em.createQuery(q).getResultList();
    }

    public long countMemberships(SecurityContext securityContext, RemoteGroupToRemoteFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<RemoteGroupToRemote> r = q.from(RemoteGroupToRemote.class);
        List<Predicate> predicates = new ArrayList<>();
        addMembershipPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private <T extends RemoteGroupToRemote> void addMembershipPredicates(RemoteGroupToRemoteFilter filter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getRemoteGroupToRemoteIds() != null && !filter.getRemoteGroupToRemoteIds().isEmpty()) {
            predicates.add(r.get(RemoteGroupToRemote_.id).in(filter.getRemoteGroupToRemoteIds()));
        }
        if (filter.getRemoteGroupIds() != null && !filter.getRemoteGroupIds().isEmpty()) {
            predicates.add(r.get(RemoteGroupToRemote_.remoteGroup).get(RemoteGroup_.id).in(filter.getRemoteGroupIds()));
        }
        if (filter.getRemoteIds() != null && !filter.getRemoteIds().isEmpty()) {
            predicates.add(r.get(RemoteGroupToRemote_.remote).get(Remote_.id).in(filter.getRemoteIds()));
        }
        if (filter.getRoleIds() != null && !filter.getRoleIds().isEmpty()) {
            predicates.add(r.get(RemoteGroupToRemote_.role).get(RemoteRoleDefinition_.id).in(filter.getRoleIds()));
        }
        if (filter.getMembershipActions() != null && !filter.getMembershipActions().isEmpty()) {
            predicates.add(r.get(RemoteGroupToRemote_.membershipAction).in(filter.getMembershipActions()));
        }
        OffsetDateTime activeAt = filter.getActiveAt();
        if (activeAt != null) {
            predicates.add(cb.or(
                    cb.isNull(r.get(RemoteGroupToRemote_.activeFrom)),
                    cb.lessThanOrEqualTo(r.get(RemoteGroupToRemote_.activeFrom), activeAt)
            ));
            predicates.add(cb.or(
                    cb.isNull(r.get(RemoteGroupToRemote_.activeUntil)),
                    cb.greaterThan(r.get(RemoteGroupToRemote_.activeUntil), activeAt)
            ));
        }
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> type, SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, type, securityContext);
    }

    @Transactional
    public void merge(Object entity) {
        securedBasicRepository.merge(entity);
    }
}
