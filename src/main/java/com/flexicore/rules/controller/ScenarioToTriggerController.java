package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.ScenarioToTrigger;
import com.flexicore.rules.model.ScenarioToTrigger_;
import com.flexicore.rules.request.ScenarioToTriggerCreate;
import com.flexicore.rules.request.ScenarioToTriggerFilter;
import com.flexicore.rules.request.ScenarioToTriggerUpdate;
import com.flexicore.rules.service.ScenarioToTriggerService;
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
@RequestMapping("ScenarioToTrigger")
@Extension
@Tag(name = "ScenarioToTrigger")
@OperationsInside
public class ScenarioToTriggerController implements Plugin {

  @Autowired private ScenarioToTriggerService scenarioToTriggerService;

  @Operation(summary = "createScenarioToTrigger", description = "Creates ScenarioToTrigger")
  public ScenarioToTrigger createScenarioToTrigger(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScenarioToTriggerCreate scenarioToTriggerCreate,
      @RequestAttribute SecurityContextBase securityContext) {

    scenarioToTriggerService.validate(scenarioToTriggerCreate, securityContext);
    return scenarioToTriggerService.createScenarioToTrigger(
        scenarioToTriggerCreate, securityContext);
  }

  @Operation(summary = "updateScenarioToTrigger", description = "Updates ScenarioToTrigger")
  public ScenarioToTrigger updateScenarioToTrigger(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScenarioToTriggerUpdate scenarioToTriggerUpdate,
      @RequestAttribute SecurityContextBase securityContext) {

    String scenarioToTriggerId = scenarioToTriggerUpdate.getId();
    ScenarioToTrigger scenarioToTrigger =
        scenarioToTriggerService.getByIdOrNull(
            scenarioToTriggerId,
            ScenarioToTrigger.class,
            ScenarioToTrigger_.security,
            securityContext);
    if (scenarioToTrigger == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioToTrigger with id " + scenarioToTriggerId);
    }
    scenarioToTriggerUpdate.setScenarioToTrigger(scenarioToTrigger);
    scenarioToTriggerService.validate(scenarioToTriggerUpdate, securityContext);
    return scenarioToTriggerService.updateScenarioToTrigger(
        scenarioToTriggerUpdate, securityContext);
  }

  @Operation(summary = "getAllScenarioToTrigger", description = "lists ScenarioToTrigger")
  public PaginationResponse<ScenarioToTrigger> getAllScenarioToTrigger(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScenarioToTriggerFilter scenarioToTriggerFilter,
      @RequestAttribute SecurityContextBase securityContext) {

    scenarioToTriggerService.validate(scenarioToTriggerFilter, securityContext);
    return scenarioToTriggerService.getAllScenarioToTriggers(
        scenarioToTriggerFilter, securityContext);
  }
}
