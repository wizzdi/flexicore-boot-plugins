package com.flexicore.ui.data;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.model.Preset;
import com.flexicore.ui.model.Preset_;
import com.flexicore.ui.request.PresetFiltering;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Extension
@Component
public class PresetRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<Preset> listAllPresets(PresetFiltering presetFiltering,
                                       SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Preset> q = cb.createQuery(Preset.class);
        Root<Preset> r = q.from(Preset.class);
        List<Predicate> preds = new ArrayList<>();
        addPresetPredicates(preds, cb,q, r, presetFiltering,securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<Preset> query = em.createQuery(q);
        BasicRepository.addPagination(presetFiltering, query);
        return query.getResultList();
    }

    public <T extends Preset> void addPresetPredicates(List<Predicate> preds, CriteriaBuilder cb,
                                                       CommonAbstractCriteria q,
                                                       From<?,T> r, PresetFiltering presetFiltering,SecurityContext SecurityContext) {
        securedBasicRepository.addSecuredBasicPredicates(presetFiltering.getBasicPropertiesFilter(),cb,q,r,preds,SecurityContext);
        if (presetFiltering.getExternalIds() != null && !presetFiltering.getExternalIds().isEmpty()) {
            preds.add(r.get(Preset_.externalId).in(presetFiltering.getExternalIds()));
        }
        if(presetFiltering.getRelatedBaseclass()!=null&&!presetFiltering.getRelatedBaseclass().isEmpty()){
            preds.add(r.get(Baseclass_.securityId).in(presetFiltering.getRelatedBaseclass()));
        }

    }

    public long countAllPresets(PresetFiltering presetFiltering,
                                SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Preset> r = q.from(Preset.class);
        List<Predicate> preds = new ArrayList<>();
        addPresetPredicates(preds, cb,q, r, presetFiltering,securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(q);
        BasicRepository.addPagination(presetFiltering, query);
        return query.getSingleResult();
    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return securedBasicRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
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
