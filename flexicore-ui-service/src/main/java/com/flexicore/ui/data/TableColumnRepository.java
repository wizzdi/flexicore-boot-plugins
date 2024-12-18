package com.flexicore.ui.data;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.flexicore.model.*;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.model.*;
import com.flexicore.ui.request.*;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Extension
@Component
public class TableColumnRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private UiFieldRepository uiFieldRepository;


    public List<TableColumn> listAllTableColumns(
            TableColumnFiltering tableColumnFiltering,
            SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TableColumn> q = cb.createQuery(TableColumn.class);
        Root<TableColumn> r = q.from(TableColumn.class);
        List<Predicate> preds = new ArrayList<>();
        addTableColumnPredicates(preds, cb, q, r, tableColumnFiltering, securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<TableColumn> query = em.createQuery(q);
        BasicRepository.addPagination(tableColumnFiltering, query);
        return query.getResultList();
    }

    public <T extends TableColumn> void addTableColumnPredicates(List<Predicate> preds,
                                                                 CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r,
                                                                 TableColumnFiltering tableColumnFiltering, SecurityContext SecurityContext) {
		uiFieldRepository.addUiFieldPredicates(preds,cb,q,r,tableColumnFiltering,SecurityContext);

    }

    public long countAllTableColumns(TableColumnFiltering tableColumnFiltering,
                                     SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<TableColumn> r = q.from(TableColumn.class);
        List<Predicate> preds = new ArrayList<>();
        addTableColumnPredicates(preds, cb, q, r, tableColumnFiltering, securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return uiFieldRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return uiFieldRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return uiFieldRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return uiFieldRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return uiFieldRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return uiFieldRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return uiFieldRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        uiFieldRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        uiFieldRepository.massMerge(toMerge);
    }
}
