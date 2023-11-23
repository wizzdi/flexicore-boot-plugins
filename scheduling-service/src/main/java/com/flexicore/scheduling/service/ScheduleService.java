package com.flexicore.scheduling.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.data.ScheduleRepository;
import com.flexicore.scheduling.request.NullActionBody;
import com.flexicore.scheduling.request.ScheduleCreate;
import com.flexicore.scheduling.request.ScheduleFilter;
import com.flexicore.scheduling.request.ScheduleUpdate;
import com.flexicore.scheduling.response.ActionResult;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class ScheduleService implements Plugin, IScheduleService {

  @Autowired private ScheduleRepository repository;

  @Autowired private BasicService basicService;
  private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
  /**
   * @param scheduleCreate Object Used to Create Entity1
   * @param securityContext
   * @return created Schedule
   */
  @Override
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
  @Override
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
  @Override
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
        && (!scheduleCreate.getTimeFrameStart().equals(schedule.getTimeFrameStart()))) {
      schedule.setTimeFrameStart(scheduleCreate.getTimeFrameStart());
      schedule.setStartTimeOffsetId(schedule.getTimeFrameStart().getOffset().getId());
      update = true;
    }
    if (scheduleCreate.getLog() != null
            && (!scheduleCreate.getLog().equals(schedule.getLog()))) {
      schedule.setLog(scheduleCreate.getLog());
      update = true;
    }
    if (scheduleCreate.getTimeFrameEnd() != null
        && (!scheduleCreate.getTimeFrameEnd().equals(schedule.getTimeFrameEnd()))) {
      schedule.setTimeFrameEnd(scheduleCreate.getTimeFrameEnd());
      schedule.setEndTimeOffsetId(schedule.getTimeFrameEnd().getOffset().getId());

      update = true;
    }

    return update;
  }
  /**
   * @param scheduleUpdate
   * @param securityContext
   * @return schedule
   */
  @Override
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
  @Override
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
  @Override
  public List<Schedule> listAllSchedules(
      ScheduleFilter scheduleFilter, SecurityContextBase securityContext) {
    return repository.listAllSchedules(scheduleFilter, securityContext);
  }

  /**
   * @param scheduleFilter Object Used to List Entity1
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleFilter is not valid
   */
  @Override
  public void validate(ScheduleFilter scheduleFilter, SecurityContextBase securityContext) {
    basicService.validate(scheduleFilter, securityContext);
  }

  /**
   * @param scheduleCreate Object Used to Create Entity1
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if scheduleCreate is not valid
   */
  @Override
  public void validate(ScheduleCreate scheduleCreate, SecurityContextBase securityContext) {
    basicService.validate(scheduleCreate, securityContext);
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

  public Long fireNullAction(NullActionBody nullActionBody, SecurityContextBase securityContext) {
    if (nullActionBody.getValue2()==null) nullActionBody.setValue2(99l);
    if (nullActionBody.getValue1()==null) nullActionBody.setValue1("no value provided");
    logger.info("Fired null action with value1:{} , value 2 {}",nullActionBody.getValue1(),nullActionBody.getValue2());
    return nullActionBody.getValue2();
  }

  public ActionResult fireNullActionWithResult(NullActionBody nullActionBody, SecurityContextBase securityContext) {
    return new ActionResult().setValue(nullActionBody.getValue1()).setExecutionTime(System.currentTimeMillis());
  }
}
