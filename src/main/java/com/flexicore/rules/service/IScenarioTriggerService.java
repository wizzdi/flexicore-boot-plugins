package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.request.ScenarioTriggerCreate;
import com.flexicore.rules.request.ScenarioTriggerFilter;
import com.flexicore.rules.request.ScenarioTriggerUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IScenarioTriggerService {

  /**
   * @param scenarioTriggerCreate Object Used to Create ScenarioTrigger
   * @param securityContext
   * @return created ScenarioTrigger
   */
  ScenarioTrigger createScenarioTrigger(
      ScenarioTriggerCreate scenarioTriggerCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerCreate Object Used to Create ScenarioTrigger
   * @param securityContext
   * @return created ScenarioTrigger unmerged
   */
  ScenarioTrigger createScenarioTriggerNoMerge(
      ScenarioTriggerCreate scenarioTriggerCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerCreate Object Used to Create ScenarioTrigger
   * @param scenarioTrigger
   * @return if scenarioTrigger was updated
   */
  boolean updateScenarioTriggerNoMerge(
      ScenarioTrigger scenarioTrigger, ScenarioTriggerCreate scenarioTriggerCreate);

  /**
   * @param scenarioTriggerUpdate
   * @param securityContext
   * @return scenarioTrigger
   */
  ScenarioTrigger updateScenarioTrigger(
      ScenarioTriggerUpdate scenarioTriggerUpdate, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerFilter Object Used to List ScenarioTrigger
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioTrigger
   */
  PaginationResponse<ScenarioTrigger> getAllScenarioTriggers(
      ScenarioTriggerFilter scenarioTriggerFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerFilter Object Used to List ScenarioTrigger
   * @param securityContext
   * @return List of ScenarioTrigger
   */
  List<ScenarioTrigger> listAllScenarioTriggers(
      ScenarioTriggerFilter scenarioTriggerFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerFilter Object Used to List ScenarioTrigger
   * @param securityContext
   * @throws ResponseStatusException if scenarioTriggerFilter is not valid
   */
  void validate(ScenarioTriggerFilter scenarioTriggerFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerCreate Object Used to Create ScenarioTrigger
   * @param securityContext
   * @throws ResponseStatusException if scenarioTriggerCreate is not valid
   */
  void validate(ScenarioTriggerCreate scenarioTriggerCreate, SecurityContextBase securityContext);

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
