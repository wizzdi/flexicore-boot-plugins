package com.flexicore.scheduling.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.data.ScheduleRepository;
import com.flexicore.scheduling.request.ScheduleCreate;
import com.flexicore.scheduling.request.ScheduleFilter;
import com.flexicore.scheduling.request.ScheduleUpdate;
import com.flexicore.scheduling.response.ActionResult;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Extension
public class ScheduleService implements Plugin  {

  @Autowired private ScheduleRepository repository;

  @Autowired private BasicService basicService;
  private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
  /**
   * @param scheduleCreate Object Used to Create Entity1
   * @param securityContext
   * @return created Schedule
   */

  public Schedule createSchedule(
      ScheduleCreate scheduleCreate, SecurityContextBase securityContext) {
    Schedule schedule = createScheduleNoMerge(scheduleCreate, securityContext);
    repository.merge(schedule);
    return schedule;
  }

  /**
   * @param scheduleCreate Object Used to Create Entity1
   * @param securityContext
   * @return created Schedule unmerged
   */

  public Schedule createScheduleNoMerge(
      ScheduleCreate scheduleCreate, SecurityContextBase securityContext) {
    Schedule schedule = new Schedule();
    schedule.setId(UUID.randomUUID().toString());
    updateScheduleNoMerge(schedule, scheduleCreate);

    BaseclassService.createSecurityObjectNoMerge(schedule, securityContext);

    return schedule;
  }

  /**
   * @param scheduleCreate Object Used to Create Entity1
   * @param schedule
   * @return if schedule was updated
   */

  public boolean updateScheduleNoMerge(Schedule schedule, ScheduleCreate scheduleCreate) {
    boolean update = basicService.updateBasicNoMerge(scheduleCreate, schedule);

    if (scheduleCreate.isSunday() != null
        && (!scheduleCreate.isSunday().equals(schedule.isSunday()))) {
      schedule.setSunday(scheduleCreate.isSunday());
      update = true;
    }

    if (scheduleCreate.isTuesday() != null
        && (!scheduleCreate.isTuesday().equals(schedule.isTuesday()))) {
      schedule.setTuesday(scheduleCreate.isTuesday());
      update = true;
    }

    if (scheduleCreate.isThursday() != null
        && (!scheduleCreate.isThursday().equals(schedule.isThursday()))) {
      schedule.setThursday(scheduleCreate.isThursday());
      update = true;
    }

    if (scheduleCreate.isWednesday() != null
        && (!scheduleCreate.isWednesday().equals(schedule.isWednesday()))) {
      schedule.setWednesday(scheduleCreate.isWednesday());
      update = true;
    }

    if (scheduleCreate.isFriday() != null
        && (!scheduleCreate.isFriday().equals(schedule.isFriday()))) {
      schedule.setFriday(scheduleCreate.isFriday());
      update = true;
    }

    if (scheduleCreate.isSaturday() != null
        && (!scheduleCreate.isSaturday().equals(schedule.isSaturday()))) {
      schedule.setSaturday(scheduleCreate.isSaturday());
      update = true;
    }

    if (scheduleCreate.isHoliday() != null
        && (!scheduleCreate.isHoliday().equals(schedule.isHoliday()))) {
      schedule.setHoliday(scheduleCreate.isHoliday());
      update = true;
    }

    if (scheduleCreate.isEnabled() != null
        && (!scheduleCreate.isEnabled().equals(schedule.isEnabled()))) {
      schedule.setEnabled(scheduleCreate.isEnabled());
      update = true;
    }

    if (scheduleCreate.isMonday() != null
        && (!scheduleCreate.isMonday().equals(schedule.isMonday()))) {
      schedule.setMonday(scheduleCreate.isMonday());
      update = true;
    }

    if (scheduleCreate.getTimeFrameStart() != null
        && (schedule.getTimeFrameStart()==null || !scheduleCreate.getTimeFrameStart().equals(schedule.getTimeFrameStart()))) {
      schedule.setTimeFrameStart(scheduleCreate.getTimeFrameStart());
      update = true;
    }
    if (scheduleCreate.getTimeFrameEnd() != null
            && (schedule.getTimeFrameEnd()==null || !scheduleCreate.getTimeFrameEnd().equals(schedule.getTimeFrameEnd()))) {
      schedule.setTimeFrameEnd(scheduleCreate.getTimeFrameEnd());
      update = true;
    }


    if (scheduleCreate.getSelectedTimeZone() != null
            && (schedule.getSelectedTimeZone()==null || !scheduleCreate.getSelectedTimeZone().equals(schedule.getSelectedTimeZone()))) {
      schedule.setSelectedTimeZone(scheduleCreate.getSelectedTimeZone());


      update = true;
    }

    return update;
  }
  /**
   * @param scheduleUpdate
   * @param securityContext
   * @return schedule
   */

  public Schedule updateSchedule(
      ScheduleUpdate scheduleUpdate, SecurityContextBase securityContext) {
    Schedule schedule = scheduleUpdate.getSchedule();
    if (updateScheduleNoMerge(schedule, scheduleUpdate)) {
      repository.merge(schedule);
    }
    return schedule;
  }

  /**
   * @param scheduleFilter Object Used to List Entity1
   * @param securityContext
   * @return PaginationResponse containing paging information for Schedule
   */

  public PaginationResponse<Schedule> getAllSchedules(
      ScheduleFilter scheduleFilter, SecurityContextBase securityContext) {
    List<Schedule> list = listAllSchedules(scheduleFilter, securityContext);
    long count = repository.countAllSchedules(scheduleFilter, securityContext);
    return new PaginationResponse<>(list, scheduleFilter, count);
  }

  /**
   * @param scheduleFilter Object Used to List Entity1
   * @param securityContext
   * @return List of Schedule
   */

  public List<Schedule> listAllSchedules(
      ScheduleFilter scheduleFilter, SecurityContextBase securityContext) {
    return repository.listAllSchedules(scheduleFilter, securityContext);
  }

  /**
   * @param scheduleFilter Object Used to List Entity1
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleFilter is not valid
   */

  public void validate(ScheduleFilter scheduleFilter, SecurityContextBase securityContext) {
    basicService.validate(scheduleFilter, securityContext);
  }

  /**
   * @param sc Object Used to Create Entity1
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleCreate is not valid
   */

  public void validate(ScheduleCreate sc, SecurityContextBase securityContext) {
    basicService.validate(sc, securityContext);
    if(sc.getSelectedTimeZone()!=null){
      if (sc.getTimeFrameStart() != null) {
        sc.setTimeFrameStart(convertToOffsetDateTime(sc.getTimeFrameStart(),
                sc.getSelectedTimeZone()).withHour(0).withMinute(0).withSecond(0).withNano(0));

      }
      if(sc.getTimeFrameEnd()!=null){
        sc.setTimeFrameEnd(convertToOffsetDateTime(sc.getTimeFrameEnd(),sc.getSelectedTimeZone()).withHour(23).withMinute(59).withSecond(59).withNano(999999999));

      }
    }




  }
  public OffsetDateTime convertToOffsetDateTime(OffsetDateTime offsetDateTime, String timeZone) {
    if (timeZone==null) {
      return offsetDateTime;
    }
    return (offsetDateTime.atZoneSameInstant(ZoneId.of(timeZone)).toOffsetDateTime());
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return repository.listByIds(c, ids, securityContext);
  }


  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return repository.getByIdOrNull(id, c, securityContext);
  }


  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }


  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
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
