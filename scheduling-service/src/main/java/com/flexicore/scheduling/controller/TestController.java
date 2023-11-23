package com.flexicore.scheduling.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.Schedule_;
import com.flexicore.scheduling.request.NullActionBody;
import com.flexicore.scheduling.request.ScheduleCreate;
import com.flexicore.scheduling.request.ScheduleFilter;
import com.flexicore.scheduling.request.ScheduleUpdate;
import com.flexicore.scheduling.response.ActionResult;
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
public class TestController implements Plugin, Invoker {

  @Autowired private ScheduleService scheduleService;



  @PostMapping("nullAction")
  @Operation(summary = "null action for testing purposes of schedules")
  public ActionResult sendNullAction(

          @RequestBody NullActionBody nullActionBody,
          @RequestAttribute SecurityContextBase securityContext) {

    return scheduleService.fireNullActionWithResult(nullActionBody, securityContext);
  }


  @Override
  public Class<?> getHandlingClass() {
    return ActionResult.class;
  }
}
