package com.flexicore.ui.dashboard.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.request.GridLayoutFilter;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Extension
@Component
public class GridLayoutRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<GridLayout> listAllGridLayout(GridLayoutFilter gridLayoutFilter,
                                              SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GridLayout> q = cb.createQuery(GridLayout.class);
        Root<GridLayout> r = q.from(GridLayout.class);
        List<Predicate> preds = new ArrayList<>();
        addGridLayoutPredicates(preds, cb, q, r, gridLayoutFilter, securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<GridLayout> query = em.createQuery(q);
        BasicRepository.addPagination(gridLayoutFilter, query);
        return query.getResultList();
    }

    public <T extends GridLayout> void addGridLayoutPredicates(List<Predicate> preds, CriteriaBuilder cb,
                                                               CommonAbstractCriteria q, From<?, T> r, GridLayoutFilter gridLayoutFilter, SecurityContextBase securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(null, cb, q, r, preds, securityContext);


    }

    public long countAllGridLayout(GridLayoutFilter gridLayoutFilter,
                                   SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<GridLayout> r = q.from(GridLayout.class);
        List<Predicate> preds = new ArrayList<>();
        addGridLayoutPredicates(preds, cb, q, r, gridLayoutFilter, securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return securedBasicRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
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
