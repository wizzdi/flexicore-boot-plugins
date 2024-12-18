package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.flexicore.rules.data.ScenarioSavableEventRepository;
import com.flexicore.rules.model.ScenarioSavableEvent;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.request.ScenarioSavableEventCreate;
import com.flexicore.rules.request.ScenarioSavableEventFilter;
import com.flexicore.rules.request.ScenarioSavableEventUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
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
public class ScenarioSavableEventService implements Plugin {

  @Autowired private ScenarioSavableEventRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param scenarioSavableEventCreate Object Used to Create ScenarioSavableEvent
   * @param securityContext
   * @return created ScenarioSavableEvent
   */
  public ScenarioSavableEvent createScenarioSavableEvent(
      ScenarioSavableEventCreate scenarioSavableEventCreate, SecurityContext securityContext) {
    ScenarioSavableEvent scenarioSavableEvent =
        createScenarioSavableEventNoMerge(scenarioSavableEventCreate, securityContext);
    this.repository.merge(scenarioSavableEvent);
    return scenarioSavableEvent;
  }

  /**
   * @param scenarioSavableEventCreate Object Used to Create ScenarioSavableEvent
   * @param securityContext
   * @return created ScenarioSavableEvent unmerged
   */
  public ScenarioSavableEvent createScenarioSavableEventNoMerge(
      ScenarioSavableEventCreate scenarioSavableEventCreate, SecurityContext securityContext) {
    ScenarioSavableEvent scenarioSavableEvent = new ScenarioSavableEvent();
    scenarioSavableEvent.setId(UUID.randomUUID().toString());
    updateScenarioSavableEventNoMerge(scenarioSavableEvent, scenarioSavableEventCreate);

    BaseclassService.createSecurityObjectNoMerge(scenarioSavableEvent, securityContext);

    return scenarioSavableEvent;
  }

  /**
   * @param scenarioSavableEventCreate Object Used to Create ScenarioSavableEvent
   * @param scenarioSavableEvent
   * @return if scenarioSavableEvent was updated
   */
  public boolean updateScenarioSavableEventNoMerge(
      ScenarioSavableEvent scenarioSavableEvent,
      ScenarioSavableEventCreate scenarioSavableEventCreate) {
    boolean update =
        basicService.updateBasicNoMerge(scenarioSavableEventCreate, scenarioSavableEvent);

    if (scenarioSavableEventCreate.getScenarioTrigger() != null
        && (scenarioSavableEvent.getScenarioTrigger() == null
            || !scenarioSavableEventCreate
                .getScenarioTrigger()
                .getId()
                .equals(scenarioSavableEvent.getScenarioTrigger().getId()))) {
      scenarioSavableEvent.setScenarioTrigger(scenarioSavableEventCreate.getScenarioTrigger());
      update = true;
    }

    return update;
  }
  /**
   * @param scenarioSavableEventUpdate
   * @param securityContext
   * @return scenarioSavableEvent
   */
  public ScenarioSavableEvent updateScenarioSavableEvent(
      ScenarioSavableEventUpdate scenarioSavableEventUpdate, SecurityContext securityContext) {
    ScenarioSavableEvent scenarioSavableEvent =
        scenarioSavableEventUpdate.getScenarioSavableEvent();
    if (updateScenarioSavableEventNoMerge(scenarioSavableEvent, scenarioSavableEventUpdate)) {
      this.repository.merge(scenarioSavableEvent);
    }
    return scenarioSavableEvent;
  }

  /**
   * @param scenarioSavableEventFilter Object Used to List ScenarioSavableEvent
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioSavableEvent
   */
  public PaginationResponse<ScenarioSavableEvent> getAllScenarioSavableEvents(
      ScenarioSavableEventFilter scenarioSavableEventFilter, SecurityContext securityContext) {
    List<ScenarioSavableEvent> list =
        listAllScenarioSavableEvents(scenarioSavableEventFilter, securityContext);
    long count =
        this.repository.countAllScenarioSavableEvents(scenarioSavableEventFilter, securityContext);
    return new PaginationResponse<>(list, scenarioSavableEventFilter, count);
  }

  /**
   * @param scenarioSavableEventFilter Object Used to List ScenarioSavableEvent
   * @param securityContext
   * @return List of ScenarioSavableEvent
   */
  public List<ScenarioSavableEvent> listAllScenarioSavableEvents(
      ScenarioSavableEventFilter scenarioSavableEventFilter, SecurityContext securityContext) {
    return this.repository.listAllScenarioSavableEvents(
        scenarioSavableEventFilter, securityContext);
  }

  /**
   * @param scenarioSavableEventFilter Object Used to List ScenarioSavableEvent
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioSavableEventFilter is not valid
   */
  public void validate(
      ScenarioSavableEventFilter scenarioSavableEventFilter, SecurityContext securityContext) {
    basicService.validate(scenarioSavableEventFilter, securityContext);

    Set<String> scenarioTriggerIds =
        scenarioSavableEventFilter.getScenarioTriggerIds() == null
            ? new HashSet<>()
            : scenarioSavableEventFilter.getScenarioTriggerIds();
    Map<String, ScenarioTrigger> scenarioTrigger =
        scenarioTriggerIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(
                    ScenarioTrigger.class,
                    scenarioTriggerIds,
                    securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    scenarioTriggerIds.removeAll(scenarioTrigger.keySet());
    if (!scenarioTriggerIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Set with ids " + scenarioTriggerIds);
    }
    scenarioSavableEventFilter.setScenarioTrigger(new ArrayList<>(scenarioTrigger.values()));
  }

  /**
   * @param scenarioSavableEventCreate Object Used to Create ScenarioSavableEvent
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioSavableEventCreate is not valid
   */
  public void validate(
      ScenarioSavableEventCreate scenarioSavableEventCreate, SecurityContext securityContext) {
    basicService.validate(scenarioSavableEventCreate, securityContext);

    String scenarioTriggerId = scenarioSavableEventCreate.getScenarioTriggerId();
    ScenarioTrigger scenarioTrigger =
        scenarioTriggerId == null
            ? null
            : this.repository.getByIdOrNull(
                scenarioTriggerId, ScenarioTrigger.class,  securityContext);
    if (scenarioTriggerId != null && scenarioTrigger == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioTrigger with id " + scenarioTriggerId);
    }
    scenarioSavableEventCreate.setScenarioTrigger(scenarioTrigger);
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
