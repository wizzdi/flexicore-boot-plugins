package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.wizzdi.basic.iot.model.StatePropertyDefinition;
import com.wizzdi.basic.iot.model.StatePropertyDefinition_;
import com.wizzdi.basic.iot.model.StateSchema_;
import com.wizzdi.basic.iot.service.request.StatePropertyDefinitionFilter;
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
public class StatePropertyDefinitionRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<StatePropertyDefinition> list(SecurityContext securityContext, StatePropertyDefinitionFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StatePropertyDefinition> q = cb.createQuery(StatePropertyDefinition.class);
        Root<StatePropertyDefinition> r = q.from(StatePropertyDefinition.class);
        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(r).where(predicates.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Baseclass_.name)));
        TypedQuery<StatePropertyDefinition> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long count(SecurityContext securityContext, StatePropertyDefinitionFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<StatePropertyDefinition> r = q.from(StatePropertyDefinition.class);
        List<Predicate> predicates = new ArrayList<>();
        addPredicates(filter, cb, q, r, predicates, securityContext);
        q.select(cb.count(r)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    private <T extends StatePropertyDefinition> void addPredicates(StatePropertyDefinitionFilter filter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> predicates, SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, predicates, securityContext);
        if (filter.getStatePropertyDefinitionIds() != null && !filter.getStatePropertyDefinitionIds().isEmpty()) {
            predicates.add(r.get(StatePropertyDefinition_.id).in(filter.getStatePropertyDefinitionIds()));
        }
        if (filter.getStateSchemaIds() != null && !filter.getStateSchemaIds().isEmpty()) {
            predicates.add(r.get(StatePropertyDefinition_.stateSchema).get(StateSchema_.id).in(filter.getStateSchemaIds()));
        }
        if (filter.getExternalIds() != null && !filter.getExternalIds().isEmpty()) {
            predicates.add(r.get(StatePropertyDefinition_.externalId).in(filter.getExternalIds()));
        }
        if (filter.getValueTypes() != null && !filter.getValueTypes().isEmpty()) {
            predicates.add(r.get(StatePropertyDefinition_.valueType).in(filter.getValueTypes()));
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
