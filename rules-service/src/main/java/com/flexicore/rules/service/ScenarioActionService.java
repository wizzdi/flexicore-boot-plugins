package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.data.ScenarioActionRepository;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.request.ScenarioActionCreate;
import com.flexicore.rules.request.ScenarioActionFilter;
import com.flexicore.rules.request.ScenarioActionUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
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
public class ScenarioActionService implements Plugin {

  @Autowired private ScenarioActionRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param scenarioActionCreate Object Used to Create ScenarioAction
   * @param securityContext
   * @return created ScenarioAction
   */
  public ScenarioAction createScenarioAction(
      ScenarioActionCreate scenarioActionCreate, SecurityContextBase securityContext) {
    ScenarioAction scenarioAction =
        createScenarioActionNoMerge(scenarioActionCreate, securityContext);
    this.repository.merge(scenarioAction);
    return scenarioAction;
  }

  /**
   * @param scenarioActionCreate Object Used to Create ScenarioAction
   * @param securityContext
   * @return created ScenarioAction unmerged
   */
  public ScenarioAction createScenarioActionNoMerge(
      ScenarioActionCreate scenarioActionCreate, SecurityContextBase securityContext) {
    ScenarioAction scenarioAction = new ScenarioAction();
    scenarioAction.setId(UUID.randomUUID().toString());
    updateScenarioActionNoMerge(scenarioAction, scenarioActionCreate);

    BaseclassService.createSecurityObjectNoMerge(scenarioAction, securityContext);

    return scenarioAction;
  }

  /**
   * @param scenarioActionCreate Object Used to Create ScenarioAction
   * @param scenarioAction
   * @return if scenarioAction was updated
   */
  public boolean updateScenarioActionNoMerge(
      ScenarioAction scenarioAction, ScenarioActionCreate scenarioActionCreate) {
    boolean update = basicService.updateBasicNoMerge(scenarioActionCreate, scenarioAction);

    if (scenarioActionCreate.getDynamicExecution() != null
        && (scenarioAction.getDynamicExecution() == null
            || !scenarioActionCreate
                .getDynamicExecution()
                .getId()
                .equals(scenarioAction.getDynamicExecution().getId()))) {
      scenarioAction.setDynamicExecution(scenarioActionCreate.getDynamicExecution());
      update = true;
    }

    return update;
  }
  /**
   * @param scenarioActionUpdate
   * @param securityContext
   * @return scenarioAction
   */
  public ScenarioAction updateScenarioAction(
      ScenarioActionUpdate scenarioActionUpdate, SecurityContextBase securityContext) {
    ScenarioAction scenarioAction = scenarioActionUpdate.getScenarioAction();
    if (updateScenarioActionNoMerge(scenarioAction, scenarioActionUpdate)) {
      this.repository.merge(scenarioAction);
    }
    return scenarioAction;
  }

  /**
   * @param scenarioActionFilter Object Used to List ScenarioAction
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioAction
   */
  public PaginationResponse<ScenarioAction> getAllScenarioActions(
      ScenarioActionFilter scenarioActionFilter, SecurityContextBase securityContext) {
    List<ScenarioAction> list = listAllScenarioActions(scenarioActionFilter, securityContext);
    long count = this.repository.countAllScenarioActions(scenarioActionFilter, securityContext);
    return new PaginationResponse<>(list, scenarioActionFilter, count);
  }

  /**
   * @param scenarioActionFilter Object Used to List ScenarioAction
   * @param securityContext
   * @return List of ScenarioAction
   */
  public List<ScenarioAction> listAllScenarioActions(
      ScenarioActionFilter scenarioActionFilter, SecurityContextBase securityContext) {
    return this.repository.listAllScenarioActions(scenarioActionFilter, securityContext);
  }

  /**
   * @param scenarioActionFilter Object Used to List ScenarioAction
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioActionFilter is not valid
   */
  public void validate(
      ScenarioActionFilter scenarioActionFilter, SecurityContextBase securityContext) {
    basicService.validate(scenarioActionFilter, securityContext);

    Set<String> dynamicExecutionIds =
        scenarioActionFilter.getDynamicExecutionIds() == null
            ? new HashSet<>()
            : scenarioActionFilter.getDynamicExecutionIds();
    Map<String, DynamicExecution> dynamicExecution =
        dynamicExecutionIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(
                    DynamicExecution.class,
                    dynamicExecutionIds,
                    SecuredBasic_.security,
                    securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    dynamicExecutionIds.removeAll(dynamicExecution.keySet());
    if (!dynamicExecutionIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Set with ids " + dynamicExecutionIds);
    }
    scenarioActionFilter.setDynamicExecution(new ArrayList<>(dynamicExecution.values()));
  }

  /**
   * @param scenarioActionCreate Object Used to Create ScenarioAction
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioActionCreate is not valid
   */
  public void validate(
      ScenarioActionCreate scenarioActionCreate, SecurityContextBase securityContext) {
    basicService.validate(scenarioActionCreate, securityContext);

    String dynamicExecutionId = scenarioActionCreate.getDynamicExecutionId();
    DynamicExecution dynamicExecution =
        dynamicExecutionId == null
            ? null
            : this.repository.getByIdOrNull(
                dynamicExecutionId,
                DynamicExecution.class,
                SecuredBasic_.security,
                securityContext);
    if (dynamicExecutionId != null && dynamicExecution == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No DynamicExecution with id " + dynamicExecutionId);
    }
    scenarioActionCreate.setDynamicExecution(dynamicExecution);
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
