package com.wizzdi.maps.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.*;
import com.wizzdi.maps.service.request.MappedPOIToLayerFilter;
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
import java.util.stream.Collectors;

@Component
@Extension
public class MappedPOIToLayerRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<MappedPOIToLayer> listAllMappedPOIToLayers(MappedPOIToLayerFilter filter, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MappedPOIToLayer> q = cb.createQuery(MappedPOIToLayer.class);
        Root<MappedPOIToLayer> r = q.from(MappedPOIToLayer.class);
        List<Predicate> preds = new ArrayList<>();
        addMappedPOIToLayerPredicates(filter, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<MappedPOIToLayer> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public <T extends MappedPOIToLayer> void addMappedPOIToLayerPredicates(
            MappedPOIToLayerFilter filter,
            CriteriaBuilder cb,
            CommonAbstractCriteria q,
            From<?, T> r,
            List<Predicate> preds,
            SecurityContext securityContext) {

        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

        if (filter.getMappedPOIs() != null && !filter.getMappedPOIs().isEmpty()) {
            Set<String> ids = filter.getMappedPOIs().stream().map(MappedPOI::getId).collect(Collectors.toSet());
            Join<T, MappedPOI> join = r.join(MappedPOIToLayer_.mappedPOI);
            Predicate in = join.get(MappedPOI_.id).in(ids);
            preds.add(filter.isMappedPOIExclude() ? cb.not(in) : in);
        }

        if (filter.getLayers() != null && !filter.getLayers().isEmpty()) {
            Set<String> ids = filter.getLayers().stream().map(Layer::getId).collect(Collectors.toSet());
            Join<T, Layer> join = r.join(MappedPOIToLayer_.layer);
            Predicate in = join.get(Layer_.id).in(ids);
            preds.add(filter.isLayerExclude() ? cb.not(in) : in);
        }
    }

    public Long countAllMappedPOIToLayers(MappedPOIToLayerFilter filter, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<MappedPOIToLayer> r = q.from(MappedPOIToLayer.class);
        List<Predicate> preds = new ArrayList<>();
        addMappedPOIToLayerPredicates(filter, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        return em.createQuery(q).getSingleResult();
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
