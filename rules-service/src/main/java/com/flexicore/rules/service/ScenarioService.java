package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.flexicore.rules.data.ScenarioRepository;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.request.ScenarioCreate;
import com.flexicore.rules.request.ScenarioFilter;
import com.flexicore.rules.request.ScenarioUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.model.FileResource;
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
public class ScenarioService implements Plugin {

  @Autowired private ScenarioRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param scenarioCreate Object Used to Create Scenario
   * @param securityContext
   * @return created Scenario
   */
  public Scenario createScenario(
      ScenarioCreate scenarioCreate, SecurityContext securityContext) {
    Scenario scenario = createScenarioNoMerge(scenarioCreate, securityContext);
    this.repository.merge(scenario);
    return scenario;
  }

  /**
   * @param scenarioCreate Object Used to Create Scenario
   * @param securityContext
   * @return created Scenario unmerged
   */
  public Scenario createScenarioNoMerge(
      ScenarioCreate scenarioCreate, SecurityContext securityContext) {
    Scenario scenario = new Scenario();
    scenario.setId(UUID.randomUUID().toString());
    updateScenarioNoMerge(scenario, scenarioCreate);

    BaseclassService.createSecurityObjectNoMerge(scenario, securityContext);

    return scenario;
  }

  /**
   * @param scenarioCreate Object Used to Create Scenario
   * @param scenario
   * @return if scenario was updated
   */
  public boolean updateScenarioNoMerge(Scenario scenario, ScenarioCreate scenarioCreate) {
    boolean update = basicService.updateBasicNoMerge(scenarioCreate, scenario);

    if (scenarioCreate.getEvaluatingJSCode() != null
        && (scenario.getEvaluatingJSCode() == null
            || !scenarioCreate
                .getEvaluatingJSCode()
                .getId()
                .equals(scenario.getEvaluatingJSCode().getId()))) {
      scenario.setEvaluatingJSCode(scenarioCreate.getEvaluatingJSCode());
      update = true;
    }

    if (scenarioCreate.getLogFileResource() != null
        && (scenario.getLogFileResource() == null
            || !scenarioCreate
                .getLogFileResource()
                .getId()
                .equals(scenario.getLogFileResource().getId()))) {
      scenario.setLogFileResource(scenarioCreate.getLogFileResource());
      update = true;
    }

    if (scenarioCreate.getScenarioHint() != null
        && (!scenarioCreate.getScenarioHint().equals(scenario.getScenarioHint()))) {
      scenario.setScenarioHint(scenarioCreate.getScenarioHint());
      update = true;
    }

    return update;
  }
  /**
   * @param scenarioUpdate
   * @param securityContext
   * @return scenario
   */
  public Scenario updateScenario(
      ScenarioUpdate scenarioUpdate, SecurityContext securityContext) {
    Scenario scenario = scenarioUpdate.getScenario();
    if (updateScenarioNoMerge(scenario, scenarioUpdate)) {
      this.repository.merge(scenario);
    }
    return scenario;
  }

  /**
   * @param scenarioFilter Object Used to List Scenario
   * @param securityContext
   * @return PaginationResponse containing paging information for Scenario
   */
  public PaginationResponse<Scenario> getAllScenarios(
      ScenarioFilter scenarioFilter, SecurityContext securityContext) {
    List<Scenario> list = listAllScenarios(scenarioFilter, securityContext);
    long count = this.repository.countAllScenarios(scenarioFilter, securityContext);
    return new PaginationResponse<>(list, scenarioFilter, count);
  }

  /**
   * @param scenarioFilter Object Used to List Scenario
   * @param securityContext
   * @return List of Scenario
   */
  public List<Scenario> listAllScenarios(
      ScenarioFilter scenarioFilter, SecurityContext securityContext) {
    return this.repository.listAllScenarios(scenarioFilter, securityContext);
  }

  /**
   * @param scenarioFilter Object Used to List Scenario
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioFilter is not valid
   */
  public void validate(ScenarioFilter scenarioFilter, SecurityContext securityContext) {
    basicService.validate(scenarioFilter, securityContext);

    Set<String> evaluatingJSCodeIds =
        scenarioFilter.getEvaluatingJSCodeIds() == null
            ? new HashSet<>()
            : scenarioFilter.getEvaluatingJSCodeIds();
    Map<String, FileResource> evaluatingJSCode =
        evaluatingJSCodeIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(
                    FileResource.class,
                    evaluatingJSCodeIds,
                    securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    evaluatingJSCodeIds.removeAll(evaluatingJSCode.keySet());
    if (!evaluatingJSCodeIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Set with ids " + evaluatingJSCodeIds);
    }
    scenarioFilter.setEvaluatingJSCode(new ArrayList<>(evaluatingJSCode.values()));
    Set<String> logFileResourceIds =
        scenarioFilter.getLogFileResourceIds() == null
            ? new HashSet<>()
            : scenarioFilter.getLogFileResourceIds();
    Map<String, FileResource> logFileResource =
        logFileResourceIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(
                    FileResource.class, logFileResourceIds,  securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    logFileResourceIds.removeAll(logFileResource.keySet());
    if (!logFileResourceIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Set with ids " + logFileResourceIds);
    }
    scenarioFilter.setLogFileResource(new ArrayList<>(logFileResource.values()));
  }

  /**
   * @param scenarioCreate Object Used to Create Scenario
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioCreate is not valid
   */
  public void validate(ScenarioCreate scenarioCreate, SecurityContext securityContext) {
    basicService.validate(scenarioCreate, securityContext);

    String evaluatingJSCodeId = scenarioCreate.getEvaluatingJSCodeId();
    FileResource evaluatingJSCode =
        evaluatingJSCodeId == null
            ? null
            : this.repository.getByIdOrNull(
                evaluatingJSCodeId, FileResource.class,  securityContext);
    if (evaluatingJSCodeId != null && evaluatingJSCode == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No FileResource with id " + evaluatingJSCodeId);
    }
    scenarioCreate.setEvaluatingJSCode(evaluatingJSCode);

    String logFileResourceId = scenarioCreate.getLogFileResourceId();
    FileResource logFileResource =
        logFileResourceId == null
            ? null
            : this.repository.getByIdOrNull(
                logFileResourceId, FileResource.class,  securityContext);
    if (logFileResourceId != null && logFileResource == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No FileResource with id " + logFileResourceId);
    }
    scenarioCreate.setLogFileResource(logFileResource);
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContext securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContext securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
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
