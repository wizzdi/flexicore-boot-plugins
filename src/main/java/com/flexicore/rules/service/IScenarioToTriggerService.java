package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.ScenarioToTrigger;
import com.flexicore.rules.request.ScenarioToTriggerCreate;
import com.flexicore.rules.request.ScenarioToTriggerFilter;
import com.flexicore.rules.request.ScenarioToTriggerUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IScenarioToTriggerService {

  /**
   * @param scenarioToTriggerCreate Object Used to Create ScenarioToScenarioTrigger
   * @param securityContext
   * @return created ScenarioToTrigger
   */
  ScenarioToTrigger createScenarioToTrigger(
      ScenarioToTriggerCreate scenarioToTriggerCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioToTriggerCreate Object Used to Create ScenarioToScenarioTrigger
   * @param securityContext
   * @return created ScenarioToTrigger unmerged
   */
  ScenarioToTrigger createScenarioToTriggerNoMerge(
      ScenarioToTriggerCreate scenarioToTriggerCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioToTriggerCreate Object Used to Create ScenarioToScenarioTrigger
   * @param scenarioToTrigger
   * @return if scenarioToTrigger was updated
   */
  boolean updateScenarioToTriggerNoMerge(
      ScenarioToTrigger scenarioToTrigger, ScenarioToTriggerCreate scenarioToTriggerCreate);

  /**
   * @param scenarioToTriggerUpdate
   * @param securityContext
   * @return scenarioToTrigger
   */
  ScenarioToTrigger updateScenarioToTrigger(
      ScenarioToTriggerUpdate scenarioToTriggerUpdate, SecurityContextBase securityContext);

  /**
   * @param scenarioToTriggerFilter Object Used to List ScenarioToScenarioTrigger
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioToTrigger
   */
  PaginationResponse<ScenarioToTrigger> getAllScenarioToTriggers(
      ScenarioToTriggerFilter scenarioToTriggerFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioToTriggerFilter Object Used to List ScenarioToScenarioTrigger
   * @param securityContext
   * @return List of ScenarioToTrigger
   */
  List<ScenarioToTrigger> listAllScenarioToTriggers(
      ScenarioToTriggerFilter scenarioToTriggerFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioToTriggerFilter Object Used to List ScenarioToScenarioTrigger
   * @param securityContext
   * @throws ResponseStatusException if scenarioToTriggerFilter is not valid
   */
  void validate(
      ScenarioToTriggerFilter scenarioToTriggerFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioToTriggerCreate Object Used to Create ScenarioToScenarioTrigger
   * @param securityContext
   * @throws ResponseStatusException if scenarioToTriggerCreate is not valid
   */
  void validate(
      ScenarioToTriggerCreate scenarioToTriggerCreate, SecurityContextBase securityContext);

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
