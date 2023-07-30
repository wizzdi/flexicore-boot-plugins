package com.flexicore.scheduling.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.scheduling.model.ScheduleAction_;
import com.flexicore.scheduling.request.ScheduleActionCreate;
import com.flexicore.scheduling.request.ScheduleActionFilter;
import com.flexicore.scheduling.request.ScheduleActionUpdate;
import com.flexicore.scheduling.service.ScheduleActionService;
import com.flexicore.security.SecurityContextBase;
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
@RequestMapping("ScheduleAction")
@Extension
@Tag(name = "ScheduleAction")
@OperationsInside
public class ScheduleActionController implements Plugin {

  @Autowired private ScheduleActionService scheduleActionService;

  @PostMapping("createScheduleAction")
  @Operation(summary = "createScheduleAction", description = "Creates ScheduleAction")
  public ScheduleAction createScheduleAction(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScheduleActionCreate scheduleActionCreate,
      @RequestAttribute SecurityContextBase securityContext) {
    scheduleActionService.validate(scheduleActionCreate, securityContext);
    return scheduleActionService.createScheduleAction(scheduleActionCreate, securityContext);
  }

  @Operation(summary = "updateScheduleAction", description = "Updates ScheduleAction")
  @PutMapping("updateScheduleAction")
  public ScheduleAction updateScheduleAction(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScheduleActionUpdate scheduleActionUpdate,
      @RequestAttribute SecurityContextBase securityContext) {
    String scheduleActionId = scheduleActionUpdate.getId();
    ScheduleAction scheduleAction =
        scheduleActionService.getByIdOrNull(
            scheduleActionId, ScheduleAction.class, ScheduleAction_.security, securityContext);
    if (scheduleAction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScheduleAction with id " + scheduleActionId);
    }
    scheduleActionUpdate.setScheduleAction(scheduleAction);
    scheduleActionService.validate(scheduleActionUpdate, securityContext);
    return scheduleActionService.updateScheduleAction(scheduleActionUpdate, securityContext);
  }

  @Operation(summary = "getAllScheduleActions", description = "Gets All ScheduleActions Filtered")
  @PostMapping("getAllScheduleActions")
  public PaginationResponse<ScheduleAction> getAllScheduleActions(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScheduleActionFilter scheduleActionFilter,
      @RequestAttribute SecurityContextBase securityContext) {
    scheduleActionService.validate(scheduleActionFilter, securityContext);
    return scheduleActionService.getAllScheduleActions(scheduleActionFilter, securityContext);
  }
}
