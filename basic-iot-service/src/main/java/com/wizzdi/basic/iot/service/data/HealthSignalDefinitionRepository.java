package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.wizzdi.basic.iot.model.HealthSignalDefinition;
import com.wizzdi.basic.iot.model.HealthSignalDefinition_;
import com.wizzdi.basic.iot.service.request.HealthSignalDefinitionFilter;
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
import java.util.List;

@Extension
@Component
public class HealthSignalDefinitionRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<HealthSignalDefinition> list(SecurityContext securityContext, HealthSignalDefinitionFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HealthSignalDefinition> q = cb.createQuery(HealthSignalDefinition.class);
        Root<HealthSignalDefinition> r = q.from(HealthSignalDefinition.class);
        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Baseclass_.name)));
        TypedQuery<HealthSignalDefinition> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long count(SecurityContext securityContext, HealthSignalDefinitionFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<HealthSignalDefinition> r = q.from(HealthSignalDefinition.class);
        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private <T extends HealthSignalDefinition> void addPredicates(HealthSignalDefinitionFilter filter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getHealthSignalDefinitionIds() != null && !filter.getHealthSignalDefinitionIds().isEmpty()) {
            predicates.add(r.get(HealthSignalDefinition_.id).in(filter.getHealthSignalDefinitionIds()));
        }
        if (filter.getExternalIds() != null && !filter.getExternalIds().isEmpty()) {
            predicates.add(r.get(HealthSignalDefinition_.externalId).in(filter.getExternalIds()));
        }
        if (filter.getValueTypes() != null && !filter.getValueTypes().isEmpty()) {
            predicates.add(r.get(HealthSignalDefinition_.valueType).in(filter.getValueTypes()));
        }
        if (filter.getBuiltIn() != null) {
            predicates.add(cb.equal(r.get(HealthSignalDefinition_.builtIn), filter.getBuiltIn()));
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
