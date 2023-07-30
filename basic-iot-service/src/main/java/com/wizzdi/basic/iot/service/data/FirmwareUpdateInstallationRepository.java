
package com.wizzdi.basic.iot.service.data;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateInstallationFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
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
public class FirmwareUpdateInstallationRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;


    public List<FirmwareUpdateInstallation> getAllFirmwareUpdateInstallations(SecurityContextBase securityContext,
                                           FirmwareUpdateInstallationFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FirmwareUpdateInstallation> q = cb.createQuery(FirmwareUpdateInstallation.class);
        Root<FirmwareUpdateInstallation> r = q.from(FirmwareUpdateInstallation.class);
        List<Predicate> preds = new ArrayList<>();
        addFirmwareUpdateInstallationPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.desc(r.get(FirmwareUpdateInstallation_.name)));
        TypedQuery<FirmwareUpdateInstallation> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllFirmwareUpdateInstallations(SecurityContextBase securityContext,
                                   FirmwareUpdateInstallationFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<FirmwareUpdateInstallation> r = q.from(FirmwareUpdateInstallation.class);
        List<Predicate> preds = new ArrayList<>();
        addFirmwareUpdateInstallationPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends FirmwareUpdateInstallation> void addFirmwareUpdateInstallationPredicates(FirmwareUpdateInstallationFilter filtering,
                                                           CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(),cb,q,r,preds,securityContext);
        if(filtering.getFirmwareUpdates()!=null&&!filtering.getFirmwareUpdates().isEmpty()){
            Set<String> ids=filtering.getFirmwareUpdates().stream().map(f->f.getId()).collect(Collectors.toSet());
            Join<T, FirmwareUpdate> join=r.join(FirmwareUpdateInstallation_.firmwareUpdate);
            preds.add(join.get(FirmwareUpdate_.id).in(ids));
        }

        if(filtering.getTargetRemotes()!=null&&!filtering.getTargetRemotes().isEmpty()){
            Set<String> ids=filtering.getTargetRemotes().stream().map(f->f.getId()).collect(Collectors.toSet());
            Join<T, Remote> join=r.join(FirmwareUpdateInstallation_.targetRemote);
            preds.add(join.get(Remote_.id).in(ids));
        }

        if(filtering.getNotInstallations()!=null&&!filtering.getNotInstallations().isEmpty()){
            Set<String> ids=filtering.getNotInstallations().stream().map(f->f.getId()).collect(Collectors.toSet());
            preds.add(cb.not(r.get(FirmwareUpdateInstallation_.id).in(ids)));
        }


        if(filtering.getAvailableAtTime()!=null){
            preds.add(cb.greaterThanOrEqualTo(r.get(FirmwareUpdateInstallation_.targetInstallationDate),filtering.getAvailableAtTime()));
        }

        if(filtering.getTimeForReminder()!=null){
            preds.add(cb.lessThanOrEqualTo(r.get(FirmwareUpdateInstallation_.nextTimeForReminder),filtering.getTimeForReminder()));
        }
        if(filtering.getFirmwareInstallationStates()!=null&&!filtering.getFirmwareInstallationStates().isEmpty()){
            preds.add(r.get(FirmwareUpdateInstallation_.firmwareInstallationState).in(filtering.getFirmwareInstallationStates()));
        }
        if(filtering.getVersions()!=null&&!filtering.getVersions().isEmpty()){
            Join<T,FirmwareUpdate> join=r.join(FirmwareUpdateInstallation_.firmwareUpdate);
            preds.add(join.get(FirmwareUpdate_.version).in(filtering.getVersions()));
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