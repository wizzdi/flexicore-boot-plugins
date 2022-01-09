package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.request.ScenarioTriggerTypeCreate;
import com.flexicore.rules.request.ScenarioTriggerTypeFilter;
import com.flexicore.rules.request.ScenarioTriggerTypeUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IScenarioTriggerTypeService {

  /**
   * @param scenarioTriggerTypeCreate Object Used to Create ScenarioTriggerType
   * @param securityContext
   * @return created ScenarioTriggerType
   */
  ScenarioTriggerType createScenarioTriggerType(
      ScenarioTriggerTypeCreate scenarioTriggerTypeCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerTypeCreate Object Used to Create ScenarioTriggerType
   * @param securityContext
   * @return created ScenarioTriggerType unmerged
   */
  ScenarioTriggerType createScenarioTriggerTypeNoMerge(
      ScenarioTriggerTypeCreate scenarioTriggerTypeCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerTypeCreate Object Used to Create ScenarioTriggerType
   * @param scenarioTriggerType
   * @return if scenarioTriggerType was updated
   */
  boolean updateScenarioTriggerTypeNoMerge(
      ScenarioTriggerType scenarioTriggerType, ScenarioTriggerTypeCreate scenarioTriggerTypeCreate);

  /**
   * @param scenarioTriggerTypeUpdate
   * @param securityContext
   * @return scenarioTriggerType
   */
  ScenarioTriggerType updateScenarioTriggerType(
      ScenarioTriggerTypeUpdate scenarioTriggerTypeUpdate, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerTypeFilter Object Used to List ScenarioTriggerType
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioTriggerType
   */
  PaginationResponse<ScenarioTriggerType> getAllScenarioTriggerTypes(
      ScenarioTriggerTypeFilter scenarioTriggerTypeFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerTypeFilter Object Used to List ScenarioTriggerType
   * @param securityContext
   * @return List of ScenarioTriggerType
   */
  List<ScenarioTriggerType> listAllScenarioTriggerTypes(
      ScenarioTriggerTypeFilter scenarioTriggerTypeFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerTypeFilter Object Used to List ScenarioTriggerType
   * @param securityContext
   * @throws ResponseStatusException if scenarioTriggerTypeFilter is not valid
   */
  void validate(
      ScenarioTriggerTypeFilter scenarioTriggerTypeFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioTriggerTypeCreate Object Used to Create ScenarioTriggerType
   * @param securityContext
   * @throws ResponseStatusException if scenarioTriggerTypeCreate is not valid
   */
  void validate(
      ScenarioTriggerTypeCreate scenarioTriggerTypeCreate, SecurityContextBase securityContext);

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
