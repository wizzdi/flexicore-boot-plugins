package com.flexicore.scheduling.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.scheduling.model.ScheduleTimeslot;
import com.flexicore.scheduling.request.ScheduleTimeslotCreate;
import com.flexicore.scheduling.request.ScheduleTimeslotFilter;
import com.flexicore.scheduling.request.ScheduleTimeslotUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import jakarta.persistence.metamodel.SingularAttribute;

public interface IScheduleTimeslotService {

  /**
   * @param scheduleTimeslotCreate Object Used to Create ScheduleTimeslot
   * @param securityContext
   * @return created ScheduleTimeslot
   */
  ScheduleTimeslot createScheduleTimeslot(
      ScheduleTimeslotCreate scheduleTimeslotCreate, SecurityContextBase securityContext);

  /**
   * @param scheduleTimeslotCreate Object Used to Create ScheduleTimeslot
   * @param securityContext
   * @return created ScheduleTimeslot unmerged
   */
  ScheduleTimeslot createScheduleTimeslotNoMerge(
      ScheduleTimeslotCreate scheduleTimeslotCreate, SecurityContextBase securityContext);

  /**
   * @param scheduleTimeslotCreate Object Used to Create ScheduleTimeslot
   * @param scheduleTimeslot
   * @return if scheduleTimeslot was updated
   */
  boolean updateScheduleTimeslotNoMerge(
      ScheduleTimeslot scheduleTimeslot, ScheduleTimeslotCreate scheduleTimeslotCreate);

  /**
   * @param scheduleTimeslotUpdate
   * @param securityContext
   * @return scheduleTimeslot
   */
  ScheduleTimeslot updateScheduleTimeslot(
      ScheduleTimeslotUpdate scheduleTimeslotUpdate, SecurityContextBase securityContext);

  /**
   * @param scheduleTimeslotFilter Object Used to List ScheduleTimeslot
   * @param securityContext
   * @return PaginationResponse containing paging information for ScheduleTimeslot
   */
  PaginationResponse<ScheduleTimeslot> getAllScheduleTimeslots(
      ScheduleTimeslotFilter scheduleTimeslotFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleTimeslotFilter Object Used to List ScheduleTimeslot
   * @param securityContext
   * @return List of ScheduleTimeslot
   */
  List<ScheduleTimeslot> listAllScheduleTimeslots(
      ScheduleTimeslotFilter scheduleTimeslotFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleTimeslotFilter Object Used to List ScheduleTimeslot
   * @param securityContext
   * @throws ResponseStatusException if scheduleTimeslotFilter is not valid
   */
  void validate(ScheduleTimeslotFilter scheduleTimeslotFilter, SecurityContextBase securityContext);

  /**
   * @param scheduleTimeslotCreate Object Used to Create ScheduleTimeslot
   * @param securityContext
   * @throws ResponseStatusException if scheduleTimeslotCreate is not valid
   */
  void validate(ScheduleTimeslotCreate scheduleTimeslotCreate, SecurityContextBase securityContext);

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
