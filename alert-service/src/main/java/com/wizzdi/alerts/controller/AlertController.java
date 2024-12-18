package com.wizzdi.alerts.controller;

import com.wizzdi.alerts.Alert;
import com.wizzdi.alerts.request.AlertCreate;
import com.wizzdi.alerts.request.AlertFilter;
import com.wizzdi.alerts.request.AlertUpdate;
import com.wizzdi.alerts.service.AlertService;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("Alert")
@Tag(name = "Alert")
@OperationsInside
@Extension
public class AlertController implements Plugin {

  @Autowired private AlertService alertService;

  @PostMapping("createAlert")
  @Operation(summary = "createAlert", description = "Creates Alert")
  public Alert createAlert(
      @Validated(Create.class) @RequestBody AlertCreate alertCreate,
      @RequestAttribute SecurityContext securityContext) {

    return alertService.createAlert(alertCreate, securityContext);
  }

  @PostMapping("getAllAlerts")
  @Operation(summary = "getAllAlerts", description = "lists Alerts")
  public PaginationResponse<Alert> getAllAlerts(
      @Valid @RequestBody AlertFilter alertFilter,
      @RequestAttribute SecurityContext securityContext) {

    return alertService.getAllAlerts(alertFilter, securityContext);
  }

  @PutMapping("updateAlert")
  @Operation(summary = "updateAlert", description = "Updates Alert")
  public Alert updateAlert(
      @Validated(Update.class) @RequestBody AlertUpdate alertUpdate,
      @RequestAttribute SecurityContext securityContext) {

    return alertService.updateAlert(alertUpdate, securityContext);
  }
}
