package com.flexicore.ui.dashboard.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.ui.dashboard.model.GraphTemplate;
import com.flexicore.ui.dashboard.request.GraphTemplateFilter;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Extension
@Component
public class GraphTemplateRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<GraphTemplate> listAllGraphTemplate(GraphTemplateFilter graphTemplateFilter,
                                                    SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GraphTemplate> q = cb.createQuery(GraphTemplate.class);
        Root<GraphTemplate> r = q.from(GraphTemplate.class);
        List<Predicate> preds = new ArrayList<>();
        addGraphTemplatePredicates(preds, cb, q, r, graphTemplateFilter, securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<GraphTemplate> query = em.createQuery(q);
        BasicRepository.addPagination(graphTemplateFilter, query);
        return query.getResultList();
    }

    public <T extends GraphTemplate> void addGraphTemplatePredicates(List<Predicate> preds, CriteriaBuilder cb,
                                                                     CommonAbstractCriteria q, From<?, T> r, GraphTemplateFilter graphTemplateFilter, SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(null,cb,q,r,preds,securityContext);

    }

    public long countAllGraphTemplate(GraphTemplateFilter graphTemplateFilter,
                                      SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<GraphTemplate> r = q.from(GraphTemplate.class);
        List<Predicate> preds = new ArrayList<>();
        addGraphTemplatePredicates(preds, cb, q, r, graphTemplateFilter, securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return securedBasicRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return securedBasicRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return securedBasicRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return securedBasicRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return securedBasicRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        securedBasicRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        securedBasicRepository.massMerge(toMerge);
    }
}
