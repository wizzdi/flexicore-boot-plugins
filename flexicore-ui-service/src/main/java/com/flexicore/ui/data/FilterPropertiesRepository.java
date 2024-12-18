package com.flexicore.ui.data;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.flexicore.model.Basic;
import com.flexicore.ui.model.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.flexicore.model.Baseclass;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.request.FilterPropertiesFiltering;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Extension
@Component
public class FilterPropertiesRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<FilterProperties> listAllFilterProperties(FilterPropertiesFiltering filterPropertiesFiltering,
                                                          SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FilterProperties> q = cb.createQuery(FilterProperties.class);
        Root<FilterProperties> r = q.from(FilterProperties.class);
        List<Predicate> preds = new ArrayList<>();
        addFilterPropertiesPredicates(preds, cb,q, r, filterPropertiesFiltering,securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<FilterProperties> query = em.createQuery(q);
        BasicRepository.addPagination(filterPropertiesFiltering, query);
        return query.getResultList();
    }

    public <T extends FilterProperties> void addFilterPropertiesPredicates(List<Predicate> preds, CriteriaBuilder cb,CommonAbstractCriteria q,
                                               From<?,T> r, FilterPropertiesFiltering filterPropertiesFiltering,SecurityContext SecurityContext) {
        securedBasicRepository.addSecuredBasicPredicates(null,cb,q,r,preds,SecurityContext);
        if (filterPropertiesFiltering.getBaseclasses() != null && !filterPropertiesFiltering.getBaseclasses().isEmpty()) {
            Set<String> ids = filterPropertiesFiltering.getBaseclasses().stream().map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, Preset> join = r.join(FilterProperties_.relatedBaseclass);
            preds.add(join.get(GridPreset_.id).in(ids));
        }


    }

    public long countAllFilterProperties(FilterPropertiesFiltering filterPropertiesFiltering,
                                         SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<FilterProperties> r = q.from(FilterProperties.class);
        List<Predicate> preds = new ArrayList<>();
        addFilterPropertiesPredicates(preds, cb,q, r, filterPropertiesFiltering,securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(q);
        BasicRepository.addPagination(filterPropertiesFiltering, query);
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
