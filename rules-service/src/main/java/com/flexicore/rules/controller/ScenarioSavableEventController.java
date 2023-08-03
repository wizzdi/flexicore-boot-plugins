package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.ScenarioSavableEvent;
import com.flexicore.rules.model.ScenarioSavableEvent_;
import com.flexicore.rules.request.ScenarioSavableEventCreate;
import com.flexicore.rules.request.ScenarioSavableEventFilter;
import com.flexicore.rules.request.ScenarioSavableEventUpdate;
import com.flexicore.rules.service.ScenarioSavableEventService;
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
@RequestMapping("ScenarioSavableEvent")
@Extension
@Tag(name = "ScenarioSavableEvent")
@OperationsInside
public class ScenarioSavableEventController implements Plugin {

  @Autowired private ScenarioSavableEventService scenarioSavableEventService;

  @PostMapping("createScenarioSavableEvent")
  @Operation(summary = "createScenarioSavableEvent", description = "Creates ScenarioSavableEvent")
  public ScenarioSavableEvent createScenarioSavableEvent(
      
      @RequestBody ScenarioSavableEventCreate scenarioSavableEventCreate,
      @RequestAttribute SecurityContextBase securityContext) {

    scenarioSavableEventService.validate(scenarioSavableEventCreate, securityContext);
    return scenarioSavableEventService.createScenarioSavableEvent(
        scenarioSavableEventCreate, securityContext);
  }

  @PutMapping("updateScenarioSavableEvent")
  @Operation(summary = "updateScenarioSavableEvent", description = "Updates ScenarioSavableEvent")
  public ScenarioSavableEvent updateScenarioSavableEvent(
      
      @RequestBody ScenarioSavableEventUpdate scenarioSavableEventUpdate,
      @RequestAttribute SecurityContextBase securityContext) {

    String scenarioSavableEventId = scenarioSavableEventUpdate.getId();
    ScenarioSavableEvent scenarioSavableEvent =
        scenarioSavableEventService.getByIdOrNull(
            scenarioSavableEventId,
            ScenarioSavableEvent.class,
            ScenarioSavableEvent_.security,
            securityContext);
    if (scenarioSavableEvent == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioSavableEvent with id " + scenarioSavableEventId);
    }
    scenarioSavableEventUpdate.setScenarioSavableEvent(scenarioSavableEvent);
    scenarioSavableEventService.validate(scenarioSavableEventUpdate, securityContext);
    return scenarioSavableEventService.updateScenarioSavableEvent(
        scenarioSavableEventUpdate, securityContext);
  }

  @PostMapping("getAllScenarioSavableEvents")
  @Operation(summary = "getAllScenarioSavableEvents", description = "lists ScenarioSavableEvents")
  public PaginationResponse<ScenarioSavableEvent> getAllScenarioSavableEvents(
      
      @RequestBody ScenarioSavableEventFilter scenarioSavableEventFilter,
      @RequestAttribute SecurityContextBase securityContext) {

    scenarioSavableEventService.validate(scenarioSavableEventFilter, securityContext);
    return scenarioSavableEventService.getAllScenarioSavableEvents(
        scenarioSavableEventFilter, securityContext);
  }
}
