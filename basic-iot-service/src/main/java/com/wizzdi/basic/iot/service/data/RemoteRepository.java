
package com.wizzdi.basic.iot.service.data;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.ConnectivityChange;
import com.wizzdi.basic.iot.model.ConnectivityChange_;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.Remote_;
import com.wizzdi.basic.iot.service.config.BasicIOTConfig;
import com.wizzdi.basic.iot.service.events.RemoteUpdatedEvent;
import com.wizzdi.basic.iot.service.request.RemoteFilter;
import com.wizzdi.dynamic.properties.converter.postgresql.FilterDynamicPropertiesUtils;
import com.wizzdi.dynamic.properties.converter.postgresql.FilterStaticPropertiesUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.data.MappedPOIRepository;
import org.checkerframework.checker.units.qual.A;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import java.util.stream.Collectors;

@Extension
@Component
public class RemoteRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;
    @Autowired
    private MappedPOIRepository mappedPOIRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private BasicIOTConfig basicIOTConfig;

    public List<Remote> getAllRemotes(SecurityContextBase securityContext,
                                           RemoteFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Remote> q = cb.createQuery(Remote.class);
        Root<Remote> r = q.from(Remote.class);
        List<Predicate> preds = new ArrayList<>();
        addRemotePredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.desc(r.get(Remote_.name)));
        TypedQuery<Remote> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllRemotes(SecurityContextBase securityContext,
                                   RemoteFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Remote> r = q.from(Remote.class);
        List<Predicate> preds = new ArrayList<>();
        addRemotePredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends Remote> void addRemotePredicates(RemoteFilter filtering,
                                                           CriteriaBuilder cb, CriteriaQuery<?> q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

        if(filtering.getRemoteIds()!=null&&!filtering.getRemoteIds().isEmpty()){
            preds.add(r.get(Remote_.remoteId).in(filtering.getRemoteIds()));
        }
        if(filtering.getConnectivity()!=null&&!filtering.getConnectivity().isEmpty()){
            Join<T, ConnectivityChange> join=r.join(Remote_.lastConnectivityChange);
            preds.add(join.get(ConnectivityChange_.connectivity).in(filtering.getConnectivity()));
        }
        if(filtering.getNotIds()!=null&&!filtering.getNotIds().isEmpty()){
            preds.add(cb.not(r.get(Remote_.id).in(filtering.getNotIds())));
        }
        if(filtering.getLastSeenTo()!=null){
            preds.add(cb.or(cb.lessThanOrEqualTo(r.get(Remote_.lastSeen),filtering.getLastSeenTo()),r.get(Remote_.lastSeen).isNull()));
        }
        if(filtering.getDevicePropertiesFilter()!=null){
            preds.addAll(FilterDynamicPropertiesUtils.filterDynamic(filtering.getDevicePropertiesFilter(),cb,(Root)r,"deviceProperties"));
        }
        if(filtering.getUserAddedPropertiesFilter()!=null){
            preds.addAll(FilterDynamicPropertiesUtils.filterDynamic(filtering.getUserAddedPropertiesFilter(),cb,(Root)r,"userAddedProperties"));
        }
        if(filtering.getStaticPropertiesFilter()!=null){
            preds.addAll(FilterStaticPropertiesUtils.filterStatic(filtering.getStaticPropertiesFilter(),cb,(Root)r));
        }
        if(filtering.getMappedPOIFilter()!=null){
            Join<T, MappedPOI> join = r.join(Remote_.mappedPOI);
            mappedPOIRepository.addMappedPOIPredicate(filtering.getMappedPOIFilter(),cb,q, join,preds,securityContext);
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
    public void merge(Remote base, RemoteUpdatedEvent remoteUpdatedEvent) {
        boolean created = base.getUpdateDate() == null;
        securedBasicRepository.merge(base,true,false);
        publishRemoteEvent(base, created, remoteUpdatedEvent);


    }

    @Transactional
    public void massMergePlain(List<?> toMerge) {
        securedBasicRepository.massMerge(toMerge);
    }
    @Transactional
    public void massMerge(List<? extends Remote> toMerge, Map<String,RemoteUpdatedEvent> remoteUpdatedEventMap) {
        Set<String> createdMap=toMerge.stream().filter(f->f.getUpdateDate()==null).map(f->f.getId()).collect(Collectors.toSet());
        securedBasicRepository.massMerge(toMerge,true,false);
        for (Remote remote : toMerge) {
            boolean created = createdMap.contains(remote.getId());
            RemoteUpdatedEvent remoteUpdatedEvent=remoteUpdatedEventMap.get(remote.getId());
            publishRemoteEvent(remote, created, remoteUpdatedEvent);
        }
    }

    private void publishRemoteEvent(Remote remote, boolean created, RemoteUpdatedEvent remoteUpdatedEvent) {
        if(created){
            eventPublisher.publishEvent(new BasicCreated<>(remote));
            return;
        }
        if(remoteUpdatedEvent!=null){
            eventPublisher.publishEvent(remoteUpdatedEvent);
            return;
        }
        eventPublisher.publishEvent(new BasicUpdated<>(remote));
    }

    @Transactional
    public void massMerge(List<?> toMerge, boolean updatedate, boolean propagateEvents) {
        securedBasicRepository.massMerge(toMerge, updatedate, propagateEvents);
    }

    @Transactional
    public void createRemoteIdx() {
        em.createNativeQuery("""
                CREATE UNIQUE INDEX IF NOT EXISTS remote_unique_idx 
                ON Remote (remoteId) 
                WHERE softdelete = false
                """).executeUpdate();
    }
}
