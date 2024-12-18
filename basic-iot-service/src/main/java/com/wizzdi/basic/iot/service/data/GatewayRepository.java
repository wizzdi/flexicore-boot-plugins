
package com.wizzdi.basic.iot.service.data;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.Gateway_;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.events.RemoteUpdatedEvent;
import com.wizzdi.basic.iot.service.request.GatewayFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
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
import java.util.Map;
import java.util.Set;

@Extension
@Component
public class GatewayRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private RemoteRepository remoteRepository;

    public List<Gateway> getAllGateways(SecurityContext securityContext,
                                           GatewayFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Gateway> q = cb.createQuery(Gateway.class);
        Root<Gateway> r = q.from(Gateway.class);
        List<Predicate> preds = new ArrayList<>();
        addGatewayPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(Gateway_.remoteId)));
        TypedQuery<Gateway> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllGateways(SecurityContext securityContext,
                                   GatewayFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Gateway> r = q.from(Gateway.class);
        List<Predicate> preds = new ArrayList<>();
        addGatewayPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends Gateway> void addGatewayPredicates(GatewayFilter filtering,
                                                           CriteriaBuilder cb, CriteriaQuery<?> q, From<?, T> r, List<Predicate> preds, SecurityContext securityContext) {
        remoteRepository.addRemotePredicates(filtering,cb,q,r,preds,securityContext);



    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return remoteRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return remoteRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return remoteRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return remoteRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return remoteRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return remoteRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return remoteRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Remote base, RemoteUpdatedEvent remoteUpdatedEvent) {
        remoteRepository.merge(base,remoteUpdatedEvent);
    }

    @Transactional
    public void massMerge(List<? extends Remote> toMerge, Map<String,RemoteUpdatedEvent> remoteUpdatedEventMap) {
        remoteRepository.massMerge(toMerge,remoteUpdatedEventMap);
    }

    @Transactional
    public void massMergePlain(List<?> toMerge) {
        remoteRepository.massMergePlain(toMerge);
    }
}
