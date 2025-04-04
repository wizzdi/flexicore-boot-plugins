package com.flexicore.scheduling.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleTimeslot;
import com.flexicore.scheduling.data.ScheduleTimeslotRepository;
import com.flexicore.scheduling.request.ScheduleTimeslotCreate;
import com.flexicore.scheduling.request.ScheduleTimeslotFilter;
import com.flexicore.scheduling.request.ScheduleTimeslotUpdate;
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
public class ScheduleTimeslotService implements Plugin  {

  @Autowired private ScheduleTimeslotRepository repository;

  @Autowired private BasicService basicService;
  @Autowired private ScheduleService scheduleService;
  /**
   * @param scheduleTimeslotCreate Object Used to Create ScheduleTimeslot
   * @param securityContext
   * @return created ScheduleTimeslot
   */

  public ScheduleTimeslot createScheduleTimeslot(
      ScheduleTimeslotCreate scheduleTimeslotCreate, SecurityContext securityContext) {
    ScheduleTimeslot scheduleTimeslot =
        createScheduleTimeslotNoMerge(scheduleTimeslotCreate, securityContext);
    repository.merge(scheduleTimeslot);
    return scheduleTimeslot;
  }

  /**
   * @param scheduleTimeslotCreate Object Used to Create ScheduleTimeslot
   * @param securityContext
   * @return created ScheduleTimeslot unmerged
   */

  public ScheduleTimeslot createScheduleTimeslotNoMerge(
      ScheduleTimeslotCreate scheduleTimeslotCreate, SecurityContext securityContext) {
    ScheduleTimeslot scheduleTimeslot = new ScheduleTimeslot();
    scheduleTimeslot.setId(UUID.randomUUID().toString());
    updateScheduleTimeslotNoMerge(scheduleTimeslot, scheduleTimeslotCreate);

    BaseclassService.createSecurityObjectNoMerge(scheduleTimeslot, securityContext);

    return scheduleTimeslot;
  }

  /**
   * @param scheduleTimeslotCreate Object Used to Create ScheduleTimeslot
   * @param scheduleTimeslot
   * @return if scheduleTimeslot was updated
   */

  public boolean updateScheduleTimeslotNoMerge(
      ScheduleTimeslot scheduleTimeslot, ScheduleTimeslotCreate scheduleTimeslotCreate) {
    boolean update = basicService.updateBasicNoMerge(scheduleTimeslotCreate, scheduleTimeslot);

    if (scheduleTimeslotCreate.getStartTime() != null && !scheduleTimeslotCreate.getStartTime().equals(scheduleTimeslot.getStartTime())) {
      scheduleTimeslot.setStartTime(scheduleTimeslotCreate.getStartTime());
      scheduleTimeslot.setStartTimeOfTheDayName(null);
      update = true;
    }

    if (scheduleTimeslotCreate.getEndTime() != null && !scheduleTimeslotCreate.getEndTime().equals(scheduleTimeslot.getEndTime())) {
      scheduleTimeslot.setEndTime(scheduleTimeslotCreate.getEndTime());
      scheduleTimeslot.setEndTimeOfTheDayName(null);
      update = true;
    }
    if (scheduleTimeslotCreate.getLog() != null &&(scheduleTimeslot.getLog()==null || !scheduleTimeslotCreate.getLog().equals(scheduleTimeslot.getLog()))) {
           scheduleTimeslot.setLog(scheduleTimeslotCreate.getLog());
      update = true;
    }
    if (scheduleTimeslotCreate.getEndTimeOfTheDayName() != null
        && (!scheduleTimeslotCreate
            .getEndTimeOfTheDayName()
            .equals(scheduleTimeslot.getEndTimeOfTheDayName()))) {
      scheduleTimeslot.setEndTimeOfTheDayName(scheduleTimeslotCreate.getEndTimeOfTheDayName());
      update = true;
    }


    if (scheduleTimeslotCreate.getSchedule() != null
        && (scheduleTimeslot.getSchedule() == null
            || !scheduleTimeslotCreate
                .getSchedule()
                .getId()
                .equals(scheduleTimeslot.getSchedule().getId()))) {
      scheduleTimeslot.setSchedule(scheduleTimeslotCreate.getSchedule());
      update = true;
    }

    if (scheduleTimeslotCreate.getStartTimeOfTheDayName() != null
        && (!scheduleTimeslotCreate
            .getStartTimeOfTheDayName()
            .equals(scheduleTimeslot.getStartTimeOfTheDayName()))) {
      scheduleTimeslot.setStartTimeOfTheDayName(scheduleTimeslotCreate.getStartTimeOfTheDayName());
      update = true;
    }

    if (scheduleTimeslotCreate.getTimeOfTheDayNameStartLat() != null
        && (!scheduleTimeslotCreate
            .getTimeOfTheDayNameStartLat()
            .equals(scheduleTimeslot.getTimeOfTheDayNameStartLat()))) {
      scheduleTimeslot.setTimeOfTheDayNameStartLat(
          scheduleTimeslotCreate.getTimeOfTheDayNameStartLat());
      update = true;
    }

    if (scheduleTimeslotCreate.getTimeOfTheDayNameStartLon() != null
        && (!scheduleTimeslotCreate
            .getTimeOfTheDayNameStartLon()
            .equals(scheduleTimeslot.getTimeOfTheDayNameStartLon()))) {
      scheduleTimeslot.setTimeOfTheDayNameStartLon(
          scheduleTimeslotCreate.getTimeOfTheDayNameStartLon());
      update = true;
    }

    if (scheduleTimeslotCreate.getStartMillisOffset() != null
        && (!scheduleTimeslotCreate
            .getStartMillisOffset()
            .equals(scheduleTimeslot.getStartMillisOffset()))) {
      scheduleTimeslot.setStartMillisOffset(scheduleTimeslotCreate.getStartMillisOffset());
      update = true;
    }

    if (scheduleTimeslotCreate.getEndMillisOffset() != null
        && (!scheduleTimeslotCreate
            .getEndMillisOffset()
            .equals(scheduleTimeslot.getEndMillisOffset()))) {
      scheduleTimeslot.setEndMillisOffset(scheduleTimeslotCreate.getEndMillisOffset());
      update = true;
    }

    if (scheduleTimeslotCreate.getLastExecution() != null
        && (!scheduleTimeslotCreate
            .getLastExecution()
            .equals(scheduleTimeslot.getLastExecution()))) {
      scheduleTimeslot.setLastExecution(scheduleTimeslotCreate.getLastExecution());
      update = true;
    }

    if (scheduleTimeslotCreate.getTimeOfTheDayNameEndLat() != null
        && (!scheduleTimeslotCreate
            .getTimeOfTheDayNameEndLat()
            .equals(scheduleTimeslot.getTimeOfTheDayNameEndLat()))) {
      scheduleTimeslot.setTimeOfTheDayNameEndLat(
          scheduleTimeslotCreate.getTimeOfTheDayNameEndLat());
      update = true;
    }

    if (scheduleTimeslotCreate.getTimeOfTheDayNameEndLon() != null
        && (!scheduleTimeslotCreate
            .getTimeOfTheDayNameEndLon()
            .equals(scheduleTimeslot.getTimeOfTheDayNameEndLon()))) {
      scheduleTimeslot.setTimeOfTheDayNameEndLon(
          scheduleTimeslotCreate.getTimeOfTheDayNameEndLon());
      update = true;
    }

    if (scheduleTimeslotCreate.getCoolDownIntervalBeforeRepeat() != null
        && (!scheduleTimeslotCreate
            .getCoolDownIntervalBeforeRepeat()
            .equals(scheduleTimeslot.getCoolDownIntervalBeforeRepeat()))) {
      scheduleTimeslot.setCoolDownIntervalBeforeRepeat(
          scheduleTimeslotCreate.getCoolDownIntervalBeforeRepeat());
      update = true;
    }

    return update;
  }
  /**
   * @param scheduleTimeslotUpdate
   * @param securityContext
   * @return scheduleTimeslot
   */

