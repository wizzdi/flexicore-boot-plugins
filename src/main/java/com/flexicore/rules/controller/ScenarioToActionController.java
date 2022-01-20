package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.ScenarioToAction;
import com.flexicore.rules.model.ScenarioToAction_;
import com.flexicore.rules.request.ScenarioToActionCreate;
import com.flexicore.rules.request.ScenarioToActionFilter;
import com.flexicore.rules.request.ScenarioToActionUpdate;
import com.flexicore.rules.service.ScenarioToActionService;
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
@RequestMapping("ScenarioToAction")
@Extension
@Tag(name = "ScenarioToAction")
@OperationsInside
public class ScenarioToActionController implements Plugin {

  @Autowired private ScenarioToActionService scenarioToActionService;

  @Operation(summary = "createScenarioToAction", description = "Creates ScenarioToAction")
  public ScenarioToAction createScenarioToAction(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScenarioToActionCreate scenarioToActionCreate,
      @RequestAttribute SecurityContextBase securityContext) {

    scenarioToActionService.validate(scenarioToActionCreate, securityContext);
    return scenarioToActionService.createScenarioToAction(scenarioToActionCreate, securityContext);
  }

  @Operation(summary = "updateScenarioToAction", description = "Updates ScenarioToAction")
  public ScenarioToAction updateScenarioToAction(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScenarioToActionUpdate scenarioToActionUpdate,
      @RequestAttribute SecurityContextBase securityContext) {

    String scenarioToActionId = scenarioToActionUpdate.getId();
    ScenarioToAction scenarioToAction =
        scenarioToActionService.getByIdOrNull(
            scenarioToActionId,
            ScenarioToAction.class,
            ScenarioToAction_.security,
            securityContext);
    if (scenarioToAction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioToAction with id " + scenarioToActionId);
    }
    scenarioToActionUpdate.setScenarioToAction(scenarioToAction);
    scenarioToActionService.validate(scenarioToActionUpdate, securityContext);
    return scenarioToActionService.updateScenarioToAction(scenarioToActionUpdate, securityContext);
  }

  @Operation(summary = "getAllScenarioToAction", description = "lists ScenarioToAction")
  public PaginationResponse<ScenarioToAction> getAllScenarioToAction(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody ScenarioToActionFilter scenarioToActionFilter,
      @RequestAttribute SecurityContextBase securityContext) {

    scenarioToActionService.validate(scenarioToActionFilter, securityContext);
    return scenarioToActionService.getAllScenarioToActions(scenarioToActionFilter, securityContext);
  }
}
