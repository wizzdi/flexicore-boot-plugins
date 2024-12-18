package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.model.ScenarioTriggerType_;
import com.flexicore.rules.request.ScenarioTriggerTypeCreate;
import com.flexicore.rules.request.ScenarioTriggerTypeFilter;
import com.flexicore.rules.request.ScenarioTriggerTypeUpdate;
import com.flexicore.rules.service.ScenarioTriggerTypeService;
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
@RequestMapping("ScenarioTriggerType")
@Extension
@Tag(name = "ScenarioTriggerType")
@OperationsInside
public class ScenarioTriggerTypeController implements Plugin {

  @Autowired private ScenarioTriggerTypeService scenarioTriggerTypeService;

  @PostMapping("createScenarioTriggerType")
  @Operation(summary = "createScenarioTriggerType", description = "Creates ScenarioTriggerType")
  public ScenarioTriggerType createScenarioTriggerType(
      
      @RequestBody ScenarioTriggerTypeCreate scenarioTriggerTypeCreate,
      @RequestAttribute SecurityContext securityContext) {

    scenarioTriggerTypeService.validate(scenarioTriggerTypeCreate, securityContext);
    return scenarioTriggerTypeService.createScenarioTriggerType(
        scenarioTriggerTypeCreate, securityContext);
  }

  @PostMapping("getAllScenarioTriggerTypes")
  @Operation(summary = "getAllScenarioTriggerTypes", description = "lists ScenarioTriggerTypes")
  public PaginationResponse<ScenarioTriggerType> getAllScenarioTriggerTypes(
      
      @RequestBody ScenarioTriggerTypeFilter scenarioTriggerTypeFilter,
      @RequestAttribute SecurityContext securityContext) {

    scenarioTriggerTypeService.validate(scenarioTriggerTypeFilter, securityContext);
    return scenarioTriggerTypeService.getAllScenarioTriggerTypes(
        scenarioTriggerTypeFilter, securityContext);
  }

  @PutMapping("updateScenarioTriggerType")
  @Operation(summary = "updateScenarioTriggerType", description = "Updates ScenarioTriggerType")
  public ScenarioTriggerType updateScenarioTriggerType(
      
      @RequestBody ScenarioTriggerTypeUpdate scenarioTriggerTypeUpdate,
      @RequestAttribute SecurityContext securityContext) {

    String scenarioTriggerTypeId = scenarioTriggerTypeUpdate.getId();
    ScenarioTriggerType scenarioTriggerType =
        scenarioTriggerTypeService.getByIdOrNull(
            scenarioTriggerTypeId,
            ScenarioTriggerType.class,
            securityContext);
    if (scenarioTriggerType == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioTriggerType with id " + scenarioTriggerTypeId);
    }
    scenarioTriggerTypeUpdate.setScenarioTriggerType(scenarioTriggerType);
    scenarioTriggerTypeService.validate(scenarioTriggerTypeUpdate, securityContext);
    return scenarioTriggerTypeService.updateScenarioTriggerType(
        scenarioTriggerTypeUpdate, securityContext);
  }
}
