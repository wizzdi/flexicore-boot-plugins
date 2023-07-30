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

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.ui.dashboard.model.*;
import com.flexicore.ui.dashboard.request.CellContentElementFilter;
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
public class CellContentElementRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<CellContentElement> listAllCellContentElement(CellContentElementFilter cellContentElementFilter,
                                                              SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CellContentElement> q = cb.createQuery(CellContentElement.class);
        Root<CellContentElement> r = q.from(CellContentElement.class);
        List<Predicate> preds = new ArrayList<>();
        addCellContentElementPredicates(preds, cb, q, r, cellContentElementFilter, securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<CellContentElement> query = em.createQuery(q);
        BasicRepository.addPagination(cellContentElementFilter, query);
        return query.getResultList();
    }

    public <T extends CellContentElement> void addCellContentElementPredicates(List<Predicate> preds, CriteriaBuilder cb, CommonAbstractCriteria q,
                                                                               From<?, T> r, CellContentElementFilter cellContentElementFilter, SecurityContextBase securityContextBase) {
        securedBasicRepository.addSecuredBasicPredicates(null, cb, q, r, preds, securityContextBase);

        if (cellContentElementFilter.getCellContents() != null && !cellContentElementFilter.getCellContents().isEmpty()) {
            Set<String> ids = cellContentElementFilter.getCellContents().stream().map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, CellContent> join = r.join(CellContentElement_.cellContent);
            preds.add(join.get(CellContent_.id).in(ids));
        }


    }

    public long countAllCellContentElement(CellContentElementFilter cellContentElementFilter,
                                           SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<CellContentElement> r = q.from(CellContentElement.class);
        List<Predicate> preds = new ArrayList<>();
        addCellContentElementPredicates(preds, cb, q, r, cellContentElementFilter, securityContext);
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
