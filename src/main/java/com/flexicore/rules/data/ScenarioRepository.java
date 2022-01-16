package com.flexicore.rules.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Basic_;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.Scenario_;
import com.flexicore.rules.request.ScenarioFilter;
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
public class ScenarioRepository implements Plugin {
  @PersistenceContext private EntityManager em;
  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param filtering Object Used to List Scenario
   * @param securityContext
   * @return List of Scenario
   */
  public List<Scenario> listAllScenarios(
      ScenarioFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Scenario> q = cb.createQuery(Scenario.class);
    Root<Scenario> r = q.from(Scenario.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioPredicate(filtering, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<Scenario> query = em.createQuery(q);
    BasicRepository.addPagination(filtering, query);
    return query.getResultList();
  }

  public <T extends Scenario> void addScenarioPredicate(
      ScenarioFilter scenarioFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        scenarioFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (scenarioFilter.getEvaluatingJSCode() != null
        && !scenarioFilter.getEvaluatingJSCode().isEmpty()) {
      Set<String> ids =
          scenarioFilter.getEvaluatingJSCode().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, FileResource> join = r.join(Scenario_.evaluatingJSCode);
      preds.add(join.get(Basic_.id).in(ids));
    }

    if (scenarioFilter.getLogFileResource() != null
        && !scenarioFilter.getLogFileResource().isEmpty()) {
      Set<String> ids =
          scenarioFilter.getLogFileResource().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, FileResource> join = r.join(Scenario_.logFileResource);
      preds.add(join.get(Basic_.id).in(ids));
    }

    if (scenarioFilter.getScenarioHint() != null && !scenarioFilter.getScenarioHint().isEmpty()) {
      preds.add(r.get(Scenario_.scenarioHint).in(scenarioFilter.getScenarioHint()));
    }
  }
  /**
   * @param filtering Object Used to List Scenario
   * @param securityContext
   * @return count of Scenario
   */
  public Long countAllScenarios(ScenarioFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<Scenario> r = q.from(Scenario.class);
    List<Predicate> preds = new ArrayList<>();
    addScenarioPredicate(filtering, cb, q, r, preds, securityContext);
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
