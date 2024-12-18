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
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.ui.dashboard.model.*;
import com.flexicore.ui.dashboard.request.GridLayoutCellFilter;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Extension
@Component
public class GridLayoutCellRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<GridLayoutCell> listAllGridLayoutCell(GridLayoutCellFilter gridLayoutCellFilter,
                                                      SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GridLayoutCell> q = cb.createQuery(GridLayoutCell.class);
        Root<GridLayoutCell> r = q.from(GridLayoutCell.class);
        List<Predicate> preds = new ArrayList<>();
        addGridLayoutCellPredicates(preds, cb, q, r, gridLayoutCellFilter, securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<GridLayoutCell> query = em.createQuery(q);
        BasicRepository.addPagination(gridLayoutCellFilter, query);
        return query.getResultList();
    }

    public <T extends GridLayoutCell> void addGridLayoutCellPredicates(List<Predicate> preds, CriteriaBuilder cb,
                                                                       CommonAbstractCriteria q, From<?, T> r, GridLayoutCellFilter gridLayoutCellFilter, SecurityContext securityContext) {

        securedBasicRepository.addSecuredBasicPredicates(null, cb, q, r, preds, securityContext);
        if (gridLayoutCellFilter.getGridLayouts() != null && !gridLayoutCellFilter.getGridLayouts().isEmpty()) {
            Set<String> ids = gridLayoutCellFilter.getGridLayouts().stream().map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, GridLayout> join = r.join(GridLayoutCell_.gridLayout);
            preds.add(join.get(GridLayout_.id).in(ids));
        }

    }

    public long countAllGridLayoutCell(GridLayoutCellFilter gridLayoutCellFilter,
                                       SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<GridLayoutCell> r = q.from(GridLayoutCell.class);
        List<Predicate> preds = new ArrayList<>();
        addGridLayoutCellPredicates(preds, cb, q, r, gridLayoutCellFilter, securityContext);
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
