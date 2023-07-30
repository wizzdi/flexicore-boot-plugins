package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.data.ScenarioToActionRepository;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.model.ScenarioToAction;
import com.flexicore.rules.request.ScenarioToActionCreate;
import com.flexicore.rules.request.ScenarioToActionFilter;
import com.flexicore.rules.request.ScenarioToActionUpdate;
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
public class ScenarioToActionService implements Plugin {

  @Autowired private ScenarioToActionRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param scenarioToActionCreate Object Used to Create ScenarioToAction
   * @param securityContext
   * @return created ScenarioToAction
   */
  public ScenarioToAction createScenarioToAction(
      ScenarioToActionCreate scenarioToActionCreate, SecurityContextBase securityContext) {
    ScenarioToAction scenarioToAction =
        createScenarioToActionNoMerge(scenarioToActionCreate, securityContext);
    this.repository.merge(scenarioToAction);
    return scenarioToAction;
  }

  /**
   * @param scenarioToActionCreate Object Used to Create ScenarioToAction
   * @param securityContext
   * @return created ScenarioToAction unmerged
   */
  public ScenarioToAction createScenarioToActionNoMerge(
      ScenarioToActionCreate scenarioToActionCreate, SecurityContextBase securityContext) {
    ScenarioToAction scenarioToAction = new ScenarioToAction();
    scenarioToAction.setId(UUID.randomUUID().toString());
    updateScenarioToActionNoMerge(scenarioToAction, scenarioToActionCreate);

    BaseclassService.createSecurityObjectNoMerge(scenarioToAction, securityContext);

    return scenarioToAction;
  }

  /**
   * @param scenarioToActionCreate Object Used to Create ScenarioToAction
   * @param scenarioToAction
   * @return if scenarioToAction was updated
   */
  public boolean updateScenarioToActionNoMerge(
      ScenarioToAction scenarioToAction, ScenarioToActionCreate scenarioToActionCreate) {
    boolean update = basicService.updateBasicNoMerge(scenarioToActionCreate, scenarioToAction);

    if (scenarioToActionCreate.getEnabled() != null
        && (!scenarioToActionCreate.getEnabled().equals(scenarioToAction.isEnabled()))) {
      scenarioToAction.setEnabled(scenarioToActionCreate.getEnabled());
      update = true;
    }

    if (scenarioToActionCreate.getScenarioAction() != null
        && (scenarioToAction.getScenarioAction() == null
            || !scenarioToActionCreate
                .getScenarioAction()
                .getId()
                .equals(scenarioToAction.getScenarioAction().getId()))) {
      scenarioToAction.setScenarioAction(scenarioToActionCreate.getScenarioAction());
      update = true;
    }

    if (scenarioToActionCreate.getScenario() != null
        && (scenarioToAction.getScenario() == null
            || !scenarioToActionCreate
                .getScenario()
                .getId()
                .equals(scenarioToAction.getScenario().getId()))) {
      scenarioToAction.setScenario(scenarioToActionCreate.getScenario());
      update = true;
    }

    return update;
  }
  /**
   * @param scenarioToActionUpdate
   * @param securityContext
   * @return scenarioToAction
   */
  public ScenarioToAction updateScenarioToAction(
      ScenarioToActionUpdate scenarioToActionUpdate, SecurityContextBase securityContext) {
    ScenarioToAction scenarioToAction = scenarioToActionUpdate.getScenarioToAction();
    if (updateScenarioToActionNoMerge(scenarioToAction, scenarioToActionUpdate)) {
      this.repository.merge(scenarioToAction);
    }
    return scenarioToAction;
  }

  /**
   * @param scenarioToActionFilter Object Used to List ScenarioToAction
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioToAction
   */
  public PaginationResponse<ScenarioToAction> getAllScenarioToActions(
      ScenarioToActionFilter scenarioToActionFilter, SecurityContextBase securityContext) {
    List<ScenarioToAction> list = listAllScenarioToActions(scenarioToActionFilter, securityContext);
    long count = this.repository.countAllScenarioToActions(scenarioToActionFilter, securityContext);
    return new PaginationResponse<>(list, scenarioToActionFilter, count);
  }

  /**
   * @param scenarioToActionFilter Object Used to List ScenarioToAction
   * @param securityContext
   * @return List of ScenarioToAction
   */
  public List<ScenarioToAction> listAllScenarioToActions(
      ScenarioToActionFilter scenarioToActionFilter, SecurityContextBase securityContext) {
    return this.repository.listAllScenarioToActions(scenarioToActionFilter, securityContext);
  }

  /**
   * @param scenarioToActionFilter Object Used to List ScenarioToAction
   * @param securityContext
   * @throws ResponseStatusException if scenarioToActionFilter is not valid
   */
  public void validate(
      ScenarioToActionFilter scenarioToActionFilter, SecurityContextBase securityContext) {
    basicService.validate(scenarioToActionFilter, securityContext);

    Set<String> scenarioActionIds =
        scenarioToActionFilter.getScenarioActionIds() == null
            ? new HashSet<>()
            : scenarioToActionFilter.getScenarioActionIds();
    Map<String, ScenarioAction> scenarioAction =
        scenarioActionIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(
                    ScenarioAction.class,
                    scenarioActionIds,
                    SecuredBasic_.security,
                    securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    scenarioActionIds.removeAll(scenarioAction.keySet());
    if (!scenarioActionIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Set with ids " + scenarioActionIds);
    }
    scenarioToActionFilter.setScenarioAction(new ArrayList<>(scenarioAction.values()));
    Set<String> scenarioIds =
        scenarioToActionFilter.getScenarioIds() == null
            ? new HashSet<>()
            : scenarioToActionFilter.getScenarioIds();
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
    scenarioToActionFilter.setScenario(new ArrayList<>(scenario.values()));
  }

  /**
   * @param scenarioToActionCreate Object Used to Create ScenarioToAction
   * @param securityContext
   * @throws ResponseStatusException if scenarioToActionCreate is not valid
   */
  public void validate(
      ScenarioToActionCreate scenarioToActionCreate, SecurityContextBase securityContext) {
    basicService.validate(scenarioToActionCreate, securityContext);

    String scenarioActionId = scenarioToActionCreate.getScenarioActionId();
    ScenarioAction scenarioAction =
        scenarioActionId == null
            ? null
            : this.repository.getByIdOrNull(
                scenarioActionId, ScenarioAction.class, SecuredBasic_.security, securityContext);
    if (scenarioActionId != null && scenarioAction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioAction with id " + scenarioActionId);
    }
    scenarioToActionCreate.setScenarioAction(scenarioAction);

    String scenarioId = scenarioToActionCreate.getScenarioId();
    Scenario scenario =
        scenarioId == null
            ? null
            : this.repository.getByIdOrNull(
                scenarioId, Scenario.class, SecuredBasic_.security, securityContext);
    if (scenarioId != null && scenario == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Scenario with id " + scenarioId);
    }
    scenarioToActionCreate.setScenario(scenario);
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
