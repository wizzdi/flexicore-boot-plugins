package com.wizzdi.basic.iot.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.basic.iot.model.*;
import com.wizzdi.basic.iot.service.request.DeviceTypeToMapIconFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MapIcon_;
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

@Extension
@Component
public class DeviceTypeToMapIconRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<DeviceTypeToMapIcon> listAllDeviceTypeToMapIcons(SecurityContext securityContext, DeviceTypeToMapIconFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DeviceTypeToMapIcon> q = cb.createQuery(DeviceTypeToMapIcon.class);
        Root<DeviceTypeToMapIcon> r = q.from(DeviceTypeToMapIcon.class);
        List<Predicate> preds = new ArrayList<>();
        addDeviceTypeToMapIconPredicates(filter, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.asc(r.get(DeviceTypeToMapIcon_.name)));
        TypedQuery<DeviceTypeToMapIcon> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public long countAllDeviceTypeToMapIcons(SecurityContext securityContext, DeviceTypeToMapIconFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<DeviceTypeToMapIcon> r = q.from(DeviceTypeToMapIcon.class);
        List<Predicate> preds = new ArrayList<>();
        addDeviceTypeToMapIconPredicates(filter, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        return em.createQuery(q).getSingleResult();
    }

    public <T extends DeviceTypeToMapIcon> void addDeviceTypeToMapIconPredicates(
            DeviceTypeToMapIconFilter filter,
            CriteriaBuilder cb,
            CommonAbstractCriteria q,
            From<?, T> r,
            List<Predicate> preds,
            SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

        if (filter.getDeviceTypes() != null && !filter.getDeviceTypes().isEmpty()) {
            Set<String> ids = filter.getDeviceTypes().stream().map(DeviceType::getId).collect(Collectors.toSet());
            Join<T, DeviceType> join = r.join(DeviceTypeToMapIcon_.deviceType);
            preds.add(join.get(DeviceType_.id).in(ids));
        }

        if (filter.getMapIcons() != null && !filter.getMapIcons().isEmpty()) {
            Set<String> ids = filter.getMapIcons().stream().map(MapIcon::getId).collect(Collectors.toSet());
            Join<T, MapIcon> join = r.join(DeviceTypeToMapIcon_.mapIcon);
            preds.add(join.get(MapIcon_.id).in(ids));
        }

        if (filter.getDefaultIcon() != null) {
            preds.add(filter.getDefaultIcon() ? cb.isTrue(r.get(DeviceTypeToMapIcon_.defaultIcon)) : cb.isFalse(r.get(DeviceTypeToMapIcon_.defaultIcon)));
        }

        if (filter.getDeviceTypeState() != null && !filter.getDeviceTypeState().isBlank()) {
            preds.add(cb.like(cb.lower(r.get(DeviceTypeToMapIcon_.deviceTypeStates)), "%" + filter.getDeviceTypeState().toLowerCase() + "%"));
        }
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
