package com.flexicore.ui.dashboard.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.ui.dashboard.model.DashboardPreset;
import com.flexicore.ui.dashboard.model.DashboardPreset_;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.model.GridLayout_;
import com.flexicore.ui.dashboard.request.DashboardPresetFilter;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Extension
@Component
public class DashboardPresetRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<DashboardPreset> listAllDashboardPreset(DashboardPresetFilter dashboardPresetFilter,
                                                        SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DashboardPreset> q = cb.createQuery(DashboardPreset.class);
        Root<DashboardPreset> r = q.from(DashboardPreset.class);
        List<Predicate> preds = new ArrayList<>();
        addDashboardPresetPredicates(preds, cb, q, r, dashboardPresetFilter, securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<DashboardPreset> query = em.createQuery(q);
        BasicRepository.addPagination(dashboardPresetFilter, query);
        return query.getResultList();
    }

    public <T extends DashboardPreset> void addDashboardPresetPredicates(List<Predicate> preds, CriteriaBuilder cb,
                                                                         CommonAbstractCriteria q, From<?,T> r, DashboardPresetFilter dashboardPresetFilter, SecurityContextBase securityContext) {

    	securedBasicRepository.addSecuredBasicPredicates(null,cb,q,r,preds,securityContext);
        if (dashboardPresetFilter.getGridLayouts() != null && !dashboardPresetFilter.getGridLayouts().isEmpty()) {
            Set<String> ids = dashboardPresetFilter.getGridLayouts().stream().map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, GridLayout> join = r.join(DashboardPreset_.gridLayout);
            preds.add(join.get(GridLayout_.id).in(ids));
        }


    }

    public long countAllDashboardPreset(DashboardPresetFilter dashboardPresetFilter,
                                        SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<DashboardPreset> r = q.from(DashboardPreset.class);
        List<Predicate> preds = new ArrayList<>();
        addDashboardPresetPredicates(preds, cb, q, r, dashboardPresetFilter, securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query = em.createQuery(q);
        return query.getSingleResult();
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
