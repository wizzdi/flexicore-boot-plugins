package com.flexicore.scheduling.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.scheduling.request.ScheduleToActionCreate;
import com.flexicore.scheduling.request.ScheduleToActionFilter;
import com.flexicore.scheduling.request.ScheduleToActionUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IScheduleToActionService {

  /**
   * @param scheduleToActionCreate Object Used to Create ScheduleToScheduleAction
   * @param securityContext
   * @return created ScheduleToAction
   */
  ScheduleToAction createScheduleToAction(
      ScheduleToActionCreate scheduleToActionCreate, SecurityContextBase securityContext);

  /**
   * @param scheduleToActionCreate Object Used to Create ScheduleToScheduleAction
   * @param securityContext
   * @return created ScheduleToAction unmerged
   */
  ScheduleToAction createScheduleToActionNoMerge(
      ScheduleToActionCreate scheduleToActionCreate, SecurityContextBase securityContext);

  /**
   * @param scheduleToActionCreate Object Used to Create ScheduleToScheduleAction
   * @param scheduleToAction
   * @return if scheduleToAction was updated
   */
  boolean updateScheduleToActionNoMerge(
      ScheduleToAction scheduleToAction, ScheduleToActionCreate scheduleToActionCreate);

  /**
   * @param scheduleToActionUpdate
   * @param securityContext
   * @return scheduleToAction
   */
  ScheduleToAction updateScheduleToAction(
      ScheduleToActionUpdate scheduleToActionUpdate, SecurityContextBase securityContext);

  /**
   * @param scheduleToActionFilter Object Used to List ScheduleToScheduleAction
   * @param securityContext
   * @return PaginationResponse containing paging information for ScheduleToAction
   */
  PaginationResponse<ScheduleToAction> getAllScheduleToActions(
      ScheduleToActionFilter scheduleToActionFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleToActionFilter Object Used to List ScheduleToScheduleAction
   * @param securityContext
   * @return List of ScheduleToAction
   */
  List<ScheduleToAction> listAllScheduleToActions(
      ScheduleToActionFilter scheduleToActionFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleToActionFilter Object Used to List ScheduleToScheduleAction
   * @param securityContext
   * @throws ResponseStatusException if scheduleToActionFilter is not valid
   */
  void validate(ScheduleToActionFilter scheduleToActionFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleToActionCreate Object Used to Create ScheduleToScheduleAction
   * @param securityContext
   * @throws ResponseStatusException if scheduleToActionCreate is not valid
   */
  void validate(ScheduleToActionCreate scheduleToActionCreate, SecurityContextBase securityContext);

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
