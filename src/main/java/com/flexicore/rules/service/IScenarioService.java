package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.request.ScenarioCreate;
import com.flexicore.rules.request.ScenarioFilter;
import com.flexicore.rules.request.ScenarioUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IScenarioService {

  /**
   * @param scenarioCreate Object Used to Create Scenario
   * @param securityContext
   * @return created Scenario
   */
  Scenario createScenario(ScenarioCreate scenarioCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioCreate Object Used to Create Scenario
   * @param securityContext
   * @return created Scenario unmerged
   */
  Scenario createScenarioNoMerge(
      ScenarioCreate scenarioCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioCreate Object Used to Create Scenario
   * @param scenario
   * @return if scenario was updated
   */
  boolean updateScenarioNoMerge(Scenario scenario, ScenarioCreate scenarioCreate);

  /**
   * @param scenarioUpdate
   * @param securityContext
   * @return scenario
   */
  Scenario updateScenario(ScenarioUpdate scenarioUpdate, SecurityContextBase securityContext);

  /**
   * @param scenarioFilter Object Used to List Scenario
   * @param securityContext
   * @return PaginationResponse containing paging information for Scenario
   */
  PaginationResponse<Scenario> getAllScenarios(
      ScenarioFilter scenarioFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioFilter Object Used to List Scenario
   * @param securityContext
   * @return List of Scenario
   */
  List<Scenario> listAllScenarios(
      ScenarioFilter scenarioFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioFilter Object Used to List Scenario
   * @param securityContext
   * @throws ResponseStatusException if scenarioFilter is not valid
   */
  void validate(ScenarioFilter scenarioFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioCreate Object Used to Create Scenario
   * @param securityContext
   * @throws ResponseStatusException if scenarioCreate is not valid
   */
  void validate(ScenarioCreate scenarioCreate, SecurityContextBase securityContext);

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
