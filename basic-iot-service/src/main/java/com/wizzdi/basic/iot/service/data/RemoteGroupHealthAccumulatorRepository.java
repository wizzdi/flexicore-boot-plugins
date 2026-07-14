package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass_;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroupHealthAccumulator;
import com.wizzdi.basic.iot.model.RemoteGroupHealthAccumulator_;
import com.wizzdi.basic.iot.model.RemoteGroupMemberHealthState;
import com.wizzdi.basic.iot.model.RemoteGroupMemberHealthState_;
import com.wizzdi.basic.iot.model.RemoteGroupSeverityBucket;
import com.wizzdi.basic.iot.model.RemoteGroupSeverityBucket_;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote_;
import com.wizzdi.basic.iot.model.RemoteGroup_;
import com.wizzdi.basic.iot.model.RemoteRoleDefinition_;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Extension
@Component
public class RemoteGroupHealthAccumulatorRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public RemoteGroupHealthAccumulator findAccumulator(String groupId, String roleId, boolean requiredOnly) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupHealthAccumulator> q = cb.createQuery(RemoteGroupHealthAccumulator.class);
        Root<RemoteGroupHealthAccumulator> r = q.from(RemoteGroupHealthAccumulator.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(r.get(RemoteGroupHealthAccumulator_.remoteGroup).get(RemoteGroup_.id), groupId));
        predicates.add(cb.equal(r.get(RemoteGroupHealthAccumulator_.requiredMembersOnly), requiredOnly));
        predicates.add(cb.isFalse(r.get(Baseclass_.softDelete)));
        if (roleId == null) {
            predicates.add(cb.isNull(r.get(RemoteGroupHealthAccumulator_.role)));
        } else {
            predicates.add(cb.equal(r.get(RemoteGroupHealthAccumulator_.role).get(RemoteRoleDefinition_.id), roleId));
        }
        q.select(r).where(predicates.toArray(Predicate[]::new));
        List<RemoteGroupHealthAccumulator> result = em.createQuery(q).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<RemoteGroupHealthAccumulator> listAccumulators(String groupId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupHealthAccumulator> q = cb.createQuery(RemoteGroupHealthAccumulator.class);
        Root<RemoteGroupHealthAccumulator> r = q.from(RemoteGroupHealthAccumulator.class);
        q.select(r).where(
                cb.equal(r.get(RemoteGroupHealthAccumulator_.remoteGroup).get(RemoteGroup_.id), groupId),
                cb.isFalse(r.get(Baseclass_.softDelete))
        );
        return em.createQuery(q).getResultList();
    }

    public List<RemoteGroupSeverityBucket> listBuckets(String accumulatorId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupSeverityBucket> q = cb.createQuery(RemoteGroupSeverityBucket.class);
        Root<RemoteGroupSeverityBucket> r = q.from(RemoteGroupSeverityBucket.class);
        q.select(r).where(
                cb.equal(r.get(RemoteGroupSeverityBucket_.accumulator)
                        .get(RemoteGroupHealthAccumulator_.id), accumulatorId),
                cb.isFalse(r.get(Baseclass_.softDelete))
        ).orderBy(cb.asc(r.get(RemoteGroupSeverityBucket_.severityValue)));
        return em.createQuery(q).getResultList();
    }

    public RemoteGroupMemberHealthState findMemberStateByMembership(String membershipId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupMemberHealthState> q = cb.createQuery(RemoteGroupMemberHealthState.class);
        Root<RemoteGroupMemberHealthState> r = q.from(RemoteGroupMemberHealthState.class);
        q.select(r).where(
                cb.equal(r.get(RemoteGroupMemberHealthState_.remoteGroupToRemote)
                        .get(RemoteGroupToRemote_.id), membershipId),
                cb.isFalse(r.get(Baseclass_.softDelete))
        );
        List<RemoteGroupMemberHealthState> result = em.createQuery(q).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<RemoteGroupMemberHealthState> listMemberStates(String groupId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupMemberHealthState> q = cb.createQuery(RemoteGroupMemberHealthState.class);
        Root<RemoteGroupMemberHealthState> r = q.from(RemoteGroupMemberHealthState.class);
        q.select(r).where(
                cb.equal(r.get(RemoteGroupMemberHealthState_.remoteGroup).get(RemoteGroup_.id), groupId),
                cb.isFalse(r.get(Baseclass_.softDelete))
        );
        return em.createQuery(q).getResultList();
    }

    public List<RemoteGroupMemberHealthState> listActiveMemberStates(String groupId, String roleId, boolean requiredOnly) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupMemberHealthState> q = cb.createQuery(RemoteGroupMemberHealthState.class);
        Root<RemoteGroupMemberHealthState> r = q.from(RemoteGroupMemberHealthState.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(r.get(RemoteGroupMemberHealthState_.remoteGroup).get(RemoteGroup_.id), groupId));
        predicates.add(cb.isTrue(r.get(RemoteGroupMemberHealthState_.active)));
        predicates.add(cb.isFalse(r.get(Baseclass_.softDelete)));
        if (requiredOnly) {
            predicates.add(cb.isTrue(r.get(RemoteGroupMemberHealthState_.requiredMember)));
        }
        if (roleId != null) {
            predicates.add(cb.equal(r.get(RemoteGroupMemberHealthState_.role)
                    .get(RemoteRoleDefinition_.id), roleId));
        }
        q.select(r).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getResultList();
    }

    public List<RemoteGroupMemberHealthState> listMemberStatesByRemote(String remoteId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteGroupMemberHealthState> q = cb.createQuery(RemoteGroupMemberHealthState.class);
        Root<RemoteGroupMemberHealthState> r = q.from(RemoteGroupMemberHealthState.class);
        q.select(r).where(
                cb.equal(r.get(RemoteGroupMemberHealthState_.remote).get(Remote_.id), remoteId),
                cb.isFalse(r.get(Baseclass_.softDelete))
        );
        return em.createQuery(q).getResultList();
    }

    public List<String> listHealthEnabledGroupIds(String afterId, int limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> q = cb.createQuery(String.class);
        Root<RemoteGroup> r = q.from(RemoteGroup.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isTrue(r.get(RemoteGroup_.healthEnabled)));
        predicates.add(cb.isFalse(r.get(Baseclass_.softDelete)));
        if (afterId != null) {
            predicates.add(cb.greaterThan(r.get(RemoteGroup_.id), afterId));
        }
        q.select(r.get(RemoteGroup_.id)).where(predicates.toArray(Predicate[]::new))
                .orderBy(cb.asc(r.get(RemoteGroup_.id)));
        return em.createQuery(q).setMaxResults(Math.max(1, limit)).getResultList();
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return id == null ? null : em.find(type, id);
    }

    @Transactional
    public void merge(Object entity) {
        securedBasicRepository.merge(entity);
    }

    @Transactional
    public void massMerge(Collection<?> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        securedBasicRepository.massMerge(new ArrayList<>(entities));
    }
}
