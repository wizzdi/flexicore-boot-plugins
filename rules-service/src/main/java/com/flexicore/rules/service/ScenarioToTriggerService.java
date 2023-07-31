package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.data.ScenarioToTriggerRepository;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioToTrigger;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.request.ScenarioToTriggerCreate;
import com.flexicore.rules.request.ScenarioToTriggerFilter;
import com.flexicore.rules.request.ScenarioToTriggerUpdate;
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
public class ScenarioToTriggerService implements Plugin {

  @Autowired private ScenarioToTriggerRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param scenarioToTriggerCreate Object Used to Create ScenarioToTrigger
   * @param securityContext
   * @return created ScenarioToTrigger
   */
  public ScenarioToTrigger createScenarioToTrigger(
      ScenarioToTriggerCreate scenarioToTriggerCreate, SecurityContextBase securityContext) {
    ScenarioToTrigger scenarioToTrigger =
        createScenarioToTriggerNoMerge(scenarioToTriggerCreate, securityContext);
    this.repository.merge(scenarioToTrigger);
    return scenarioToTrigger;
  }

  /**
   * @param scenarioToTriggerCreate Object Used to Create ScenarioToTrigger
   * @param securityContext
   * @return created ScenarioToTrigger unmerged
   */
  public ScenarioToTrigger createScenarioToTriggerNoMerge(
      ScenarioToTriggerCreate scenarioToTriggerCreate, SecurityContextBase securityContext) {
    ScenarioToTrigger scenarioToTrigger = new ScenarioToTrigger();
    scenarioToTrigger.setId(UUID.randomUUID().toString());
    updateScenarioToTriggerNoMerge(scenarioToTrigger, scenarioToTriggerCreate);

    BaseclassService.createSecurityObjectNoMerge(scenarioToTrigger, securityContext);

    return scenarioToTrigger;
  }

  /**
   * @param scenarioToTriggerCreate Object Used to Create ScenarioToTrigger
   * @param scenarioToTrigger
   * @return if scenarioToTrigger was updated
   */
  public boolean updateScenarioToTriggerNoMerge(
      ScenarioToTrigger scenarioToTrigger, ScenarioToTriggerCreate scenarioToTriggerCreate) {
    boolean update = basicService.updateBasicNoMerge(scenarioToTriggerCreate, scenarioToTrigger);

    if (scenarioToTriggerCreate.getFiring() != null
        && (!scenarioToTriggerCreate.getFiring().equals(scenarioToTrigger.isFiring()))) {
      scenarioToTrigger.setFiring(scenarioToTriggerCreate.getFiring());
      update = true;
    }

    if (scenarioToTriggerCreate.getScenarioTrigger() != null
        && (scenarioToTrigger.getScenarioTrigger() == null
            || !scenarioToTriggerCreate
                .getScenarioTrigger()
                .getId()
                .equals(scenarioToTrigger.getScenarioTrigger().getId()))) {
      scenarioToTrigger.setScenarioTrigger(scenarioToTriggerCreate.getScenarioTrigger());
      update = true;
    }

    if (scenarioToTriggerCreate.getScenario() != null
        && (scenarioToTrigger.getScenario() == null
            || !scenarioToTriggerCreate
                .getScenario()
                .getId()
                .equals(scenarioToTrigger.getScenario().getId()))) {
      scenarioToTrigger.setScenario(scenarioToTriggerCreate.getScenario());
      update = true;
    }

    if (scenarioToTriggerCreate.getOrdinal() != null
        && (!scenarioToTriggerCreate.getOrdinal().equals(scenarioToTrigger.getOrdinal()))) {
      scenarioToTrigger.setOrdinal(scenarioToTriggerCreate.getOrdinal());
      update = true;
    }

    if (scenarioToTriggerCreate.getEnabled() != null
        && (!scenarioToTriggerCreate.getEnabled().equals(scenarioToTrigger.isEnabled()))) {
      scenarioToTrigger.setEnabled(scenarioToTriggerCreate.getEnabled());
      update = true;
    }

    return update;
  }
  /**
   * @param scenarioToTriggerUpdate
   * @param securityContext
   * @return scenarioToTrigger
   */
  public ScenarioToTrigger updateScenarioToTrigger(
      ScenarioToTriggerUpdate scenarioToTriggerUpdate, SecurityContextBase securityContext) {
    ScenarioToTrigger scenarioToTrigger = scenarioToTriggerUpdate.getScenarioToTrigger();
    if (updateScenarioToTriggerNoMerge(scenarioToTrigger, scenarioToTriggerUpdate)) {
      this.repository.merge(scenarioToTrigger);
    }
    return scenarioToTrigger;
  }

