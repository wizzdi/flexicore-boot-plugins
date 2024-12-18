package com.wizzdi.flexicore.pricing.data;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice;
import com.wizzdi.flexicore.pricing.model.price.RecurringPrice_;
import com.wizzdi.flexicore.pricing.request.RecurringPriceFiltering;
import com.wizzdi.flexicore.security.data.BasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Extension
@Component
public class RecurringPriceRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private PriceRepository priceRepository;

    public List<RecurringPrice> getAllRecurringPrice(SecurityContext securityContext,
                                           RecurringPriceFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RecurringPrice> q = cb.createQuery(RecurringPrice.class);
        Root<RecurringPrice> r = q.from(RecurringPrice.class);
        List<Predicate> preds = new ArrayList<>();
        addRecurringPricePredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.desc(r.get(RecurringPrice_.name)));
        TypedQuery<RecurringPrice> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllRecurringPrice(SecurityContext securityContext,
                                   RecurringPriceFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<RecurringPrice> r = q.from(RecurringPrice.class);
        List<Predicate> preds = new ArrayList<>();
        addRecurringPricePredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends RecurringPrice> void addRecurringPricePredicates(RecurringPriceFiltering filtering,
                                                           CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContext securityContext) {
        priceRepository.addPricePredicates(filtering, cb, q, r, preds, securityContext);


    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return priceRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return priceRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return priceRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return priceRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return priceRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return priceRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return priceRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        priceRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        priceRepository.massMerge(toMerge);
    }

}
