package com.flexicore.ui.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.ui.model.GridPresetStyle;
import com.flexicore.ui.request.GridPresetStyleFiltering;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class GridPresetStyleRepository implements Plugin {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<GridPresetStyle> listAllGridPresetStyles(GridPresetStyleFiltering filtering,
                                                          SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GridPresetStyle> queryDefinition = cb.createQuery(GridPresetStyle.class);
        Root<GridPresetStyle> root = queryDefinition.from(GridPresetStyle.class);
        List<Predicate> predicates = new ArrayList<>();
        addGridPresetStylePredicates(predicates, cb, queryDefinition, root, filtering, securityContext);
        queryDefinition.select(root).where(predicates.toArray(new Predicate[0]));
        TypedQuery<GridPresetStyle> query = em.createQuery(queryDefinition);
        BasicRepository.addPagination(filtering, query);
        return query.getResultList();
    }

    public <T extends GridPresetStyle> void addGridPresetStylePredicates(List<Predicate> predicates,
                                                                         CriteriaBuilder cb,
                                                                         CommonAbstractCriteria query,
                                                                         From<?, T> root,
                                                                         GridPresetStyleFiltering filtering,
                                                                         SecurityContext securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(
                filtering.getBasicPropertiesFilter(), cb, query, root, predicates, securityContext);
    }

    public long countAllGridPresetStyles(GridPresetStyleFiltering filtering,
                                         SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> queryDefinition = cb.createQuery(Long.class);
        Root<GridPresetStyle> root = queryDefinition.from(GridPresetStyle.class);
        List<Predicate> predicates = new ArrayList<>();
        addGridPresetStylePredicates(predicates, cb, queryDefinition, root, filtering, securityContext);
        queryDefinition.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(queryDefinition).getSingleResult();
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> type, Set<String> ids,
                                                    SecurityContext securityContext) {
        return securedBasicRepository.listByIds(type, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> type,
                                                 SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, type, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
            String id, Class<T> type, SingularAttribute<D, E> baseclassAttribute,
            SecurityContext securityContext) {
        return securedBasicRepository.getByIdOrNull(id, type, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
            Class<T> type, Set<String> ids, SingularAttribute<D, E> baseclassAttribute,
            SecurityContext securityContext) {
        return securedBasicRepository.listByIds(type, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(
            Class<T> type, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return securedBasicRepository.findByIds(type, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> type, Set<String> ids) {
        return securedBasicRepository.findByIds(type, ids);
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
