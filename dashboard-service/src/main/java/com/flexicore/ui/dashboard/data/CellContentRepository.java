package com.flexicore.ui.dashboard.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.request.CellContentFiltering;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Extension
@Component
public class CellContentRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<CellContent> listAllCellContent(CellContentFiltering cellContentFiltering,
                                                SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CellContent> q = cb.createQuery(CellContent.class);
        Root<CellContent> r = q.from(CellContent.class);
        List<Predicate> preds = new ArrayList<>();
        addCellContentPredicates(preds, cb, q, r, cellContentFiltering, securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<CellContent> query = em.createQuery(q);
        BasicRepository.addPagination(cellContentFiltering, query);
        return query.getResultList();
    }

    public <T extends CellContent> void addCellContentPredicates(List<Predicate> preds, CriteriaBuilder cb,CommonAbstractCriteria q,
                                                                 From<?,T> r, CellContentFiltering cellContentFiltering,SecurityContextBase securityContextBase) {
        securedBasicRepository.addSecuredBasicPredicates(null,cb,q,r,preds,securityContextBase);


    }

    public long countAllCellContent(CellContentFiltering cellContentFiltering,
                                    SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<CellContent> r = q.from(CellContent.class);
        List<Predicate> preds = new ArrayList<>();
        addCellContentPredicates(preds, cb, q, r, cellContentFiltering, securityContext);
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
