package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.flexicore.rules.data.ScenarioTriggerRepository;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.request.ScenarioTriggerCreate;
import com.flexicore.rules.request.ScenarioTriggerFilter;
import com.flexicore.rules.request.ScenarioTriggerUpdate;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class ScenarioTriggerService implements Plugin {

  @Autowired private ScenarioTriggerRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param scenarioTriggerCreate Object Used to Create ScenarioTrigger
   * @param securityContext
   * @return created ScenarioTrigger
   */
  public ScenarioTrigger createScenarioTrigger(
      ScenarioTriggerCreate scenarioTriggerCreate, SecurityContext securityContext) {
    ScenarioTrigger scenarioTrigger =
        createScenarioTriggerNoMerge(scenarioTriggerCreate, securityContext);
    this.repository.merge(scenarioTrigger);
    return scenarioTrigger;
  }

  /**
   * @param scenarioTriggerCreate Object Used to Create ScenarioTrigger
   * @param securityContext
   * @return created ScenarioTrigger unmerged
   */
  public ScenarioTrigger createScenarioTriggerNoMerge(
      ScenarioTriggerCreate scenarioTriggerCreate, SecurityContext securityContext) {
    ScenarioTrigger scenarioTrigger = new ScenarioTrigger();
    scenarioTrigger.setId(UUID.randomUUID().toString());
    updateScenarioTriggerNoMerge(scenarioTrigger, scenarioTriggerCreate);

    BaseclassService.createSecurityObjectNoMerge(scenarioTrigger, securityContext);

    return scenarioTrigger;
  }

  /**
   * @param scenarioTriggerCreate Object Used to Create ScenarioTrigger
   * @param scenarioTrigger
   * @return if scenarioTrigger was updated
   */
  public boolean updateScenarioTriggerNoMerge(
      ScenarioTrigger scenarioTrigger, ScenarioTriggerCreate scenarioTriggerCreate) {
    boolean update = basicService.updateBasicNoMerge(scenarioTriggerCreate, scenarioTrigger);

    if (scenarioTriggerCreate.getLastEventId() != null
        && (!scenarioTriggerCreate.getLastEventId().equals(scenarioTrigger.getLastEventId()))) {
      scenarioTrigger.setLastEventId(scenarioTriggerCreate.getLastEventId());
      update = true;
    }

    if (scenarioTriggerCreate.getLastActivated() != null
        && (!scenarioTriggerCreate.getLastActivated().equals(scenarioTrigger.getLastActivated()))) {
      scenarioTrigger.setLastActivated(scenarioTriggerCreate.getLastActivated());
      update = true;
    }

    if (scenarioTriggerCreate.getValidFrom() != null
        && (!scenarioTriggerCreate.getValidFrom().equals(scenarioTrigger.getValidFrom()))) {
      scenarioTrigger.setValidFrom(scenarioTriggerCreate.getValidFrom());
      update = true;
    }

    if (scenarioTriggerCreate.getCooldownIntervalMs() != null
        && (!scenarioTriggerCreate
            .getCooldownIntervalMs()
            .equals(scenarioTrigger.getCooldownIntervalMs()))) {
      scenarioTrigger.setCooldownIntervalMs(scenarioTriggerCreate.getCooldownIntervalMs());
      update = true;
    }

    if (scenarioTriggerCreate.getActiveTill() != null
        && (!scenarioTriggerCreate.getActiveTill().equals(scenarioTrigger.getActiveTill()))) {
      scenarioTrigger.setActiveTill(scenarioTriggerCreate.getActiveTill());
      update = true;
    }

    if (scenarioTriggerCreate.getActiveMs() != null
        && (!scenarioTriggerCreate.getActiveMs().equals(scenarioTrigger.getActiveMs()))) {
      scenarioTrigger.setActiveMs(scenarioTriggerCreate.getActiveMs());
      update = true;
    }

    if (scenarioTriggerCreate.getLogFileResource() != null
        && (scenarioTrigger.getLogFileResource() == null
            || !scenarioTriggerCreate
                .getLogFileResource()
                .getId()
                .equals(scenarioTrigger.getLogFileResource().getId()))) {
      scenarioTrigger.setLogFileResource(scenarioTriggerCreate.getLogFileResource());
      update = true;
    }

    if (scenarioTriggerCreate.getEvaluatingJSCode() != null
        && (scenarioTrigger.getEvaluatingJSCode() == null
            || !scenarioTriggerCreate
                .getEvaluatingJSCode()
                .getId()
                .equals(scenarioTrigger.getEvaluatingJSCode().getId()))) {
      scenarioTrigger.setEvaluatingJSCode(scenarioTriggerCreate.getEvaluatingJSCode());
      update = true;
    }

    if (scenarioTriggerCreate.getScenarioTriggerType() != null
        && (scenarioTrigger.getScenarioTriggerType() == null
            || !scenarioTriggerCreate
                .getScenarioTriggerType()
                .getId()
                .equals(scenarioTrigger.getScenarioTriggerType().getId()))) {
      scenarioTrigger.setScenarioTriggerType(scenarioTriggerCreate.getScenarioTriggerType());
      update = true;
    }

    if (scenarioTriggerCreate.getValidTill() != null
        && (!scenarioTriggerCreate.getValidTill().equals(scenarioTrigger.getValidTill()))) {
      scenarioTrigger.setValidTill(scenarioTriggerCreate.getValidTill());
      update = true;
    }
    if(scenarioTriggerCreate.getTimeZoneId()!=null && !scenarioTriggerCreate.getTimeZoneId().equals(scenarioTrigger.getTimeZoneId())){
      scenarioTrigger.setTimeZoneId(scenarioTriggerCreate.getTimeZoneId());
      update=true;
    }

    return update;
  }
  /**
   * @param scenarioTriggerUpdate
   * @param securityContext
   * @return scenarioTrigger
   */
  public ScenarioTrigger updateScenarioTrigger(
      ScenarioTriggerUpdate scenarioTriggerUpdate, SecurityContext securityContext) {
    ScenarioTrigger scenarioTrigger = scenarioTriggerUpdate.getScenarioTrigger();
    if (updateScenarioTriggerNoMerge(scenarioTrigger, scenarioTriggerUpdate)) {
      this.repository.merge(scenarioTrigger);
    }
    return scenarioTrigger;
  }

  /**
   * @param scenarioTriggerFilter Object Used to List ScenarioTrigger
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioTrigger
   */
  public PaginationResponse<ScenarioTrigger> getAllScenarioTriggers(
      ScenarioTriggerFilter scenarioTriggerFilter, SecurityContext securityContext) {
    List<ScenarioTrigger> list = listAllScenarioTriggers(scenarioTriggerFilter, securityContext);
    long count = this.repository.countAllScenarioTriggers(scenarioTriggerFilter, securityContext);
    return new PaginationResponse<>(list, scenarioTriggerFilter, count);
  }

  /**
   * @param scenarioTriggerFilter Object Used to List ScenarioTrigger
   * @param securityContext
   * @return List of ScenarioTrigger
   */
  public List<ScenarioTrigger> listAllScenarioTriggers(
      ScenarioTriggerFilter scenarioTriggerFilter, SecurityContext securityContext) {
    return this.repository.listAllScenarioTriggers(scenarioTriggerFilter, securityContext);
  }

  /**
   * @param scenarioTriggerFilter Object Used to List ScenarioTrigger
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioTriggerFilter is not valid
   */
  public void validate(
      ScenarioTriggerFilter scenarioTriggerFilter, SecurityContext securityContext) {
    basicService.validate(scenarioTriggerFilter, securityContext);

    Set<String> logFileResourceIds =
        scenarioTriggerFilter.getLogFileResourceIds() == null
            ? new HashSet<>()
            : scenarioTriggerFilter.getLogFileResourceIds();
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
    scenarioTriggerFilter.setLogFileResource(new ArrayList<>(logFileResource.values()));
    Set<String> evaluatingJSCodeIds =
        scenarioTriggerFilter.getEvaluatingJSCodeIds() == null
            ? new HashSet<>()
            : scenarioTriggerFilter.getEvaluatingJSCodeIds();
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
    scenarioTriggerFilter.setEvaluatingJSCode(new ArrayList<>(evaluatingJSCode.values()));
    Set<String> scenarioTriggerTypeIds =
        scenarioTriggerFilter.getScenarioTriggerTypeIds() == null
            ? new HashSet<>()
            : scenarioTriggerFilter.getScenarioTriggerTypeIds();
    Map<String, ScenarioTriggerType> scenarioTriggerType =
        scenarioTriggerTypeIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(
                    ScenarioTriggerType.class,
                    scenarioTriggerTypeIds,
                    securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    scenarioTriggerTypeIds.removeAll(scenarioTriggerType.keySet());
    if (!scenarioTriggerTypeIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Set with ids " + scenarioTriggerTypeIds);
    }
    scenarioTriggerFilter.setScenarioTriggerType(new ArrayList<>(scenarioTriggerType.values()));
  }

  /**
   * @param scenarioTriggerCreate Object Used to Create ScenarioTrigger
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioTriggerCreate is not valid
   */
  public void validate(
      ScenarioTriggerCreate scenarioTriggerCreate, SecurityContext securityContext) {
    basicService.validate(scenarioTriggerCreate, securityContext);

    String evaluatingJSCodeId = scenarioTriggerCreate.getEvaluatingJSCodeId();
    FileResource evaluatingJSCode =
        evaluatingJSCodeId == null
            ? null
            : this.repository.getByIdOrNull(
                evaluatingJSCodeId, FileResource.class,  securityContext);
    if (evaluatingJSCodeId != null && evaluatingJSCode == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No FileResource with id " + evaluatingJSCodeId);
    }
    scenarioTriggerCreate.setEvaluatingJSCode(evaluatingJSCode);

    String scenarioTriggerTypeId = scenarioTriggerCreate.getScenarioTriggerTypeId();
    ScenarioTriggerType scenarioTriggerType =
        scenarioTriggerTypeId == null
            ? null
            : this.repository.getByIdOrNull(
                scenarioTriggerTypeId,
                ScenarioTriggerType.class,
                securityContext);
    if (scenarioTriggerTypeId != null && scenarioTriggerType == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioTriggerType with id " + scenarioTriggerTypeId);
    }
    scenarioTriggerCreate.setScenarioTriggerType(scenarioTriggerType);
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

  @Transactional
  public void massMerge(List<?> toMerge, boolean updatedate, boolean propagateEvents) {
    repository.massMerge(toMerge, updatedate, propagateEvents);
  }
}
