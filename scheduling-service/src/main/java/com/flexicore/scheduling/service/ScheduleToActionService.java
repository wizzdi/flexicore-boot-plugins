package com.flexicore.scheduling.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.scheduling.model.*;
import com.flexicore.scheduling.data.ScheduleToActionRepository;
import com.flexicore.scheduling.request.ScheduleToActionCreate;
import com.flexicore.scheduling.request.ScheduleToActionFilter;
import com.flexicore.scheduling.request.ScheduleToActionUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;

import java.util.*;
import java.util.stream.Collectors;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class ScheduleToActionService implements Plugin {

  @Autowired private ScheduleToActionRepository repository;

  @Autowired private BasicService basicService;
  @Autowired
  private ScheduleService scheduleService;

  /**
   * @param scheduleToActionCreate Object Used to Create ScheduleToScheduleAction
   * @param securityContext
   * @return created ScheduleToAction
   */

  public ScheduleToAction createScheduleToAction(
      ScheduleToActionCreate scheduleToActionCreate, SecurityContext securityContext) {
    ScheduleToAction scheduleToAction =
        createScheduleToActionNoMerge(scheduleToActionCreate, securityContext);
    repository.merge(scheduleToAction);
    return scheduleToAction;
  }

  /**
   * @param scheduleToActionCreate Object Used to Create ScheduleToScheduleAction
   * @param securityContext
   * @return created ScheduleToAction unmerged
   */

  public ScheduleToAction createScheduleToActionNoMerge(
      ScheduleToActionCreate scheduleToActionCreate, SecurityContext securityContext) {
    ScheduleToAction scheduleToAction = new ScheduleToAction();
    scheduleToAction.setId(UUID.randomUUID().toString());
    updateScheduleToActionNoMerge(scheduleToAction, scheduleToActionCreate);

    BaseclassService.createSecurityObjectNoMerge(scheduleToAction, securityContext);

    return scheduleToAction;
  }

  /**
   * @param scheduleToActionCreate Object Used to Create ScheduleToScheduleAction
   * @param scheduleToAction
   * @return if scheduleToAction was updated
   */

  public boolean updateScheduleToActionNoMerge(
      ScheduleToAction scheduleToAction, ScheduleToActionCreate scheduleToActionCreate) {
    boolean update = basicService.updateBasicNoMerge(scheduleToActionCreate, scheduleToAction);

    if (scheduleToActionCreate.getSchedule() != null
        && (scheduleToAction.getSchedule() == null
            || !scheduleToActionCreate
                .getSchedule()
                .getId()
                .equals(scheduleToAction.getSchedule().getId()))) {
      scheduleToAction.setSchedule(scheduleToActionCreate.getSchedule());
      update = true;
    }
    if (scheduleToActionCreate.getLastExecution() != null
            && (scheduleToAction.getLastExecution() == null
            || !scheduleToActionCreate
            .getLastExecution()

            .equals(scheduleToAction.getLastExecution()))) {
      scheduleToAction.setLastExecution(scheduleToActionCreate.getLastExecution());
      update = true;
    }

    if (scheduleToActionCreate.getScheduleAction() != null
        && (scheduleToAction.getScheduleAction() == null
            || !scheduleToActionCreate
                .getScheduleAction()
                .getId()
                .equals(scheduleToAction.getScheduleAction().getId()))) {
      scheduleToAction.setScheduleAction(scheduleToActionCreate.getScheduleAction());
      update = true;
    }

    return update;
  }
  /**
   * @param scheduleToActionUpdate
   * @param securityContext
   * @return scheduleToAction
   */

  public ScheduleToAction updateScheduleToAction(
      ScheduleToActionUpdate scheduleToActionUpdate, SecurityContext securityContext) {
    ScheduleToAction scheduleToAction = scheduleToActionUpdate.getScheduleToAction();
    if (updateScheduleToActionNoMerge(scheduleToAction, scheduleToActionUpdate)) {
      repository.merge(scheduleToAction);
    }
    return scheduleToAction;
  }

  /**
   * @param scheduleToActionFilter Object Used to List ScheduleToScheduleAction
   * @param securityContext
   * @return PaginationResponse containing paging information for ScheduleToAction
   */

  public PaginationResponse<ScheduleToAction> getAllScheduleToActions(
      ScheduleToActionFilter scheduleToActionFilter, SecurityContext securityContext) {
    List<ScheduleToAction> list = listAllScheduleToActions(scheduleToActionFilter, securityContext);
    long count = repository.countAllScheduleToActions(scheduleToActionFilter, securityContext);
    return new PaginationResponse<>(list, scheduleToActionFilter, count);
  }

  /**
   * @param scheduleToActionFilter Object Used to List ScheduleToScheduleAction
   * @param securityContext
   * @return List of ScheduleToAction
   */

  public List<ScheduleToAction> listAllScheduleToActions(
      ScheduleToActionFilter scheduleToActionFilter, SecurityContext securityContext) {
    List<ScheduleToAction> scheduleToActions = repository.listAllScheduleToActions(scheduleToActionFilter, securityContext);
    scheduleToActions.stream().forEach(f->{
      if (f.getLastExecution() != null) {
        scheduleService.convertToOffsetDateTime(f.getLastExecution(),f.getSchedule().getSelectedTimeZone());
      }
    });
    return scheduleToActions;
  }

  /**
   * @param scheduleToActionFilter Object Used to List ScheduleToScheduleAction
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleToActionFilter is not valid
   */

  public void validate(
      ScheduleToActionFilter scheduleToActionFilter, SecurityContext securityContext) {
    basicService.validate(scheduleToActionFilter, securityContext);
    Set<String> scheduleIds = scheduleToActionFilter.getScheduleIds();
    Map<String, Schedule> scheduleMap = scheduleIds.isEmpty() ? new HashMap<>() : repository.listByIds(Schedule.class, scheduleIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
    scheduleIds.removeAll(scheduleMap.keySet());
    if (!scheduleIds.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Schedule with ids " + scheduleIds + " were found, invalid schedule ids provided");
    }
    scheduleToActionFilter.setSchedule(new ArrayList<>(scheduleMap.values()));

    Set<String> scheduleActionIds = scheduleToActionFilter.getScheduleActionIds();
    Map<String, ScheduleAction> scheduleActionMap = scheduleActionIds.isEmpty() ? new HashMap<>() : repository.listByIds(ScheduleAction.class, scheduleActionIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
    scheduleActionIds.removeAll(scheduleActionMap.keySet());
    if (!scheduleActionIds.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ScheduleAction with ids " + scheduleActionIds + " were found, invalid scheduleAction ids provided");
    }
    scheduleToActionFilter.setScheduleAction(new ArrayList<>(scheduleActionMap.values()));

  }

  /**
   * @param scheduleToActionCreate Object Used to Create ScheduleToScheduleAction
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleToActionCreate is not valid
   */

  public void validate(
      ScheduleToActionCreate scheduleToActionCreate, SecurityContext securityContext) {
    basicService.validate(scheduleToActionCreate, securityContext);
    String scheduleId = scheduleToActionCreate.getScheduleId();
    Schedule schedule = scheduleId == null ? null : repository.getByIdOrNull(scheduleId, Schedule.class,securityContext);
    if (scheduleId != null && schedule == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Schedule with id " + scheduleId + " was found, invalid schedule id provided");
    }
    scheduleToActionCreate.setSchedule(schedule);

    String scheduleActionId = scheduleToActionCreate.getScheduleActionId();
    ScheduleAction scheduleAction = scheduleActionId == null ? null : repository.getByIdOrNull(scheduleActionId, ScheduleAction.class,securityContext);
    if (scheduleActionId != null && scheduleAction == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No ScheduleAction with id " + scheduleActionId + " was found, invalid scheduleAction id provided");
    }
    scheduleToActionCreate.setScheduleAction(scheduleAction);
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
