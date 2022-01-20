package com.flexicore.rules.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Basic_;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.model.ScenarioTrigger_;
import com.flexicore.rules.request.ScenarioTriggerFilter;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.model.FileResource;
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
public class ScenarioTriggerRepository implements Plugin {
  @PersistenceContext private EntityManager em;
  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param filtering Object Used to List ScenarioTrigger
   * @param securityContext
   * @return List of ScenarioTrigger
   */
  public List<ScenarioTrigger> listAllScenarioTriggers(
          ScenarioTriggerFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<ScenarioTrigger> q = cb.createQuery(ScenarioTrigger.class);
    Root<ScenarioTrigger> r = q.from(ScenarioTrigger.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioTriggerPredicate(filtering, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<ScenarioTrigger> query = em.createQuery(q);
    BasicRepository.addPagination(filtering, query);
    return query.getResultList();
  }

  public <T extends ScenarioTrigger> void addScenarioTriggerPredicate(
          ScenarioTriggerFilter scenarioTriggerFilter,
          CriteriaBuilder cb,
          CommonAbstractCriteria q,
          From<?, T> r,
          List<Predicate> preds,
          SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(null, cb, q, r, preds, securityContext);

    if (scenarioTriggerFilter.getLastEventId() != null
            && !scenarioTriggerFilter.getLastEventId().isEmpty()) {
      preds.add(r.get(ScenarioTrigger_.lastEventId).in(scenarioTriggerFilter.getLastEventId()));
    }

    if (scenarioTriggerFilter.getLastActivated() != null
            && !scenarioTriggerFilter.getLastActivated().isEmpty()) {
      preds.add(r.get(ScenarioTrigger_.lastActivated).in(scenarioTriggerFilter.getLastActivated()));
    }

    if (scenarioTriggerFilter.getValidFrom() != null
            && !scenarioTriggerFilter.getValidFrom().isEmpty()) {
      preds.add(r.get(ScenarioTrigger_.validFrom).in(scenarioTriggerFilter.getValidFrom()));
    }

    if (scenarioTriggerFilter.getCooldownIntervalMs() != null
            && !scenarioTriggerFilter.getCooldownIntervalMs().isEmpty()) {
      preds.add(
              r.get(ScenarioTrigger_.cooldownIntervalMs)
                      .in(scenarioTriggerFilter.getCooldownIntervalMs()));
    }

    if (scenarioTriggerFilter.getActiveTill() != null
            && !scenarioTriggerFilter.getActiveTill().isEmpty()) {
      preds.add(r.get(ScenarioTrigger_.activeTill).in(scenarioTriggerFilter.getActiveTill()));
    }

    if (scenarioTriggerFilter.getActiveMs() != null
            && !scenarioTriggerFilter.getActiveMs().isEmpty()) {
      preds.add(r.get(ScenarioTrigger_.activeMs).in(scenarioTriggerFilter.getActiveMs()));
    }

    if (scenarioTriggerFilter.getLogFileResource() != null
            && !scenarioTriggerFilter.getLogFileResource().isEmpty()) {
      Set<String> ids =
              scenarioTriggerFilter.getLogFileResource().parallelStream()
                      .map(f -> f.getId())
                      .collect(Collectors.toSet());
      Join<T, FileResource> join = r.join(ScenarioTrigger_.logFileResource);
      preds.add(join.get(Basic_.id).in(ids));
    }

    if (scenarioTriggerFilter.getEvaluatingJSCode() != null
            && !scenarioTriggerFilter.getEvaluatingJSCode().isEmpty()) {
      Set<String> ids =
              scenarioTriggerFilter.getEvaluatingJSCode().parallelStream()
                      .map(f -> f.getId())
                      .collect(Collectors.toSet());
      Join<T, FileResource> join = r.join(ScenarioTrigger_.evaluatingJSCode);
      preds.add(join.get(Basic_.id).in(ids));
    }

    if (scenarioTriggerFilter.getScenarioTriggerType() != null
            && !scenarioTriggerFilter.getScenarioTriggerType().isEmpty()) {
      Set<String> ids =
              scenarioTriggerFilter.getScenarioTriggerType().parallelStream()
                      .map(f -> f.getId())
                      .collect(Collectors.toSet());
      Join<T, ScenarioTriggerType> join = r.join(ScenarioTrigger_.scenarioTriggerType);
      preds.add(join.get(Basic_.id).in(ids));
    }

    if (scenarioTriggerFilter.getValidTill() != null
            && !scenarioTriggerFilter.getValidTill().isEmpty()) {
      preds.add(r.get(ScenarioTrigger_.validTill).in(scenarioTriggerFilter.getValidTill()));
    }
    if (filtering.getEventCanonicalNames() != null && !filtering.getEventCanonicalNames().isEmpty()) {
      Join<T,ScenarioTriggerType> join=r.join(ScenarioTrigger_.scenarioTriggerType);
      preds.add(join.get(ScenarioTriggerType_.eventCanonicalName).in(filtering.getEventCanonicalNames()));
    }
  }
  /**
   * @param filtering Object Used to List ScenarioTrigger
   * @param securityContext
   * @return count of ScenarioTrigger
   */
  public Long countAllScenarioTriggers(
          ScenarioTriggerFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<ScenarioTrigger> r = q.from(ScenarioTrigger.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioTriggerPredicate(filtering, cb, q, r, preds, securityContext);
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
