package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.ScenarioToTrigger;
import com.flexicore.rules.model.ScenarioToTrigger_;
import com.flexicore.rules.request.ScenarioToTriggerCreate;
import com.flexicore.rules.request.ScenarioToTriggerFilter;
import com.flexicore.rules.request.ScenarioToTriggerUpdate;
import com.flexicore.rules.service.ScenarioToTriggerService;
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
@RequestMapping("ScenarioToTrigger")
@Extension
@Tag(name = "ScenarioToTrigger")
@OperationsInside
public class ScenarioToTriggerController implements Plugin {

  @Autowired private ScenarioToTriggerService scenarioToTriggerService;

  @PostMapping("getAllScenarioToTriggers")
  @Operation(summary = "getAllScenarioToTriggers", description = "lists ScenarioToTriggers")
  public PaginationResponse<ScenarioToTrigger> getAllScenarioToTriggers(
      
      @RequestBody ScenarioToTriggerFilter scenarioToTriggerFilter,
      @RequestAttribute SecurityContext securityContext) {

    scenarioToTriggerService.validate(scenarioToTriggerFilter, securityContext);
    return scenarioToTriggerService.getAllScenarioToTriggers(
        scenarioToTriggerFilter, securityContext);
  }

  @PostMapping("createScenarioToTrigger")
  @Operation(summary = "createScenarioToTrigger", description = "Creates ScenarioToTrigger")
  public ScenarioToTrigger createScenarioToTrigger(
      
      @RequestBody ScenarioToTriggerCreate scenarioToTriggerCreate,
      @RequestAttribute SecurityContext securityContext) {

    scenarioToTriggerService.validate(scenarioToTriggerCreate, securityContext);
    return scenarioToTriggerService.createScenarioToTrigger(
        scenarioToTriggerCreate, securityContext);
  }

  @PutMapping("updateScenarioToTrigger")
  @Operation(summary = "updateScenarioToTrigger", description = "Updates ScenarioToTrigger")
  public ScenarioToTrigger updateScenarioToTrigger(
      
      @RequestBody ScenarioToTriggerUpdate scenarioToTriggerUpdate,
      @RequestAttribute SecurityContext securityContext) {

    String scenarioToTriggerId = scenarioToTriggerUpdate.getId();
    ScenarioToTrigger scenarioToTrigger =
        scenarioToTriggerService.getByIdOrNull(
            scenarioToTriggerId,
            ScenarioToTrigger.class,
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
}