  public ScheduleTimeslot updateScheduleTimeslot(
      ScheduleTimeslotUpdate scheduleTimeslotUpdate, SecurityContext securityContext) {
    ScheduleTimeslot scheduleTimeslot = scheduleTimeslotUpdate.getScheduleTimeslot();
    if (updateScheduleTimeslotNoMerge(scheduleTimeslot, scheduleTimeslotUpdate)) {
      repository.merge(scheduleTimeslot);
    }
    return scheduleTimeslot;
  }

  /**
   * @param scheduleTimeslotFilter Object Used to List ScheduleTimeslot
   * @param securityContext
   * @return PaginationResponse containing paging information for ScheduleTimeslot
   */

  public PaginationResponse<ScheduleTimeslot> getAllScheduleTimeslots(
      ScheduleTimeslotFilter scheduleTimeslotFilter, SecurityContext securityContext) {
    List<ScheduleTimeslot> list = listAllScheduleTimeslots(scheduleTimeslotFilter, securityContext);
    long count = repository.countAllScheduleTimeslots(scheduleTimeslotFilter, securityContext);
    return new PaginationResponse<>(list, scheduleTimeslotFilter, count);
  }

  /**
   * @param scheduleTimeslotFilter Object Used to List ScheduleTimeslot
   * @param securityContext
   * @return List of ScheduleTimeslot
   */

  public List<ScheduleTimeslot> listAllScheduleTimeslots(
      ScheduleTimeslotFilter scheduleTimeslotFilter, SecurityContext securityContext) {
    return repository.listAllScheduleTimeslots(scheduleTimeslotFilter, securityContext);
  }

  /**
   * @param scheduleTimeslotFilter Object Used to List ScheduleTimeslot
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleTimeslotFilter is not valid
   */

  public void validate(
      ScheduleTimeslotFilter scheduleTimeslotFilter, SecurityContext securityContext) {
    basicService.validate(scheduleTimeslotFilter, securityContext);

    Set<String> scheduleIds =
        scheduleTimeslotFilter.getScheduleIds() == null
            ? new HashSet<>()
            : scheduleTimeslotFilter.getScheduleIds();
    Map<String, Schedule> schedule =
        scheduleIds.isEmpty()
            ? new HashMap<>()
            : repository
                .listByIds(Schedule.class, scheduleIds,securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    scheduleIds.removeAll(schedule.keySet());
    if (!scheduleIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Schedule with ids " + scheduleIds);
    }
    scheduleTimeslotFilter.setSchedule(new ArrayList<>(schedule.values()));
  }

  /**
   * @param stc Object Used to Create ScheduleTimeslot
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if stc is not valid
   */

  public void validate(
      ScheduleTimeslotCreate stc, SecurityContext securityContext) {
    validateBase(stc, securityContext);


  }

  private void validateBase(ScheduleTimeslotCreate stc, SecurityContext securityContext) {
    basicService.validate(stc, securityContext);

    String scheduleId = stc.getScheduleId();
    Schedule schedule =
        scheduleId == null
            ? null
            : repository.getByIdOrNull(
                scheduleId, Schedule.class,  securityContext);
    if (scheduleId != null && schedule == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Schedule with id " + scheduleId);
    }
    stc.setSchedule(schedule);
  }

  public void validateUpdate(
          ScheduleTimeslotUpdate stc, SecurityContext securityContext) {
   validateBase(stc, securityContext);
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
