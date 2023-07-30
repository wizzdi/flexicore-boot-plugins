package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.data.ScenarioToDataSourceRepository;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioToDataSource;
import com.flexicore.rules.request.ScenarioToDataSourceCreate;
import com.flexicore.rules.request.ScenarioToDataSourceFilter;
import com.flexicore.rules.request.ScenarioToDataSourceUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class ScenarioToDataSourceService implements Plugin {

  @Autowired private ScenarioToDataSourceRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param scenarioToDataSourceCreate Object Used to Create ScenarioToDataSource
   * @param securityContext
   * @return created ScenarioToDataSource
   */
  public ScenarioToDataSource createScenarioToDataSource(
      ScenarioToDataSourceCreate scenarioToDataSourceCreate, SecurityContextBase securityContext) {
    ScenarioToDataSource scenarioToDataSource =
        createScenarioToDataSourceNoMerge(scenarioToDataSourceCreate, securityContext);
    this.repository.merge(scenarioToDataSource);
    return scenarioToDataSource;
  }

  /**
   * @param scenarioToDataSourceCreate Object Used to Create ScenarioToDataSource
   * @param securityContext
   * @return created ScenarioToDataSource unmerged
   */
  public ScenarioToDataSource createScenarioToDataSourceNoMerge(
      ScenarioToDataSourceCreate scenarioToDataSourceCreate, SecurityContextBase securityContext) {
    ScenarioToDataSource scenarioToDataSource = new ScenarioToDataSource();
    scenarioToDataSource.setId(UUID.randomUUID().toString());
    updateScenarioToDataSourceNoMerge(scenarioToDataSource, scenarioToDataSourceCreate);

    BaseclassService.createSecurityObjectNoMerge(scenarioToDataSource, securityContext);

    return scenarioToDataSource;
  }

  /**
   * @param scenarioToDataSourceCreate Object Used to Create ScenarioToDataSource
   * @param scenarioToDataSource
   * @return if scenarioToDataSource was updated
   */
  public boolean updateScenarioToDataSourceNoMerge(
      ScenarioToDataSource scenarioToDataSource,
      ScenarioToDataSourceCreate scenarioToDataSourceCreate) {
    boolean update =
        basicService.updateBasicNoMerge(scenarioToDataSourceCreate, scenarioToDataSource);

    if (scenarioToDataSourceCreate.getEnabled() != null
        && (!scenarioToDataSourceCreate.getEnabled().equals(scenarioToDataSource.isEnabled()))) {
      scenarioToDataSource.setEnabled(scenarioToDataSourceCreate.getEnabled());
      update = true;
    }

    if (scenarioToDataSourceCreate.getDataSource() != null
        && (scenarioToDataSource.getDataSource() == null
            || !scenarioToDataSourceCreate
                .getDataSource()
                .getId()
                .equals(scenarioToDataSource.getDataSource().getId()))) {
      scenarioToDataSource.setDataSource(scenarioToDataSourceCreate.getDataSource());
      update = true;
    }

    if (scenarioToDataSourceCreate.getOrdinal() != null
        && (!scenarioToDataSourceCreate.getOrdinal().equals(scenarioToDataSource.getOrdinal()))) {
      scenarioToDataSource.setOrdinal(scenarioToDataSourceCreate.getOrdinal());
      update = true;
    }

    if (scenarioToDataSourceCreate.getScenario() != null
        && (scenarioToDataSource.getScenario() == null
            || !scenarioToDataSourceCreate
                .getScenario()
                .getId()
                .equals(scenarioToDataSource.getScenario().getId()))) {
      scenarioToDataSource.setScenario(scenarioToDataSourceCreate.getScenario());
      update = true;
    }

    return update;
  }
  /**
   * @param scenarioToDataSourceUpdate
   * @param securityContext
   * @return scenarioToDataSource
   */
  public ScenarioToDataSource updateScenarioToDataSource(
      ScenarioToDataSourceUpdate scenarioToDataSourceUpdate, SecurityContextBase securityContext) {
    ScenarioToDataSource scenarioToDataSource =
        scenarioToDataSourceUpdate.getScenarioToDataSource();
    if (updateScenarioToDataSourceNoMerge(scenarioToDataSource, scenarioToDataSourceUpdate)) {
      this.repository.merge(scenarioToDataSource);
    }
    return scenarioToDataSource;
  }

  /**
   * @param scenarioToDataSourceFilter Object Used to List ScenarioToDataSource
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioToDataSource
   */
  public PaginationResponse<ScenarioToDataSource> getAllScenarioToDataSources(
      ScenarioToDataSourceFilter scenarioToDataSourceFilter, SecurityContextBase securityContext) {
    List<ScenarioToDataSource> list =
        listAllScenarioToDataSources(scenarioToDataSourceFilter, securityContext);
    long count =
        this.repository.countAllScenarioToDataSources(scenarioToDataSourceFilter, securityContext);
    return new PaginationResponse<>(list, scenarioToDataSourceFilter, count);
  }

  /**
   * @param scenarioToDataSourceFilter Object Used to List ScenarioToDataSource
   * @param securityContext
   * @return List of ScenarioToDataSource
   */
  public List<ScenarioToDataSource> listAllScenarioToDataSources(
      ScenarioToDataSourceFilter scenarioToDataSourceFilter, SecurityContextBase securityContext) {
    return this.repository.listAllScenarioToDataSources(
        scenarioToDataSourceFilter, securityContext);
  }

  /**
   * @param scenarioToDataSourceFilter Object Used to List ScenarioToDataSource
   * @param securityContext
   * @throws ResponseStatusException if scenarioToDataSourceFilter is not valid
   */
  public void validate(
      ScenarioToDataSourceFilter scenarioToDataSourceFilter, SecurityContextBase securityContext) {
    basicService.validate(scenarioToDataSourceFilter, securityContext);

    Set<String> dataSourceIds =
        scenarioToDataSourceFilter.getDataSourceIds() == null
            ? new HashSet<>()
            : scenarioToDataSourceFilter.getDataSourceIds();
    Map<String, DataSource> dataSource =
        dataSourceIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(DataSource.class, dataSourceIds, SecuredBasic_.security, securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    dataSourceIds.removeAll(dataSource.keySet());
    if (!dataSourceIds.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Set with ids " + dataSourceIds);
    }
    scenarioToDataSourceFilter.setDataSource(new ArrayList<>(dataSource.values()));
    Set<String> scenarioIds =
        scenarioToDataSourceFilter.getScenarioIds() == null
            ? new HashSet<>()
            : scenarioToDataSourceFilter.getScenarioIds();
    Map<String, Scenario> scenario =
        scenarioIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(Scenario.class, scenarioIds, SecuredBasic_.security, securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    scenarioIds.removeAll(scenario.keySet());
    if (!scenarioIds.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Set with ids " + scenarioIds);
    }
    scenarioToDataSourceFilter.setScenario(new ArrayList<>(scenario.values()));
  }

  /**
   * @param scenarioToDataSourceCreate Object Used to Create ScenarioToDataSource
   * @param securityContext
   * @throws ResponseStatusException if scenarioToDataSourceCreate is not valid
   */
  public void validate(
      ScenarioToDataSourceCreate scenarioToDataSourceCreate, SecurityContextBase securityContext) {
    basicService.validate(scenarioToDataSourceCreate, securityContext);

    String dataSourceId = scenarioToDataSourceCreate.getDataSourceId();
    DataSource dataSource =
        dataSourceId == null
            ? null
            : this.repository.getByIdOrNull(
                dataSourceId, DataSource.class, SecuredBasic_.security, securityContext);
    if (dataSourceId != null && dataSource == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No DataSource with id " + dataSourceId);
    }
    scenarioToDataSourceCreate.setDataSource(dataSource);

    String scenarioId = scenarioToDataSourceCreate.getScenarioId();
    Scenario scenario =
        scenarioId == null
            ? null
            : this.repository.getByIdOrNull(
                scenarioId, Scenario.class, SecuredBasic_.security, securityContext);
    if (scenarioId != null && scenario == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Scenario with id " + scenarioId);
    }
    scenarioToDataSourceCreate.setScenario(scenario);
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return this.repository.findByIds(c, ids, idAttribute);
  }

  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return this.repository.findByIds(c, requested);
  }

  public <T> T findByIdOrNull(Class<T> type, String id) {
    return this.repository.findByIdOrNull(type, id);
  }

  public void merge(java.lang.Object base) {
    this.repository.merge(base);
  }

  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }
}
