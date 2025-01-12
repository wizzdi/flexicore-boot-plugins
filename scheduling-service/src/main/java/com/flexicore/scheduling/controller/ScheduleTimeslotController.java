package com.flexicore.scheduling.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.scheduling.model.ScheduleTimeslot;
import com.flexicore.scheduling.model.ScheduleTimeslot_;
import com.flexicore.scheduling.request.ScheduleTimeslotCreate;
import com.flexicore.scheduling.request.ScheduleTimeslotFilter;
import com.flexicore.scheduling.request.ScheduleTimeslotUpdate;
import com.flexicore.scheduling.service.ScheduleTimeslotService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("ScheduleTimeslot")
@Extension
@Tag(name = "ScheduleTimeslot")
@OperationsInside
public class ScheduleTimeslotController implements Plugin {

  @Autowired private ScheduleTimeslotService scheduleTimeslotService;

  @PostMapping("createScheduleTimeslot")
  @Operation(summary = "createScheduleTimeslot", description = "Creates ScheduleTimeslot")
  public ScheduleTimeslot createScheduleTimeslot(
      
      @RequestBody ScheduleTimeslotCreate scheduleTimeslotCreate,
      @RequestAttribute SecurityContext securityContext) {
    scheduleTimeslotService.validate(scheduleTimeslotCreate, securityContext);
    return scheduleTimeslotService.createScheduleTimeslot(scheduleTimeslotCreate, securityContext);
  }

  @Operation(summary = "updateScheduleTimeslot", description = "Updates ScheduleTimeslot")
  @PutMapping("updateScheduleTimeslot")
  public ScheduleTimeslot updateScheduleTimeslot(
      
      @RequestBody ScheduleTimeslotUpdate scheduleTimeslotUpdate,
      @RequestAttribute SecurityContext securityContext) {
    String scheduleTimeslotId = scheduleTimeslotUpdate.getId();
    ScheduleTimeslot scheduleTimeslot =
        scheduleTimeslotService.getByIdOrNull(
            scheduleTimeslotId,
            ScheduleTimeslot.class,
            securityContext);
    if (scheduleTimeslot == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScheduleTimeslot with id " + scheduleTimeslotId);
    }
    scheduleTimeslotUpdate.setScheduleTimeslot(scheduleTimeslot);
    scheduleTimeslotService.validate(scheduleTimeslotUpdate, securityContext);
    return scheduleTimeslotService.updateScheduleTimeslot(scheduleTimeslotUpdate, securityContext);
  }

  @Operation(
      summary = "getAllScheduleTimeslots",
      description = "Gets All ScheduleTimeslots Filtered")
  @PostMapping("getAllScheduleTimeslots")
  public PaginationResponse<ScheduleTimeslot> getAllScheduleTimeslots(
      
      @RequestBody ScheduleTimeslotFilter scheduleTimeslotFilter,
      @RequestAttribute SecurityContext securityContext) {
    scheduleTimeslotService.validate(scheduleTimeslotFilter, securityContext);
    return scheduleTimeslotService.getAllScheduleTimeslots(scheduleTimeslotFilter, securityContext);
  }
}
