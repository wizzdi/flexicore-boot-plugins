package com.flexicore.rules.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Basic_;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.model.ScenarioToAction;
import com.flexicore.rules.model.ScenarioToAction_;
import com.flexicore.rules.request.ScenarioToActionFilter;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Extension
@Component
public class ScenarioToActionRepository implements Plugin {
  @PersistenceContext private EntityManager em;
  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param filtering Object Used to List ScenarioToAction
   * @param securityContext
   * @return List of ScenarioToAction
   */
  public List<ScenarioToAction> listAllScenarioToActions(
          ScenarioToActionFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<ScenarioToAction> q = cb.createQuery(ScenarioToAction.class);
    Root<ScenarioToAction> r = q.from(ScenarioToAction.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioToActionPredicate(filtering, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<ScenarioToAction> query = em.createQuery(q);
    BasicRepository.addPagination(filtering, query);
    return query.getResultList();
  }

  public <T extends ScenarioToAction> void addScenarioToActionPredicate(
          ScenarioToActionFilter scenarioToActionFilter,
          CriteriaBuilder cb,
          CommonAbstractCriteria q,
          From<?, T> r,
          List<Predicate> preds,
          SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(null, cb, q, r, preds, securityContext);

    if (filtering.getEnabled() != null) {

      preds.add(cb.equal(r.get(ScenarioToAction_.enabled), filtering.getEnabled()));
    }
    if (scenarioToActionFilter.getScenarioAction() != null
            && !scenarioToActionFilter.getScenarioAction().isEmpty()) {
      Set<String> ids =
              scenarioToActionFilter.getScenarioAction().parallelStream()
                      .map(f -> f.getId())
                      .collect(Collectors.toSet());
      Join<T, ScenarioAction> join = r.join(ScenarioToAction_.scenarioAction);
      preds.add(join.get(Basic_.id).in(ids));
    }

    if (scenarioToActionFilter.getScenario() != null
            && !scenarioToActionFilter.getScenario().isEmpty()) {
      Set<String> ids =
              scenarioToActionFilter.getScenario().parallelStream()
                      .map(f -> f.getId())
                      .collect(Collectors.toSet());
      Join<T, Scenario> join = r.join(ScenarioToAction_.scenario);
      preds.add(join.get(Basic_.id).in(ids));
    }
  }
  /**
   * @param filtering Object Used to List ScenarioToAction
   * @param securityContext
   * @return count of ScenarioToAction
   */
  public Long countAllScenarioToActions(
          ScenarioToActionFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<ScenarioToAction> r = q.from(ScenarioToAction.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioToActionPredicate(filtering, cb, q, r, preds, securityContext);
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
