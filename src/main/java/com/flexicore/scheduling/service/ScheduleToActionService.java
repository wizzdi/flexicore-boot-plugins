package com.flexicore.scheduling.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.scheduling.data.ScheduleToActionRepository;
import com.flexicore.scheduling.request.ScheduleToActionCreate;
import com.flexicore.scheduling.request.ScheduleToActionFilter;
import com.flexicore.scheduling.request.ScheduleToActionUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class ScheduleToActionService implements Plugin, IScheduleToActionService {

  @Autowired private ScheduleToActionRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param scheduleToActionCreate Object Used to Create ScheduleToScheduleAction
   * @param securityContext
   * @return created ScheduleToAction
   */
  @Override
  public ScheduleToAction createScheduleToAction(
      ScheduleToActionCreate scheduleToActionCreate, SecurityContextBase securityContext) {
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
  @Override
  public ScheduleToAction createScheduleToActionNoMerge(
      ScheduleToActionCreate scheduleToActionCreate, SecurityContextBase securityContext) {
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
  @Override
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
  @Override
  public ScheduleToAction updateScheduleToAction(
      ScheduleToActionUpdate scheduleToActionUpdate, SecurityContextBase securityContext) {
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
  @Override
  public PaginationResponse<ScheduleToAction> getAllScheduleToActions(
      ScheduleToActionFilter scheduleToActionFilter, SecurityContextBase securityContext) {
    List<ScheduleToAction> list = listAllScheduleToActions(scheduleToActionFilter, securityContext);
    long count = repository.countAllScheduleToActions(scheduleToActionFilter, securityContext);
    return new PaginationResponse<>(list, scheduleToActionFilter, count);
  }

  /**
   * @param scheduleToActionFilter Object Used to List ScheduleToScheduleAction
   * @param securityContext
   * @return List of ScheduleToAction
   */
  @Override
  public List<ScheduleToAction> listAllScheduleToActions(
      ScheduleToActionFilter scheduleToActionFilter, SecurityContextBase securityContext) {
    return repository.listAllScheduleToActions(scheduleToActionFilter, securityContext);
  }

  /**
   * @param scheduleToActionFilter Object Used to List ScheduleToScheduleAction
   * @param securityContext
   * @throws ResponseStatusException if scheduleToActionFilter is not valid
   */
  @Override
  public void validate(
      ScheduleToActionFilter scheduleToActionFilter, SecurityContextBase securityContext) {
    basicService.validate(scheduleToActionFilter, securityContext);
  }

  /**
   * @param scheduleToActionCreate Object Used to Create ScheduleToScheduleAction
   * @param securityContext
   * @throws ResponseStatusException if scheduleToActionCreate is not valid
   */
  @Override
  public void validate(
      ScheduleToActionCreate scheduleToActionCreate, SecurityContextBase securityContext) {
    basicService.validate(scheduleToActionCreate, securityContext);
  }

  @Override
  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return repository.listByIds(c, ids, securityContext);
  }

  @Override
  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return repository.getByIdOrNull(id, c, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return repository.findByIds(c, ids, idAttribute);
  }

  @Override
  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return repository.findByIds(c, requested);
  }

  @Override
  public <T> T findByIdOrNull(Class<T> type, String id) {
    return repository.findByIdOrNull(type, id);
  }

  @Override
  public void merge(java.lang.Object base) {
    repository.merge(base);
  }

  @Override
  public void massMerge(List<?> toMerge) {
    repository.massMerge(toMerge);
  }
}
