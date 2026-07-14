package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.wizzdi.basic.iot.model.HealthSignalMapping;
import com.wizzdi.basic.iot.model.HealthSignalMapping_;
import com.wizzdi.basic.iot.model.RemoteHealthProfile;
import com.wizzdi.basic.iot.model.RemoteHealthProfile_;
import com.wizzdi.basic.iot.model.RemoteHealthRule;
import com.wizzdi.basic.iot.model.RemoteHealthRuleCondition;
import com.wizzdi.basic.iot.model.RemoteHealthRuleCondition_;
import com.wizzdi.basic.iot.model.RemoteHealthRule_;
import com.wizzdi.basic.iot.service.request.RemoteHealthProfileFilter;
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
public class RemoteHealthProfileRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<RemoteHealthProfile> list(SecurityContext securityContext, RemoteHealthProfileFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteHealthProfile> q = cb.createQuery(RemoteHealthProfile.class);
        Root<RemoteHealthProfile> r = q.from(RemoteHealthProfile.class);
        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Baseclass_.name)));
        TypedQuery<RemoteHealthProfile> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long count(SecurityContext securityContext, RemoteHealthProfileFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<RemoteHealthProfile> r = q.from(RemoteHealthProfile.class);
        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private <T extends RemoteHealthProfile> void addPredicates(RemoteHealthProfileFilter filter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getRemoteHealthProfileIds() != null && !filter.getRemoteHealthProfileIds().isEmpty()) {
            predicates.add(r.get(RemoteHealthProfile_.id).in(filter.getRemoteHealthProfileIds()));
        }
        if (filter.getExternalIds() != null && !filter.getExternalIds().isEmpty()) {
            predicates.add(r.get(RemoteHealthProfile_.externalId).in(filter.getExternalIds()));
        }
        if (filter.getEnabled() != null) {
            predicates.add(cb.equal(r.get(RemoteHealthProfile_.enabled), filter.getEnabled()));
        }
    }

    public List<HealthSignalMapping> listMappings(String profileId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthSignalMapping> q = cb.createQuery(HealthSignalMapping.class);
        Root<HealthSignalMapping> r = q.from(HealthSignalMapping.class);
        q.select(r).where(
                cb.equal(r.get(HealthSignalMapping_.remoteHealthProfile).get(RemoteHealthProfile_.id), profileId),
                cb.isFalse(r.get(Baseclass_.softDelete))
        ).orderBy(cb.desc(r.get(HealthSignalMapping_.priority)), cb.asc(r.get(Baseclass_.name)));
        return em.createQuery(q).getResultList();
    }

    public List<RemoteHealthRule> listRules(String profileId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteHealthRule> q = cb.createQuery(RemoteHealthRule.class);
        Root<RemoteHealthRule> r = q.from(RemoteHealthRule.class);
        q.select(r).where(
                cb.equal(r.get(RemoteHealthRule_.remoteHealthProfile).get(RemoteHealthProfile_.id), profileId),
                cb.isFalse(r.get(Baseclass_.softDelete))
        ).orderBy(cb.desc(r.get(RemoteHealthRule_.priority)), cb.asc(r.get(Baseclass_.name)));
        return em.createQuery(q).getResultList();
    }

    public List<RemoteHealthRuleCondition> listConditions(Collection<String> ruleIds) {
        if (ruleIds == null || ruleIds.isEmpty()) {
            return Collections.emptyList();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RemoteHealthRuleCondition> q = cb.createQuery(RemoteHealthRuleCondition.class);
        Root<RemoteHealthRuleCondition> r = q.from(RemoteHealthRuleCondition.class);
        q.select(r).where(
                r.get(RemoteHealthRuleCondition_.remoteHealthRule).get(RemoteHealthRule_.id).in(ruleIds),
                cb.isFalse(r.get(Baseclass_.softDelete))
        ).orderBy(cb.asc(r.get(RemoteHealthRuleCondition_.priority)), cb.asc(r.get(Baseclass_.name)));
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
