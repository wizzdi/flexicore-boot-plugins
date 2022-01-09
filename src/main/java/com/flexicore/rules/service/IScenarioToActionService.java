package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.ScenarioToAction;
import com.flexicore.rules.request.ScenarioToActionCreate;
import com.flexicore.rules.request.ScenarioToActionFilter;
import com.flexicore.rules.request.ScenarioToActionUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IScenarioToActionService {

  /**
   * @param scenarioToActionCreate Object Used to Create ScenarioActionToScenario
   * @param securityContext
   * @return created ScenarioToAction
   */
  ScenarioToAction createScenarioToAction(
      ScenarioToActionCreate scenarioToActionCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioToActionCreate Object Used to Create ScenarioActionToScenario
   * @param securityContext
   * @return created ScenarioToAction unmerged
   */
  ScenarioToAction createScenarioToActionNoMerge(
      ScenarioToActionCreate scenarioToActionCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioToActionCreate Object Used to Create ScenarioActionToScenario
   * @param scenarioToAction
   * @return if scenarioToAction was updated
   */
  boolean updateScenarioToActionNoMerge(
      ScenarioToAction scenarioToAction, ScenarioToActionCreate scenarioToActionCreate);

  /**
   * @param scenarioToActionUpdate
   * @param securityContext
   * @return scenarioToAction
   */
  ScenarioToAction updateScenarioToAction(
      ScenarioToActionUpdate scenarioToActionUpdate, SecurityContextBase securityContext);

  /**
   * @param scenarioToActionFilter Object Used to List ScenarioActionToScenario
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioToAction
   */
  PaginationResponse<ScenarioToAction> getAllScenarioToActions(
      ScenarioToActionFilter scenarioToActionFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioToActionFilter Object Used to List ScenarioActionToScenario
   * @param securityContext
   * @return List of ScenarioToAction
   */
  List<ScenarioToAction> listAllScenarioToActions(
      ScenarioToActionFilter scenarioToActionFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioToActionFilter Object Used to List ScenarioActionToScenario
   * @param securityContext
   * @throws ResponseStatusException if scenarioToActionFilter is not valid
   */
  void validate(ScenarioToActionFilter scenarioToActionFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioToActionCreate Object Used to Create ScenarioActionToScenario
   * @param securityContext
   * @throws ResponseStatusException if scenarioToActionCreate is not valid
   */
  void validate(ScenarioToActionCreate scenarioToActionCreate, SecurityContextBase securityContext);

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
