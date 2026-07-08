package com.wizzdi.building.studio.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.building.studio.model.*;
import com.wizzdi.building.studio.service.request.BuildingBundleFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
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

@Component
@Extension
public class BuildingStudioRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<BuildingBundle> listAllBuildingBundles(BuildingBundleFilter filter, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BuildingBundle> q = cb.createQuery(BuildingBundle.class);
        Root<BuildingBundle> r = q.from(BuildingBundle.class);
        List<Predicate> preds = new ArrayList<>();
        addBuildingBundlePredicates(filter, cb, q, r, preds, securityContext);
        q.select(r).where(preds.toArray(new Predicate[0])).orderBy(cb.desc(r.get("creationDate")));
        TypedQuery<BuildingBundle> query = em.createQuery(q);
        BasicRepository.addPagination(filter, query);
        return query.getResultList();
    }

    public Long countAllBuildingBundles(BuildingBundleFilter filter, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<BuildingBundle> r = q.from(BuildingBundle.class);
        List<Predicate> preds = new ArrayList<>();
        addBuildingBundlePredicates(filter, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        return em.createQuery(q).getSingleResult();
    }

    public <T extends BuildingBundle> void addBuildingBundlePredicates(BuildingBundleFilter filter, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(filter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);
        if (filter.getBuildingBundleIds() != null && !filter.getBuildingBundleIds().isEmpty()) {
            preds.add(r.get("id").in(filter.getBuildingBundleIds()));
        }
        if (filter.getExternalIds() != null && !filter.getExternalIds().isEmpty()) {
            preds.add(r.get("externalId").in(filter.getExternalIds()));
        }
        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
            preds.add(r.get("status").in(filter.getStatuses()));
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
    public void merge(Object base) { securedBasicRepository.merge(base); }

    @Transactional
    public void massMerge(List<?> toMerge) { securedBasicRepository.massMerge(toMerge); }

    @Transactional
    public void createIndexes() {
        em.createNativeQuery("""
                CREATE UNIQUE INDEX IF NOT EXISTS building_bundle_external_id_unique
                ON BuildingBundle (externalId)
                WHERE softdelete = false AND externalId IS NOT NULL
                """).executeUpdate();
    }
}
