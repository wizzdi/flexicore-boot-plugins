package com.flexicore.rules.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Basic_;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.model.ScenarioAction_;
import com.flexicore.rules.request.ScenarioActionFilter;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
public class ScenarioActionRepository implements Plugin {
  @PersistenceContext private EntityManager em;
  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param scenarioActionFilter Object Used to List ScenarioAction
   * @param securityContext
   * @return List of ScenarioAction
   */
  public List<ScenarioAction> listAllScenarioActions(
      ScenarioActionFilter scenarioActionFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<ScenarioAction> q = cb.createQuery(ScenarioAction.class);
    Root<ScenarioAction> r = q.from(ScenarioAction.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioActionPredicate(scenarioActionFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0])).orderBy(cb.asc(r.get(ScenarioAction_.name)));
    TypedQuery<ScenarioAction> query = em.createQuery(q);
    BasicRepository.addPagination(scenarioActionFilter, query);
    return query.getResultList();
  }

  public <T extends ScenarioAction> void addScenarioActionPredicate(
      ScenarioActionFilter scenarioActionFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        scenarioActionFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (scenarioActionFilter.getDynamicExecution() != null
        && !scenarioActionFilter.getDynamicExecution().isEmpty()) {
      Set<String> ids =
          scenarioActionFilter.getDynamicExecution().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, DynamicExecution> join = r.join(ScenarioAction_.dynamicExecution);
      preds.add(join.get(Basic_.id).in(ids));
    }
  }
  /**
   * @param scenarioActionFilter Object Used to List ScenarioAction
   * @param securityContext
   * @return count of ScenarioAction
   */
  public Long countAllScenarioActions(
      ScenarioActionFilter scenarioActionFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<ScenarioAction> r = q.from(ScenarioAction.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioActionPredicate(scenarioActionFilter, cb, q, r, preds, securityContext);
    q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
    TypedQuery<Long> query = em.createQuery(q);
    return query.getSingleResult();
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return securedBasicRepository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return securedBasicRepository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
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
