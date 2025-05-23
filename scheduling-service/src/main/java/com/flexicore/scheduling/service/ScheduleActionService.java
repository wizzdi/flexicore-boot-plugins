package com.flexicore.scheduling.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.scheduling.data.ScheduleActionRepository;
import com.flexicore.scheduling.request.ScheduleActionCreate;
import com.flexicore.scheduling.request.ScheduleActionFilter;
import com.flexicore.scheduling.request.ScheduleActionUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
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
public class ScheduleActionService implements Plugin {

  @Autowired private ScheduleActionRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param scheduleActionCreate Object Used to Create ScheduleAction
   * @param securityContext
   * @return created ScheduleAction
   */

  public ScheduleAction createScheduleAction(
      ScheduleActionCreate scheduleActionCreate, SecurityContext securityContext) {
    ScheduleAction scheduleAction =
        createScheduleActionNoMerge(scheduleActionCreate, securityContext);
    repository.merge(scheduleAction);
    return scheduleAction;
  }

  /**
   * @param scheduleActionCreate Object Used to Create ScheduleAction
   * @param securityContext
   * @return created ScheduleAction unmerged
   */

  public ScheduleAction createScheduleActionNoMerge(
      ScheduleActionCreate scheduleActionCreate, SecurityContext securityContext) {
    ScheduleAction scheduleAction = new ScheduleAction();
    scheduleAction.setId(UUID.randomUUID().toString());
    updateScheduleActionNoMerge(scheduleAction, scheduleActionCreate);

    BaseclassService.createSecurityObjectNoMerge(scheduleAction, securityContext);

    return scheduleAction;
  }

  /**
   * @param scheduleActionCreate Object Used to Create ScheduleAction
   * @param scheduleAction
   * @return if scheduleAction was updated
   */

  public boolean updateScheduleActionNoMerge(
      ScheduleAction scheduleAction, ScheduleActionCreate scheduleActionCreate) {
    boolean update = basicService.updateBasicNoMerge(scheduleActionCreate, scheduleAction);

    if (scheduleActionCreate.getDynamicExecution() != null
        && (scheduleAction.getDynamicExecution() == null
            || !scheduleActionCreate
                .getDynamicExecution()
                .getId()
                .equals(scheduleAction.getDynamicExecution().getId()))) {
      scheduleAction.setDynamicExecution(scheduleActionCreate.getDynamicExecution());
      update = true;
    }
    if (scheduleActionCreate.getLastExecution() != null
            && (scheduleAction.getLastExecution() == null
            || !scheduleActionCreate
            .getLastExecution()

            .equals(scheduleAction.getLastExecution()))) {
      scheduleAction.setLastExecution(scheduleActionCreate.getLastExecution());
      update = true;
    }

    return update;
  }
  /**
   * @param scheduleActionUpdate
   * @param securityContext
   * @return scheduleAction
   */

  public ScheduleAction updateScheduleAction(
      ScheduleActionUpdate scheduleActionUpdate, SecurityContext securityContext) {
    ScheduleAction scheduleAction = scheduleActionUpdate.getScheduleAction();
    if (updateScheduleActionNoMerge(scheduleAction, scheduleActionUpdate)) {
      repository.merge(scheduleAction);
    }
    return scheduleAction;
  }

  /**
   * @param scheduleActionFilter Object Used to List ScheduleAction
   * @param securityContext
   * @return PaginationResponse containing paging information for ScheduleAction
   */

  public PaginationResponse<ScheduleAction> getAllScheduleActions(
      ScheduleActionFilter scheduleActionFilter, SecurityContext securityContext) {
    List<ScheduleAction> list = listAllScheduleActions(scheduleActionFilter, securityContext);
    long count = repository.countAllScheduleActions(scheduleActionFilter, securityContext);
    return new PaginationResponse<>(list, scheduleActionFilter, count);
  }

  /**
   * @param scheduleActionFilter Object Used to List ScheduleAction
   * @param securityContext
   * @return List of ScheduleAction
   */

  public List<ScheduleAction> listAllScheduleActions(
      ScheduleActionFilter scheduleActionFilter, SecurityContext securityContext) {
    return repository.listAllScheduleActions(scheduleActionFilter, securityContext);
  }

  /**
   * @param scheduleActionFilter Object Used to List ScheduleAction
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleActionFilter is not valid
   */

  public void validate(
      ScheduleActionFilter scheduleActionFilter, SecurityContext securityContext) {
    basicService.validate(scheduleActionFilter, securityContext);

    Set<String> dynamicExecutionIds =
        scheduleActionFilter.getDynamicExecutionIds() == null
            ? new HashSet<>()
            : scheduleActionFilter.getDynamicExecutionIds();
    Map<String, DynamicExecution> dynamicExecution =
        dynamicExecutionIds.isEmpty()
            ? new HashMap<>()
            : repository
                .listByIds(
                    DynamicExecution.class,
                    dynamicExecutionIds,
                    securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    dynamicExecutionIds.removeAll(dynamicExecution.keySet());
    if (!dynamicExecutionIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No DynamicExecution with ids " + dynamicExecutionIds);
    }
    scheduleActionFilter.setDynamicExecution(new ArrayList<>(dynamicExecution.values()));
  }

  /**
   * @param scheduleActionCreate Object Used to Create ScheduleAction
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleActionCreate is not valid
   */

  public void validate(
      ScheduleActionCreate scheduleActionCreate, SecurityContext securityContext) {
    basicService.validate(scheduleActionCreate, securityContext);

    String dynamicExecutionId = scheduleActionCreate.getDynamicExecutionId();
    DynamicExecution dynamicExecution =
        dynamicExecutionId == null
            ? null
            : repository.getByIdOrNull(
                dynamicExecutionId,
                DynamicExecution.class,
                
                securityContext);
    if (dynamicExecutionId != null && dynamicExecution == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No DynamicExecution with id " + dynamicExecutionId);
    }
    scheduleActionCreate.setDynamicExecution(dynamicExecution);
  }


  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContext securityContext) {
    return repository.listByIds(c, ids, securityContext);
  }


  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContext securityContext) {
    return repository.getByIdOrNull(id, c, securityContext);
  }


  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
    return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }


  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
    return repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }


  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return repository.findByIds(c, ids, idAttribute);
  }


  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return repository.findByIds(c, requested);
  }


  public <T> T findByIdOrNull(Class<T> type, String id) {
    return repository.findByIdOrNull(type, id);
  }


  public void merge(java.lang.Object base) {
    repository.merge(base);
  }


  public void massMerge(List<?> toMerge) {
    repository.massMerge(toMerge);
  }
}
