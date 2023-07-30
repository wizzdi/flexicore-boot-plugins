package com.wizzdi.flexicore.billing.data;


import com.wizzdi.flexicore.billing.model.payment.*;
import com.wizzdi.flexicore.billing.request.PaymentMethodFiltering;
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

import com.flexicore.organization.model.Customer;
import com.flexicore.organization.model.Customer_;
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
public class PaymentMethodRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<PaymentMethod> getAllPaymentMethods(SecurityContextBase securityContext,
                                                    PaymentMethodFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PaymentMethod> q = cb.createQuery(PaymentMethod.class);
        Root<PaymentMethod> r = q.from(PaymentMethod.class);
        List<Predicate> preds = new ArrayList<>();
        addPaymentMethodPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new));
        TypedQuery<PaymentMethod> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllPaymentMethods(SecurityContextBase securityContext,
                                       PaymentMethodFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<PaymentMethod> r = q.from(PaymentMethod.class);
        List<Predicate> preds = new ArrayList<>();
        addPaymentMethodPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends PaymentMethod> void addPaymentMethodPredicates(PaymentMethodFiltering filtering,
                                                                     CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

        if (filtering.getCustomers() != null && !filtering.getCustomers().isEmpty()) {
            Set<String> ids = filtering.getCustomers().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, Customer> join = r.join(PaymentMethod_.customer);
            preds.add(join.get(Customer_.id).in(ids));
        }
        if (filtering.getPaymentMethodTypes() != null && !filtering.getPaymentMethodTypes().isEmpty()) {
            Set<String> ids = filtering.getPaymentMethodTypes().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, PaymentMethodType> join = r.join(PaymentMethod_.paymentMethodType);
            preds.add(join.get(PaymentMethodType_.id).in(ids));
        }
        if (filtering.getActive() != null) {
            preds.add(cb.equal(r.get(PaymentMethod_.active), filtering.getActive()));
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