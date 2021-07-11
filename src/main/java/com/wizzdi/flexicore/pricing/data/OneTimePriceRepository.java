package com.wizzdi.flexicore.pricing.data;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.model.price.OneTimePrice;
import com.wizzdi.flexicore.pricing.model.price.OneTimePrice_;
import com.wizzdi.flexicore.pricing.request.OneTimePriceFiltering;
import com.wizzdi.flexicore.security.data.BasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Extension
@Component
public class OneTimePriceRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private PriceRepository priceRepository;

    public List<OneTimePrice> getAllOneTimePrice(SecurityContextBase securityContext,
                                           OneTimePriceFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OneTimePrice> q = cb.createQuery(OneTimePrice.class);
        Root<OneTimePrice> r = q.from(OneTimePrice.class);
        List<Predicate> preds = new ArrayList<>();
        addOneTimePricePredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.desc(r.get(OneTimePrice_.name)));
        TypedQuery<OneTimePrice> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllOneTimePrice(SecurityContextBase securityContext,
                                   OneTimePriceFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<OneTimePrice> r = q.from(OneTimePrice.class);
        List<Predicate> preds = new ArrayList<>();
        addOneTimePricePredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends OneTimePrice> void addOneTimePricePredicates(OneTimePriceFiltering filtering,
                                                           CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
        priceRepository.addPricePredicates(filtering, cb, q, r, preds, securityContext);



    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return priceRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return priceRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return priceRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
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