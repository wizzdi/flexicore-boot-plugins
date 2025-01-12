package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.model.ScenarioAction_;
import com.flexicore.rules.request.ScenarioActionCreate;
import com.flexicore.rules.request.ScenarioActionFilter;
import com.flexicore.rules.request.ScenarioActionUpdate;
import com.flexicore.rules.service.ScenarioActionService;
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
@RequestMapping("ScenarioAction")
@Extension
@Tag(name = "ScenarioAction")
@OperationsInside
public class ScenarioActionController implements Plugin {

  @Autowired private ScenarioActionService scenarioActionService;

  @PostMapping("createScenarioAction")
  @Operation(summary = "createScenarioAction", description = "Creates ScenarioAction")
  public ScenarioAction createScenarioAction(
      
      @RequestBody ScenarioActionCreate scenarioActionCreate,
      @RequestAttribute SecurityContext securityContext) {

    scenarioActionService.validate(scenarioActionCreate, securityContext);
    return scenarioActionService.createScenarioAction(scenarioActionCreate, securityContext);
  }

  @PutMapping("updateScenarioAction")
  @Operation(summary = "updateScenarioAction", description = "Updates ScenarioAction")
  public ScenarioAction updateScenarioAction(
      
      @RequestBody ScenarioActionUpdate scenarioActionUpdate,
      @RequestAttribute SecurityContext securityContext) {

    String scenarioActionId = scenarioActionUpdate.getId();
    ScenarioAction scenarioAction =
        scenarioActionService.getByIdOrNull(
            scenarioActionId, ScenarioAction.class,  securityContext);
    if (scenarioAction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioAction with id " + scenarioActionId);
    }
    scenarioActionUpdate.setScenarioAction(scenarioAction);
    scenarioActionService.validate(scenarioActionUpdate, securityContext);
    return scenarioActionService.updateScenarioAction(scenarioActionUpdate, securityContext);
  }

  @PostMapping("getAllScenarioActions")
  @Operation(summary = "getAllScenarioActions", description = "lists ScenarioActions")
  public PaginationResponse<ScenarioAction> getAllScenarioActions(
      
      @RequestBody ScenarioActionFilter scenarioActionFilter,
      @RequestAttribute SecurityContext securityContext) {

    scenarioActionService.validate(scenarioActionFilter, securityContext);
    return scenarioActionService.getAllScenarioActions(scenarioActionFilter, securityContext);
  }
}
