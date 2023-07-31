package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.data.ScenarioTriggerTypeRepository;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.request.ScenarioTriggerTypeCreate;
import com.flexicore.rules.request.ScenarioTriggerTypeFilter;
import com.flexicore.rules.request.ScenarioTriggerTypeUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class ScenarioTriggerTypeService implements Plugin {

  @Autowired private ScenarioTriggerTypeRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param scenarioTriggerTypeCreate Object Used to Create ScenarioTriggerType
   * @param securityContext
   * @return created ScenarioTriggerType
   */
  public ScenarioTriggerType createScenarioTriggerType(
      ScenarioTriggerTypeCreate scenarioTriggerTypeCreate, SecurityContextBase securityContext) {
    ScenarioTriggerType scenarioTriggerType =
        createScenarioTriggerTypeNoMerge(scenarioTriggerTypeCreate, securityContext);
    this.repository.merge(scenarioTriggerType);
    return scenarioTriggerType;
  }

  /**
   * @param scenarioTriggerTypeCreate Object Used to Create ScenarioTriggerType
   * @param securityContext
   * @return created ScenarioTriggerType unmerged
   */
  public ScenarioTriggerType createScenarioTriggerTypeNoMerge(
      ScenarioTriggerTypeCreate scenarioTriggerTypeCreate, SecurityContextBase securityContext) {
    ScenarioTriggerType scenarioTriggerType = new ScenarioTriggerType();
    scenarioTriggerType.setId(UUID.randomUUID().toString());
    updateScenarioTriggerTypeNoMerge(scenarioTriggerType, scenarioTriggerTypeCreate);

    BaseclassService.createSecurityObjectNoMerge(scenarioTriggerType, securityContext);

    return scenarioTriggerType;
  }

  /**
   * @param scenarioTriggerTypeCreate Object Used to Create ScenarioTriggerType
   * @param scenarioTriggerType
   * @return if scenarioTriggerType was updated
   */
  public boolean updateScenarioTriggerTypeNoMerge(
      ScenarioTriggerType scenarioTriggerType,
      ScenarioTriggerTypeCreate scenarioTriggerTypeCreate) {
    boolean update =
        basicService.updateBasicNoMerge(scenarioTriggerTypeCreate, scenarioTriggerType);

    if (scenarioTriggerTypeCreate.getEventCanonicalName() != null
        && (!scenarioTriggerTypeCreate
            .getEventCanonicalName()
            .equals(scenarioTriggerType.getEventCanonicalName()))) {
      scenarioTriggerType.setEventCanonicalName(scenarioTriggerTypeCreate.getEventCanonicalName());
      update = true;
    }

    return update;
  }
  /**
   * @param scenarioTriggerTypeUpdate
   * @param securityContext
   * @return scenarioTriggerType
   */
  public ScenarioTriggerType updateScenarioTriggerType(
      ScenarioTriggerTypeUpdate scenarioTriggerTypeUpdate, SecurityContextBase securityContext) {
    ScenarioTriggerType scenarioTriggerType = scenarioTriggerTypeUpdate.getScenarioTriggerType();
    if (updateScenarioTriggerTypeNoMerge(scenarioTriggerType, scenarioTriggerTypeUpdate)) {
      this.repository.merge(scenarioTriggerType);
    }
    return scenarioTriggerType;
  }

  /**
   * @param scenarioTriggerTypeFilter Object Used to List ScenarioTriggerType
   * @param securityContext
   * @return PaginationResponse containing paging information for ScenarioTriggerType
   */
  public PaginationResponse<ScenarioTriggerType> getAllScenarioTriggerTypes(
      ScenarioTriggerTypeFilter scenarioTriggerTypeFilter, SecurityContextBase securityContext) {
    List<ScenarioTriggerType> list =
        listAllScenarioTriggerTypes(scenarioTriggerTypeFilter, securityContext);
    long count =
        this.repository.countAllScenarioTriggerTypes(scenarioTriggerTypeFilter, securityContext);
    return new PaginationResponse<>(list, scenarioTriggerTypeFilter, count);
  }

  /**
   * @param scenarioTriggerTypeFilter Object Used to List ScenarioTriggerType
   * @param securityContext
   * @return List of ScenarioTriggerType
   */
  public List<ScenarioTriggerType> listAllScenarioTriggerTypes(
      ScenarioTriggerTypeFilter scenarioTriggerTypeFilter, SecurityContextBase securityContext) {
    return this.repository.listAllScenarioTriggerTypes(scenarioTriggerTypeFilter, securityContext);
  }

  /**
   * @param scenarioTriggerTypeFilter Object Used to List ScenarioTriggerType
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioTriggerTypeFilter is not valid
   */
  public void validate(
      ScenarioTriggerTypeFilter scenarioTriggerTypeFilter, SecurityContextBase securityContext) {
    basicService.validate(scenarioTriggerTypeFilter, securityContext);
  }

  /**
   * @param scenarioTriggerTypeCreate Object Used to Create ScenarioTriggerType
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scenarioTriggerTypeCreate is not valid
   */
  public void validate(
      ScenarioTriggerTypeCreate scenarioTriggerTypeCreate, SecurityContextBase securityContext) {
    basicService.validate(scenarioTriggerTypeCreate, securityContext);
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
