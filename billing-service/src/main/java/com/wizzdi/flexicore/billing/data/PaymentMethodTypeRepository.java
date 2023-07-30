package com.wizzdi.flexicore.billing.data;


import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethodType_;
import com.wizzdi.flexicore.billing.request.PaymentMethodTypeFiltering;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.data.BasicRepository;

import jakarta.persistence.TypedQuery;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
public class PaymentMethodTypeRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<PaymentMethodType> getAllPaymentMethodTypes(SecurityContextBase securityContext,
                                                            PaymentMethodTypeFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PaymentMethodType> q = cb.createQuery(PaymentMethodType.class);
        Root<PaymentMethodType> r = q.from(PaymentMethodType.class);
        List<Predicate> preds = new ArrayList<>();
        addPaymentMethodTypePredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new));
        TypedQuery<PaymentMethodType> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllPaymentMethodTypes(SecurityContextBase securityContext,
                                           PaymentMethodTypeFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<PaymentMethodType> r = q.from(PaymentMethodType.class);
        List<Predicate> preds = new ArrayList<>();
        addPaymentMethodTypePredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends PaymentMethodType> void addPaymentMethodTypePredicates(PaymentMethodTypeFiltering filtering,
                                                                             CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

        if (filtering.getCanonicalClassNames() != null && !filtering.getCanonicalClassNames().isEmpty()) {
            preds.add(r.get(PaymentMethodType_.canonicalClassName).in(filtering.getCanonicalClassNames()));
        }

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