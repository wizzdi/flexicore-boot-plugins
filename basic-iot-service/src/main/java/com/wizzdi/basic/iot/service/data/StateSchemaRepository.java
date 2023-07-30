
package com.wizzdi.basic.iot.service.data;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.StateSchema;
import com.wizzdi.basic.iot.model.StateSchema_;
import com.wizzdi.basic.iot.service.request.StateSchemaFilter;
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
public class StateSchemaRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<StateSchema> getAllStateSchemas(SecurityContextBase securityContext,
                                           StateSchemaFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StateSchema> q = cb.createQuery(StateSchema.class);
        Root<StateSchema> r = q.from(StateSchema.class);
        List<Predicate> preds = new ArrayList<>();
        addStateSchemaPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(Predicate[]::new)).orderBy(cb.desc(r.get(StateSchema_.version)));
        TypedQuery<StateSchema> query = em.createQuery(q);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public long countAllStateSchemas(SecurityContextBase securityContext,
                                   StateSchemaFilter filtering) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<StateSchema> r = q.from(StateSchema.class);
        List<Predicate> preds = new ArrayList<>();
        addStateSchemaPredicates(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
    }

    public <T extends StateSchema> void addStateSchemaPredicates(StateSchemaFilter filtering,
                                                           CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filtering.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);
        if(filtering.getDeviceTypes()!=null&&!filtering.getDeviceTypes().isEmpty()){
            Set<String> ids=filtering.getDeviceTypes().stream().map(f->f.getId()).collect(Collectors.toSet());
            Join<T, DeviceType> join=r.join(StateSchema_.deviceType);
            preds.add(join.get(StateSchema_.id).in(ids));
        }
        if(filtering.getVersion()!=null){
            preds.add(cb.equal(r.get(StateSchema_.version),filtering.getVersion()));
        }

        if(filtering.getLessThenVersion()!=null){
            preds.add(cb.lessThan(r.get(StateSchema_.version),filtering.getLessThenVersion()));
        }
        if(filtering.getUserAddedSchema()!=null){
            Path<Boolean> userStateSchema = r.get(StateSchema_.userAddedSchema);
            preds.add(filtering.getUserAddedSchema()?cb.isTrue(userStateSchema):cb.isFalse(userStateSchema));
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