
package com.wizzdi.basic.iot.service.data;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.StateHistory;
import com.wizzdi.basic.iot.model.StateHistory_;
import com.wizzdi.basic.iot.service.request.StateHistoryFilter;
import com.wizzdi.dynamic.properties.converter.postgresql.FilterDynamicPropertiesUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.data.MappedPOIRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Extension
@Component
public class StateHistoryRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;
    @Autowired
    private RemoteRepository remoteRepository;

    public List<StateHistory> getAllStateHistories(SecurityContextBase securityContext,
                                           StateHistoryFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StateHistory> q = cb.createQuery(StateHistory.class);
        Root<StateHistory> r = q.from(StateHistory.class);
        List<Predicate> preds = new ArrayList<>();
        addStateHistoryPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.desc(r.get(StateHistory_.name)));
        TypedQuery<StateHistory> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllStateHistories(SecurityContextBase securityContext,
                                   StateHistoryFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<StateHistory> r = q.from(StateHistory.class);
        List<Predicate> preds = new ArrayList<>();
        addStateHistoryPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends StateHistory> void addStateHistoryPredicates(StateHistoryFilter filtering,
                                                           CriteriaBuilder cb, CriteriaQuery<?> q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

        if(filtering.getTimeAtStateTo()!=null){
            preds.add(cb.or(cb.lessThanOrEqualTo(r.get(StateHistory_.timeAtState),filtering.getTimeAtStateTo()),r.get(StateHistory_.timeAtState).isNull()));
        }
        if(filtering.getTimeAtStateFrom()!=null){
            preds.add(cb.or(cb.greaterThanOrEqualTo(r.get(StateHistory_.timeAtState),filtering.getTimeAtStateTo()),r.get(StateHistory_.timeAtState).isNull()));
        }
        if(filtering.getDevicePropertiesFilter()!=null){
            preds.addAll(FilterDynamicPropertiesUtils.filterDynamic(filtering.getDevicePropertiesFilter(),cb,(Root)r,"deviceProperties"));
        }
        if(filtering.getUserAddedPropertiesFilter()!=null){
            preds.addAll(FilterDynamicPropertiesUtils.filterDynamic(filtering.getUserAddedPropertiesFilter(),cb,(Root)r,"userAddedProperties"));
        }
        if(filtering.getRemoteFilter()!=null){
            Join<T, Remote> join = r.join(StateHistory_.remote);
            remoteRepository.addRemotePredicates(filtering.getRemoteFilter(),cb,q, join,preds,securityContext);
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