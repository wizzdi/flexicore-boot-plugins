package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.request.ScenarioActionCreate;
import com.flexicore.rules.request.ScenarioActionFilter;
import com.flexicore.rules.request.ScenarioActionUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IScenarioActionService {

  /**
   * @param scenarioActionCreate Object Used to Create ScenarioAction
   * @param securityContext
   * @return created ScenarioAction
   */
  ScenarioAction createScenarioAction(
      ScenarioActionCreate scenarioActionCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioActionCreate Object Used to Create ScenarioAction
   * @param securityContext
   * @return created ScenarioAction unmerged
   */
  ScenarioAction createScenarioActionNoMerge(
      ScenarioActionCreate scenarioActionCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioActionCreate Object Used to Create ScenarioAction
   * @param scenarioAction
   * @return if scenarioAction was updated
   */
  boolean updateScenarioActionNoMerge(
      ScenarioAction scenarioAction, ScenarioActionCreate scenarioActionCreate);

  /**
   * @param scenarioActionUpdate
   * @param securityContext
   * @return scenarioAction
   */
  ScenarioAction updateScenarioAction(
      ScenarioActionUpdate scenarioActionUpdate, SecurityContextBase securityContext);

  /**
   * @param scenarioActionFilter Object Used to List ScenarioAction
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioAction
   */
  PaginationResponse<ScenarioAction> getAllScenarioActions(
      ScenarioActionFilter scenarioActionFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioActionFilter Object Used to List ScenarioAction
   * @param securityContext
   * @return List of ScenarioAction
   */
  List<ScenarioAction> listAllScenarioActions(
      ScenarioActionFilter scenarioActionFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioActionFilter Object Used to List ScenarioAction
   * @param securityContext
   * @throws ResponseStatusException if scenarioActionFilter is not valid
   */
  void validate(ScenarioActionFilter scenarioActionFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioActionCreate Object Used to Create ScenarioAction
   * @param securityContext
   * @throws ResponseStatusException if scenarioActionCreate is not valid
   */
  void validate(ScenarioActionCreate scenarioActionCreate, SecurityContextBase securityContext);

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
