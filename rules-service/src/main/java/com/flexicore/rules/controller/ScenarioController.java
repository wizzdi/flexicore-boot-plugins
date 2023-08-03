package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.Scenario_;
import com.flexicore.rules.request.ScenarioCreate;
import com.flexicore.rules.request.ScenarioFilter;
import com.flexicore.rules.request.ScenarioUpdate;
import com.flexicore.rules.service.ScenarioService;
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
@RequestMapping("Scenario")
@Extension
@Tag(name = "Scenario")
@OperationsInside
public class ScenarioController implements Plugin {

  @Autowired private ScenarioService scenarioService;

  @PostMapping("createScenario")
  @Operation(summary = "createScenario", description = "Creates Scenario")
  public Scenario createScenario(
      
      @RequestBody ScenarioCreate scenarioCreate,
      @RequestAttribute SecurityContextBase securityContext) {

    scenarioService.validate(scenarioCreate, securityContext);
    return scenarioService.createScenario(scenarioCreate, securityContext);
  }

  @PutMapping("updateScenario")
  @Operation(summary = "updateScenario", description = "Updates Scenario")
  public Scenario updateScenario(
      
      @RequestBody ScenarioUpdate scenarioUpdate,
      @RequestAttribute SecurityContextBase securityContext) {

    String scenarioId = scenarioUpdate.getId();
    Scenario scenario =
        scenarioService.getByIdOrNull(
            scenarioId, Scenario.class, Scenario_.security, securityContext);
    if (scenario == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Scenario with id " + scenarioId);
    }
    scenarioUpdate.setScenario(scenario);
    scenarioService.validate(scenarioUpdate, securityContext);
    return scenarioService.updateScenario(scenarioUpdate, securityContext);
  }

  @PostMapping("getAllScenarios")
  @Operation(summary = "getAllScenarios", description = "lists Scenarios")
  public PaginationResponse<Scenario> getAllScenarios(
      
      @RequestBody ScenarioFilter scenarioFilter,
      @RequestAttribute SecurityContextBase securityContext) {

    scenarioService.validate(scenarioFilter, securityContext);
    return scenarioService.getAllScenarios(scenarioFilter, securityContext);
  }
}
