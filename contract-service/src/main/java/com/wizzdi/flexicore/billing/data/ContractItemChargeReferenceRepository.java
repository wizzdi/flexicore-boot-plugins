package com.wizzdi.flexicore.billing.data;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.billing.request.ContractItemChargeReferenceFiltering;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.contract.model.ContractItem;
import com.wizzdi.flexicore.contract.model.ContractItemChargeReference;
import com.wizzdi.flexicore.contract.model.ContractItemChargeReference_;
import com.wizzdi.flexicore.contract.model.ContractItem_;
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
import java.util.stream.Collectors;

@Extension
@Component
public class ContractItemChargeReferenceRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private ChargeReferenceRepository chargeReferenceRepository;

    public List<ContractItemChargeReference> getAllContractItemChargeReferences(SecurityContext securityContext,
                                        ContractItemChargeReferenceFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ContractItemChargeReference> q = cb.createQuery(ContractItemChargeReference.class);
        Root<ContractItemChargeReference> r = q.from(ContractItemChargeReference.class);
        List<Predicate> preds = new ArrayList<>();
        addContractItemChargeReferencePredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new));
        TypedQuery<ContractItemChargeReference> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllContractItemChargeReferences(SecurityContext securityContext,
                                 ContractItemChargeReferenceFiltering filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<ContractItemChargeReference> r = q.from(ContractItemChargeReference.class);
        List<Predicate> preds = new ArrayList<>();
        addContractItemChargeReferencePredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends ContractItemChargeReference> void addContractItemChargeReferencePredicates(ContractItemChargeReferenceFiltering filtering, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContext securityContext) {
        chargeReferenceRepository.addChargeReferencePredicates(filtering, cb, q, r, preds, securityContext);

        if (filtering.getContractItems() != null && !filtering.getContractItems().isEmpty()) {
            Set<String> ids = filtering.getContractItems().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, ContractItem> join = r.join(ContractItemChargeReference_.contractItem);
            preds.add(join.get(ContractItem_.id).in(ids));
        }


    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return chargeReferenceRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return chargeReferenceRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return chargeReferenceRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return chargeReferenceRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return chargeReferenceRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return chargeReferenceRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return chargeReferenceRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        chargeReferenceRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        chargeReferenceRepository.massMerge(toMerge);
    }

}
