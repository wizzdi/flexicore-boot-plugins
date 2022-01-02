package com.flexicore.scheduling.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.scheduling.model.ScheduleToAction;
import com.flexicore.scheduling.model.ScheduleToAction_;
import com.flexicore.scheduling.request.ScheduleToActionCreate;
import com.flexicore.scheduling.request.ScheduleToActionFilter;
import com.flexicore.scheduling.request.ScheduleToActionUpdate;
import com.flexicore.scheduling.service.ScheduleToActionService;
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
@RequestMapping("ScheduleToAction")
@Extension
@Tag(name = "ScheduleToAction")
@OperationsInside
public class ScheduleToActionController implements Plugin {

  @Autowired private ScheduleToActionService scheduleToActionService;

  @PostMapping("createScheduleToAction")
  @Operation(summary = "createScheduleToAction", description = "Creates ScheduleToAction")
  public ScheduleToAction createScheduleToAction(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScheduleToActionCreate scheduleToActionCreate,
      @RequestAttribute SecurityContextBase securityContext) {
    scheduleToActionService.validate(scheduleToActionCreate, securityContext);
    return scheduleToActionService.createScheduleToAction(scheduleToActionCreate, securityContext);
  }

  @Operation(summary = "updateScheduleToAction", description = "Updates ScheduleToAction")
  @PutMapping("updateScheduleToAction")
  public ScheduleToAction updateScheduleToAction(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScheduleToActionUpdate scheduleToActionUpdate,
      @RequestAttribute SecurityContextBase securityContext) {
    String scheduleToActionId = scheduleToActionUpdate.getId();
    ScheduleToAction scheduleToAction =
        scheduleToActionService.getByIdOrNull(
            scheduleToActionId,
            ScheduleToAction.class,
            ScheduleToAction_.security,
            securityContext);
    if (scheduleToAction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScheduleToAction with id " + scheduleToActionId);
    }
    scheduleToActionUpdate.setScheduleToAction(scheduleToAction);
    scheduleToActionService.validate(scheduleToActionUpdate, securityContext);
    return scheduleToActionService.updateScheduleToAction(scheduleToActionUpdate, securityContext);
  }

  @Operation(
      summary = "getAllScheduleToActions",
      description = "Gets All ScheduleToActions Filtered")
  @PostMapping("getAllScheduleToActions")
  public PaginationResponse<ScheduleToAction> getAllScheduleToActions(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScheduleToActionFilter scheduleToActionFilter,
      @RequestAttribute SecurityContextBase securityContext) {
    scheduleToActionService.validate(scheduleToActionFilter, securityContext);
    return scheduleToActionService.getAllScheduleToActions(scheduleToActionFilter, securityContext);
  }
}
