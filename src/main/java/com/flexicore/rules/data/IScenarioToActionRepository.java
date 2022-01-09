package com.flexicore.rules.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.ScenarioToAction;
import com.flexicore.rules.request.ScenarioToActionFilter;
import com.flexicore.security.SecurityContextBase;
import java.util.List;
import java.util.Set;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;

public interface IScenarioToActionRepository {

  /**
   * @param filtering Object Used to List ScenarioActionToScenario
   * @param securityContext
   * @return List of ScenarioToAction
   */
  List<ScenarioToAction> listAllScenarioToActions(
      ScenarioToActionFilter filtering, SecurityContextBase securityContext);

  <T extends ScenarioToAction> void addScenarioToActionPredicate(
      ScenarioToActionFilter filtering,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext);

  /**
   * @param filtering Object Used to List ScenarioActionToScenario
   * @param securityContext
   * @return count of ScenarioToAction
   */
  Long countAllScenarioToActions(
      ScenarioToActionFilter filtering, SecurityContextBase securityContext);

  <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext);

  <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext);

  <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext);

  <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext);

  <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute);

  <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested);

  <T> T findByIdOrNull(Class<T> type, String id);

  void merge(java.lang.Object base);

  void massMerge(List<?> toMerge);
}
