package com.flexicore.rules.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.model.ScenarioTriggerType_;
import com.flexicore.rules.request.ScenarioTriggerTypeFilter;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Extension
@Component
public class ScenarioTriggerTypeRepository implements Plugin {
  @PersistenceContext private EntityManager em;
  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param scenarioTriggerTypeFilter Object Used to List ScenarioTriggerType
   * @param securityContext
   * @return List of ScenarioTriggerType
   */
  public List<ScenarioTriggerType> listAllScenarioTriggerTypes(
      ScenarioTriggerTypeFilter scenarioTriggerTypeFilter, SecurityContext securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<ScenarioTriggerType> q = cb.createQuery(ScenarioTriggerType.class);
    Root<ScenarioTriggerType> r = q.from(ScenarioTriggerType.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioTriggerTypePredicate(scenarioTriggerTypeFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0])).orderBy(cb.asc(r.get(ScenarioTriggerType_.name)));
    TypedQuery<ScenarioTriggerType> query = em.createQuery(q);
    BasicRepository.addPagination(scenarioTriggerTypeFilter, query);
    return query.getResultList();
  }

  public <T extends ScenarioTriggerType> void addScenarioTriggerTypePredicate(
      ScenarioTriggerTypeFilter scenarioTriggerTypeFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContext securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        scenarioTriggerTypeFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (scenarioTriggerTypeFilter.getEventCanonicalName() != null
        && !scenarioTriggerTypeFilter.getEventCanonicalName().isEmpty()) {
      preds.add(
          r.get(ScenarioTriggerType_.eventCanonicalName)
              .in(scenarioTriggerTypeFilter.getEventCanonicalName()));
    }
  }
  /**
   * @param scenarioTriggerTypeFilter Object Used to List ScenarioTriggerType
   * @param securityContext
   * @return count of ScenarioTriggerType
   */
  public Long countAllScenarioTriggerTypes(
      ScenarioTriggerTypeFilter scenarioTriggerTypeFilter, SecurityContext securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<ScenarioTriggerType> r = q.from(ScenarioTriggerType.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioTriggerTypePredicate(scenarioTriggerTypeFilter, cb, q, r, preds, securityContext);
    q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
    TypedQuery<Long> query = em.createQuery(q);
    return query.getSingleResult();
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContext securityContext) {
    return securedBasicRepository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContext securityContext) {
    return securedBasicRepository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
    return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
    return securedBasicRepository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return securedBasicRepository.findByIds(c, ids, idAttribute);
  }

  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return securedBasicRepository.findByIds(c, requested);
  }

  public <T> T findByIdOrNull(Class<T> type, String id) {
    return securedBasicRepository.findByIdOrNull(type, id);
  }

  @Transactional
  public void merge(java.lang.Object base) {
    securedBasicRepository.merge(base);
  }

  @Transactional
  public void massMerge(List<?> toMerge) {
    securedBasicRepository.massMerge(toMerge);
  }
}