  /**
   * @param scenarioToTriggerFilter Object Used to List ScenarioToTrigger
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioToTrigger
   */
  public PaginationResponse<ScenarioToTrigger> getAllScenarioToTriggers(
      ScenarioToTriggerFilter scenarioToTriggerFilter, SecurityContextBase securityContext) {
    List<ScenarioToTrigger> list =
        listAllScenarioToTriggers(scenarioToTriggerFilter, securityContext);
    long count =
        this.repository.countAllScenarioToTriggers(scenarioToTriggerFilter, securityContext);
    return new PaginationResponse<>(list, scenarioToTriggerFilter, count);
  }

  /**
   * @param scenarioToTriggerFilter Object Used to List ScenarioToTrigger
   * @param securityContext
   * @return List of ScenarioToTrigger
   */
  public List<ScenarioToTrigger> listAllScenarioToTriggers(
      ScenarioToTriggerFilter scenarioToTriggerFilter, SecurityContextBase securityContext) {
    return this.repository.listAllScenarioToTriggers(scenarioToTriggerFilter, securityContext);
  }

  /**
   * @param scenarioToTriggerFilter Object Used to List ScenarioToTrigger
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioToTriggerFilter is not valid
   */
  public void validate(
      ScenarioToTriggerFilter scenarioToTriggerFilter, SecurityContextBase securityContext) {
    basicService.validate(scenarioToTriggerFilter, securityContext);

    Set<String> scenarioTriggerIds =
        scenarioToTriggerFilter.getScenarioTriggerIds() == null
            ? new HashSet<>()
            : scenarioToTriggerFilter.getScenarioTriggerIds();
    Map<String, ScenarioTrigger> scenarioTrigger =
        scenarioTriggerIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(
                    ScenarioTrigger.class,
                    scenarioTriggerIds,
                    SecuredBasic_.security,
                    securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    scenarioTriggerIds.removeAll(scenarioTrigger.keySet());
    if (!scenarioTriggerIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Set with ids " + scenarioTriggerIds);
    }
    scenarioToTriggerFilter.setScenarioTrigger(new ArrayList<>(scenarioTrigger.values()));
    Set<String> scenarioIds =
        scenarioToTriggerFilter.getScenarioIds() == null
            ? new HashSet<>()
            : scenarioToTriggerFilter.getScenarioIds();
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
    scenarioToTriggerFilter.setScenario(new ArrayList<>(scenario.values()));
  }

  /**
   * @param scenarioToTriggerCreate Object Used to Create ScenarioToTrigger
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioToTriggerCreate is not valid
   */
  public void validate(
      ScenarioToTriggerCreate scenarioToTriggerCreate, SecurityContextBase securityContext) {
    basicService.validate(scenarioToTriggerCreate, securityContext);

    String scenarioTriggerId = scenarioToTriggerCreate.getScenarioTriggerId();
    ScenarioTrigger scenarioTrigger =
        scenarioTriggerId == null
            ? null
            : this.repository.getByIdOrNull(
                scenarioTriggerId, ScenarioTrigger.class, SecuredBasic_.security, securityContext);
    if (scenarioTriggerId != null && scenarioTrigger == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioTrigger with id " + scenarioTriggerId);
    }
    scenarioToTriggerCreate.setScenarioTrigger(scenarioTrigger);

    String scenarioId = scenarioToTriggerCreate.getScenarioId();
    Scenario scenario =
        scenarioId == null
            ? null
            : this.repository.getByIdOrNull(
                scenarioId, Scenario.class, SecuredBasic_.security, securityContext);
    if (scenarioId != null && scenario == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Scenario with id " + scenarioId);
    }
    scenarioToTriggerCreate.setScenario(scenario);
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
