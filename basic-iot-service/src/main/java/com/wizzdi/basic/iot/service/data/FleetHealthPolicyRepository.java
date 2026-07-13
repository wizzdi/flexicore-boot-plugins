package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.wizzdi.basic.iot.model.FleetHealthPolicy;
import com.wizzdi.basic.iot.model.FleetHealthPolicy_;
import com.wizzdi.basic.iot.model.FleetHealthRule;
import com.wizzdi.basic.iot.model.FleetHealthRuleCondition;
import com.wizzdi.basic.iot.model.FleetHealthRuleCondition_;
import com.wizzdi.basic.iot.model.FleetHealthRule_;
import com.wizzdi.basic.iot.service.request.FleetHealthPolicyFilter;
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
import java.util.Collections;
import java.util.List;

@Extension
@Component
public class FleetHealthPolicyRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<FleetHealthPolicy> list(SecurityContext securityContext, FleetHealthPolicyFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FleetHealthPolicy> q = cb.createQuery(FleetHealthPolicy.class);
        Root<FleetHealthPolicy> r = q.from(FleetHealthPolicy.class);
        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Baseclass_.name)));
        TypedQuery<FleetHealthPolicy> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long count(SecurityContext securityContext, FleetHealthPolicyFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<FleetHealthPolicy> r = q.from(FleetHealthPolicy.class);
        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private <T extends FleetHealthPolicy> void addPredicates(FleetHealthPolicyFilter filter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getFleetHealthPolicyIds() != null && !filter.getFleetHealthPolicyIds().isEmpty()) {
            predicates.add(r.get(FleetHealthPolicy_.id).in(filter.getFleetHealthPolicyIds()));
        }
        if (filter.getExternalIds() != null && !filter.getExternalIds().isEmpty()) {
            predicates.add(r.get(FleetHealthPolicy_.externalId).in(filter.getExternalIds()));
        }
        if (filter.getEnabled() != null) {
            predicates.add(cb.equal(r.get(FleetHealthPolicy_.enabled), filter.getEnabled()));
        }
    }

    public List<FleetHealthRule> listRules(String policyId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FleetHealthRule> q = cb.createQuery(FleetHealthRule.class);
        Root<FleetHealthRule> r = q.from(FleetHealthRule.class);
        q.select(r).where(
                cb.equal(r.get(FleetHealthRule_.fleetHealthPolicy).get(FleetHealthPolicy_.id), policyId),
                cb.isFalse(r.get(Baseclass_.softDelete))
        ).orderBy(cb.desc(r.get(FleetHealthRule_.priority)), cb.asc(r.get(Baseclass_.name)));
        return em.createQuery(q).getResultList();
    }

    public List<FleetHealthRuleCondition> listConditions(Collection<String> ruleIds) {
        if (ruleIds == null || ruleIds.isEmpty()) {
            return Collections.emptyList();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FleetHealthRuleCondition> q = cb.createQuery(FleetHealthRuleCondition.class);
        Root<FleetHealthRuleCondition> r = q.from(FleetHealthRuleCondition.class);
        q.select(r).where(
                r.get(FleetHealthRuleCondition_.fleetHealthRule).get(FleetHealthRule_.id).in(ruleIds),
                cb.isFalse(r.get(Baseclass_.softDelete))
        ).orderBy(cb.asc(r.get(Baseclass_.name)));
        return em.createQuery(q).getResultList();
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> type, SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, type, securityContext);
    }

    @Transactional
    public void merge(Object entity) {
        securedBasicRepository.merge(entity);
    }

    @Transactional
    public void massMerge(List<?> entities) {
        securedBasicRepository.massMerge(entities);
    }
}
