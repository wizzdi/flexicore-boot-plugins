package com.flexicore.ui.data;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.model.FormField;
import com.flexicore.ui.request.FormFieldFiltering;

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
public class FormFieldRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private UiFieldRepository uiFieldRepository;

    @Transactional
    public void massMerge(List<?> list) {
        for (Object o : list) {
            em.merge(o);
        }
    }

    public List<FormField> listAllFormFields(
            FormFieldFiltering formFieldFiltering,
            SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FormField> q = cb.createQuery(FormField.class);
        Root<FormField> r = q.from(FormField.class);
        List<Predicate> preds = new ArrayList<>();
        addFormFieldPredicates(preds, cb,q, r, formFieldFiltering,securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<FormField> query = em.createQuery(q);
        BasicRepository.addPagination(formFieldFiltering, query);
        return query.getResultList();
    }

    public <T extends FormField> void addFormFieldPredicates(List<Predicate> preds,
                                        CriteriaBuilder cb,CommonAbstractCriteria q, From<?,FormField> r,
                                        FormFieldFiltering formFieldFiltering,SecurityContext SecurityContext) {
        uiFieldRepository.addUiFieldPredicates(preds,cb,q,r,formFieldFiltering,SecurityContext);

    }

    public long countAllFormFields(FormFieldFiltering formFieldFiltering,
                                   SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<FormField> r = q.from(FormField.class);
        List<Predicate> preds = new ArrayList<>();
        addFormFieldPredicates(preds, cb,q, r, formFieldFiltering,securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(q);
        BasicRepository.addPagination(formFieldFiltering, query);
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
}
