package com.flexicore.scheduling.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.Schedule_;
import com.flexicore.scheduling.request.NullActionBody;
import com.flexicore.scheduling.request.ScheduleCreate;
import com.flexicore.scheduling.request.ScheduleFilter;
import com.flexicore.scheduling.request.ScheduleUpdate;
import com.flexicore.scheduling.service.ScheduleService;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("Schedule")
@Extension
@Tag(name = "Schedule")
@OperationsInside
public class ScheduleController implements Plugin, Invoker {

  @Autowired private ScheduleService scheduleService;

  @PostMapping("createSchedule")
  @Operation(summary = "createSchedule", description = "Creates Schedule")
  public Schedule createSchedule(
      
      @RequestBody ScheduleCreate scheduleCreate,
      @RequestAttribute SecurityContextBase securityContext) {
    scheduleService.validate(scheduleCreate, securityContext);
    return scheduleService.createSchedule(scheduleCreate, securityContext);
  }

  @Operation(summary = "updateSchedule", description = "Updates Schedule")
  @PutMapping("updateSchedule")
  public Schedule updateSchedule(
      
      @RequestBody ScheduleUpdate scheduleUpdate,
      @RequestAttribute SecurityContextBase securityContext) {
    String scheduleId = scheduleUpdate.getId();
    Schedule schedule =
        scheduleService.getByIdOrNull(
            scheduleId, Schedule.class, Schedule_.security, securityContext);
    if (schedule == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Schedule with id " + scheduleId);
    }
    scheduleUpdate.setSchedule(schedule);
    scheduleService.validate(scheduleUpdate, securityContext);
    return scheduleService.updateSchedule(scheduleUpdate, securityContext);
  }

  @Operation(summary = "getAllSchedules", description = "Gets All Schedules Filtered")
  @PostMapping("getAllSchedules")
  public PaginationResponse<Schedule> getAllSchedules(
      
      @RequestBody ScheduleFilter scheduleFilter,
      @RequestAttribute SecurityContextBase securityContext) {
    scheduleService.validate(scheduleFilter, securityContext);
    return scheduleService.getAllSchedules(scheduleFilter, securityContext);
  }
  @PostMapping("nullAction")
  @Operation(summary = "null action for testing purposes of schedules")
  public Long activateNullAction(

          @RequestBody NullActionBody nullActionBody,
          @RequestAttribute SecurityContextBase securityContext) {

    return scheduleService.fireNullAction(nullActionBody, securityContext);
  }


  @Override
  public Class<?> getHandlingClass() {
    return Schedule.class;
  }
}
