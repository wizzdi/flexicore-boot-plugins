package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.ScenarioToDataSource;
import com.flexicore.rules.request.ScenarioToDataSourceCreate;
import com.flexicore.rules.request.ScenarioToDataSourceFilter;
import com.flexicore.rules.request.ScenarioToDataSourceUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IScenarioToDataSourceService {

  /**
   * @param scenarioToDataSourceCreate Object Used to Create ScenarioActionToDataSource
   * @param securityContext
   * @return created ScenarioToDataSource
   */
  ScenarioToDataSource createScenarioToDataSource(
      ScenarioToDataSourceCreate scenarioToDataSourceCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioToDataSourceCreate Object Used to Create ScenarioActionToDataSource
   * @param securityContext
   * @return created ScenarioToDataSource unmerged
   */
  ScenarioToDataSource createScenarioToDataSourceNoMerge(
      ScenarioToDataSourceCreate scenarioToDataSourceCreate, SecurityContextBase securityContext);

  /**
   * @param scenarioToDataSourceCreate Object Used to Create ScenarioActionToDataSource
   * @param scenarioToDataSource
   * @return if scenarioToDataSource was updated
   */
  boolean updateScenarioToDataSourceNoMerge(
      ScenarioToDataSource scenarioToDataSource,
      ScenarioToDataSourceCreate scenarioToDataSourceCreate);

  /**
   * @param scenarioToDataSourceUpdate
   * @param securityContext
   * @return scenarioToDataSource
   */
  ScenarioToDataSource updateScenarioToDataSource(
      ScenarioToDataSourceUpdate scenarioToDataSourceUpdate, SecurityContextBase securityContext);

  /**
   * @param scenarioToDataSourceFilter Object Used to List ScenarioActionToDataSource
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioToDataSource
   */
  PaginationResponse<ScenarioToDataSource> getAllScenarioToDataSources(
      ScenarioToDataSourceFilter scenarioToDataSourceFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioToDataSourceFilter Object Used to List ScenarioActionToDataSource
   * @param securityContext
   * @return List of ScenarioToDataSource
   */
  List<ScenarioToDataSource> listAllScenarioToDataSources(
      ScenarioToDataSourceFilter scenarioToDataSourceFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioToDataSourceFilter Object Used to List ScenarioActionToDataSource
   * @param securityContext
   * @throws ResponseStatusException if scenarioToDataSourceFilter is not valid
   */
  void validate(
      ScenarioToDataSourceFilter scenarioToDataSourceFilter, SecurityContextBase securityContext);

  /**
   * @param scenarioToDataSourceCreate Object Used to Create ScenarioActionToDataSource
   * @param securityContext
   * @throws ResponseStatusException if scenarioToDataSourceCreate is not valid
   */
  void validate(
      ScenarioToDataSourceCreate scenarioToDataSourceCreate, SecurityContextBase securityContext);

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
