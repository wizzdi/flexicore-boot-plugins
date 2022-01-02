package com.flexicore.scheduling.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.request.ScheduleCreate;
import com.flexicore.scheduling.request.ScheduleFilter;
import com.flexicore.scheduling.request.ScheduleUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IScheduleService {

  /**
   * @param scheduleCreate Object Used to Create Entity1
   * @param securityContext
   * @return created Schedule
   */
  Schedule createSchedule(ScheduleCreate scheduleCreate, SecurityContextBase securityContext);

  /**
   * @param scheduleCreate Object Used to Create Entity1
   * @param securityContext
   * @return created Schedule unmerged
   */
  Schedule createScheduleNoMerge(
      ScheduleCreate scheduleCreate, SecurityContextBase securityContext);

  /**
   * @param scheduleCreate Object Used to Create Entity1
   * @param schedule
   * @return if schedule was updated
   */
  boolean updateScheduleNoMerge(Schedule schedule, ScheduleCreate scheduleCreate);

  /**
   * @param scheduleUpdate
   * @param securityContext
   * @return schedule
   */
  Schedule updateSchedule(ScheduleUpdate scheduleUpdate, SecurityContextBase securityContext);

  /**
   * @param scheduleFilter Object Used to List Entity1
   * @param securityContext
   * @return PaginationResponse containing paging information for Schedule
   */
  PaginationResponse<Schedule> getAllSchedules(
      ScheduleFilter scheduleFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleFilter Object Used to List Entity1
   * @param securityContext
   * @return List of Schedule
   */
  List<Schedule> listAllSchedules(
      ScheduleFilter scheduleFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleFilter Object Used to List Entity1
   * @param securityContext
   * @throws ResponseStatusException if scheduleFilter is not valid
   */
  void validate(ScheduleFilter scheduleFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleCreate Object Used to Create Entity1
   * @param securityContext
   * @throws ResponseStatusException if scheduleCreate is not valid
   */
  void validate(ScheduleCreate scheduleCreate, SecurityContextBase securityContext);

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
