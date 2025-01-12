package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.model.ScenarioTrigger_;
import com.flexicore.rules.request.ScenarioTriggerCreate;
import com.flexicore.rules.request.ScenarioTriggerFilter;
import com.flexicore.rules.request.ScenarioTriggerUpdate;
import com.flexicore.rules.service.ScenarioTriggerService;
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
@RequestMapping("ScenarioTrigger")
@Extension
@Tag(name = "ScenarioTrigger")
@OperationsInside
public class ScenarioTriggerController implements Plugin {

  @Autowired private ScenarioTriggerService scenarioTriggerService;

  @PostMapping("createScenarioTrigger")
  @Operation(summary = "createScenarioTrigger", description = "Creates ScenarioTrigger")
  public ScenarioTrigger createScenarioTrigger(
      
      @RequestBody ScenarioTriggerCreate scenarioTriggerCreate,
      @RequestAttribute SecurityContext securityContext) {

    scenarioTriggerService.validate(scenarioTriggerCreate, securityContext);
    return scenarioTriggerService.createScenarioTrigger(scenarioTriggerCreate, securityContext);
  }

  @PostMapping("getAllScenarioTriggers")
  @Operation(summary = "getAllScenarioTriggers", description = "lists ScenarioTriggers")
  public PaginationResponse<ScenarioTrigger> getAllScenarioTriggers(
      
      @RequestBody ScenarioTriggerFilter scenarioTriggerFilter,
      @RequestAttribute SecurityContext securityContext) {

    scenarioTriggerService.validate(scenarioTriggerFilter, securityContext);
    return scenarioTriggerService.getAllScenarioTriggers(scenarioTriggerFilter, securityContext);
  }

  @PutMapping("updateScenarioTrigger")
  @Operation(summary = "updateScenarioTrigger", description = "Updates ScenarioTrigger")
  public ScenarioTrigger updateScenarioTrigger(
      
      @RequestBody ScenarioTriggerUpdate scenarioTriggerUpdate,
      @RequestAttribute SecurityContext securityContext) {

    String scenarioTriggerId = scenarioTriggerUpdate.getId();
    ScenarioTrigger scenarioTrigger =
        scenarioTriggerService.getByIdOrNull(
            scenarioTriggerId, ScenarioTrigger.class,  securityContext);
    if (scenarioTrigger == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioTrigger with id " + scenarioTriggerId);
    }
    scenarioTriggerUpdate.setScenarioTrigger(scenarioTrigger);
    scenarioTriggerService.validate(scenarioTriggerUpdate, securityContext);
    return scenarioTriggerService.updateScenarioTrigger(scenarioTriggerUpdate, securityContext);
  }
}
