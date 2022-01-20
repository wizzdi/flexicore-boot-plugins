package com.flexicore.rules.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Basic_;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioToTrigger;
import com.flexicore.rules.model.ScenarioToTrigger_;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.request.ScenarioToTriggerFilter;
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
public class ScenarioToTriggerRepository implements Plugin {
  @PersistenceContext private EntityManager em;
  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param filtering Object Used to List ScenarioToTrigger
   * @param securityContext
   * @return List of ScenarioToTrigger
   */
  public List<ScenarioToTrigger> listAllScenarioToTriggers(
          ScenarioToTriggerFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<ScenarioToTrigger> q = cb.createQuery(ScenarioToTrigger.class);
    Root<ScenarioToTrigger> r = q.from(ScenarioToTrigger.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioToTriggerPredicate(filtering, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<ScenarioToTrigger> query = em.createQuery(q);
    BasicRepository.addPagination(filtering, query);
    return query.getResultList();
  }

  public <T extends ScenarioToTrigger> void addScenarioToTriggerPredicate(
          ScenarioToTriggerFilter scenarioToTriggerFilter,
          CriteriaBuilder cb,
          CommonAbstractCriteria q,
          From<?, T> r,
          List<Predicate> preds,
          SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(null, cb, q, r, preds, securityContext);

    if (filtering.getFiring() != null ) {

      preds.add(cb.equal(r.get(ScenarioToTrigger_.firing),filtering.getFiring()));
    }
    if (filtering.getEnabled() != null ) {

      preds.add(cb.equal(r.get(ScenarioToTrigger_.enabled),filtering.getEnabled()));
    }
    if (filtering.getNonDeletedScenarios() != null ) {
      Join<T, Scenario> join = r.join(ScenarioToTrigger_.scenario);

      preds.add(cb.isFalse(join.get(Scenario_.softDelete)));
    }

    if (scenarioToTriggerFilter.getScenarioTrigger() != null
            && !scenarioToTriggerFilter.getScenarioTrigger().isEmpty()) {
      Set<String> ids =
              scenarioToTriggerFilter.getScenarioTrigger().parallelStream()
                      .map(f -> f.getId())
                      .collect(Collectors.toSet());
      Join<T, ScenarioTrigger> join = r.join(ScenarioToTrigger_.scenarioTrigger);
      preds.add(join.get(Basic_.id).in(ids));
    }

    if (scenarioToTriggerFilter.getScenario() != null
            && !scenarioToTriggerFilter.getScenario().isEmpty()) {
      Set<String> ids =
              scenarioToTriggerFilter.getScenario().parallelStream()
                      .map(f -> f.getId())
                      .collect(Collectors.toSet());
      Join<T, Scenario> join = r.join(ScenarioToTrigger_.scenario);
      preds.add(join.get(Basic_.id).in(ids));
    }

    if (scenarioToTriggerFilter.getOrdinal() != null
            && !scenarioToTriggerFilter.getOrdinal().isEmpty()) {
      preds.add(r.get(ScenarioToTrigger_.ordinal).in(scenarioToTriggerFilter.getOrdinal()));
    }


  }
  /**
   * @param filtering Object Used to List ScenarioToTrigger
   * @param securityContext
   * @return count of ScenarioToTrigger
   */
  public Long countAllScenarioToTriggers(
          ScenarioToTriggerFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<ScenarioToTrigger> r = q.from(ScenarioToTrigger.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioToTriggerPredicate(filtering, cb, q, r, preds, securityContext);
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
