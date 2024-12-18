package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.ScenarioToAction;
import com.flexicore.rules.model.ScenarioToAction_;
import com.flexicore.rules.request.ScenarioToActionCreate;
import com.flexicore.rules.request.ScenarioToActionFilter;
import com.flexicore.rules.request.ScenarioToActionUpdate;
import com.flexicore.rules.service.ScenarioToActionService;
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
@RequestMapping("ScenarioToAction")
@Extension
@Tag(name = "ScenarioToAction")
@OperationsInside
public class ScenarioToActionController implements Plugin {

  @Autowired private ScenarioToActionService scenarioToActionService;

  @PostMapping("createScenarioToAction")
  @Operation(summary = "createScenarioToAction", description = "Creates ScenarioToAction")
  public ScenarioToAction createScenarioToAction(
      
      @RequestBody ScenarioToActionCreate scenarioToActionCreate,
      @RequestAttribute SecurityContext securityContext) {

    scenarioToActionService.validate(scenarioToActionCreate, securityContext);
    return scenarioToActionService.createScenarioToAction(scenarioToActionCreate, securityContext);
  }

  @PutMapping("updateScenarioToAction")
  @Operation(summary = "updateScenarioToAction", description = "Updates ScenarioToAction")
  public ScenarioToAction updateScenarioToAction(
      
      @RequestBody ScenarioToActionUpdate scenarioToActionUpdate,
      @RequestAttribute SecurityContext securityContext) {

    String scenarioToActionId = scenarioToActionUpdate.getId();
    ScenarioToAction scenarioToAction =
        scenarioToActionService.getByIdOrNull(
            scenarioToActionId,
            ScenarioToAction.class,
            securityContext);
    if (scenarioToAction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioToAction with id " + scenarioToActionId);
    }
    scenarioToActionUpdate.setScenarioToAction(scenarioToAction);
    scenarioToActionService.validate(scenarioToActionUpdate, securityContext);
    return scenarioToActionService.updateScenarioToAction(scenarioToActionUpdate, securityContext);
  }

  @PostMapping("getAllScenarioToActions")
  @Operation(summary = "getAllScenarioToActions", description = "lists ScenarioToActions")
  public PaginationResponse<ScenarioToAction> getAllScenarioToActions(
      
      @RequestBody ScenarioToActionFilter scenarioToActionFilter,
      @RequestAttribute SecurityContext securityContext) {

    scenarioToActionService.validate(scenarioToActionFilter, securityContext);
    return scenarioToActionService.getAllScenarioToActions(scenarioToActionFilter, securityContext);
  }
}
