package com.flexicore.scheduling.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.scheduling.request.ScheduleActionCreate;
import com.flexicore.scheduling.request.ScheduleActionFilter;
import com.flexicore.scheduling.request.ScheduleActionUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import jakarta.persistence.metamodel.SingularAttribute;

public interface IScheduleActionService {

  /**
   * @param scheduleActionCreate Object Used to Create ScheduleAction
   * @param securityContext
   * @return created ScheduleAction
   */
  ScheduleAction createScheduleAction(
      ScheduleActionCreate scheduleActionCreate, SecurityContextBase securityContext);

  /**
   * @param scheduleActionCreate Object Used to Create ScheduleAction
   * @param securityContext
   * @return created ScheduleAction unmerged
   */
  ScheduleAction createScheduleActionNoMerge(
      ScheduleActionCreate scheduleActionCreate, SecurityContextBase securityContext);

  /**
   * @param scheduleActionCreate Object Used to Create ScheduleAction
   * @param scheduleAction
   * @return if scheduleAction was updated
   */
  boolean updateScheduleActionNoMerge(
      ScheduleAction scheduleAction, ScheduleActionCreate scheduleActionCreate);

  /**
   * @param scheduleActionUpdate
   * @param securityContext
   * @return scheduleAction
   */
  ScheduleAction updateScheduleAction(
      ScheduleActionUpdate scheduleActionUpdate, SecurityContextBase securityContext);

  /**
   * @param scheduleActionFilter Object Used to List ScheduleAction
   * @param securityContext
   * @return PaginationResponse containing paging information for ScheduleAction
   */
  PaginationResponse<ScheduleAction> getAllScheduleActions(
      ScheduleActionFilter scheduleActionFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleActionFilter Object Used to List ScheduleAction
   * @param securityContext
   * @return List of ScheduleAction
   */
  List<ScheduleAction> listAllScheduleActions(
      ScheduleActionFilter scheduleActionFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleActionFilter Object Used to List ScheduleAction
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleActionFilter is not valid
   */
  void validate(ScheduleActionFilter scheduleActionFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleActionCreate Object Used to Create ScheduleAction
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleActionCreate is not valid
   */
  void validate(ScheduleActionCreate scheduleActionCreate, SecurityContextBase securityContext);

  <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext);

  <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext);

  <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext);

  <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext);

  <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute);

  <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested);

  <T> T findByIdOrNull(Class<T> type, String id);

  void merge(java.lang.Object base);

  void massMerge(List<?> toMerge);
}
