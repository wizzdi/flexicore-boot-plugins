package com.flexicore.rules.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.rules.model.ScenarioToDataSource;
import com.flexicore.rules.model.ScenarioToDataSource_;
import com.flexicore.rules.request.ScenarioToDataSourceCreate;
import com.flexicore.rules.request.ScenarioToDataSourceFilter;
import com.flexicore.rules.request.ScenarioToDataSourceUpdate;
import com.flexicore.rules.service.ScenarioToDataSourceService;
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
@RequestMapping("ScenarioToDataSource")
@Extension
@Tag(name = "ScenarioToDataSource")
@OperationsInside
public class ScenarioToDataSourceController implements Plugin {

  @Autowired private ScenarioToDataSourceService scenarioToDataSourceService;

  @PostMapping("createScenarioToDataSource")
  @Operation(summary = "createScenarioToDataSource", description = "Creates ScenarioToDataSource")
  public ScenarioToDataSource createScenarioToDataSource(
      
      @RequestBody ScenarioToDataSourceCreate scenarioToDataSourceCreate,
      @RequestAttribute SecurityContext securityContext) {

    scenarioToDataSourceService.validate(scenarioToDataSourceCreate, securityContext);
    return scenarioToDataSourceService.createScenarioToDataSource(
        scenarioToDataSourceCreate, securityContext);
  }

  @PutMapping("updateScenarioToDataSource")
  @Operation(summary = "updateScenarioToDataSource", description = "Updates ScenarioToDataSource")
  public ScenarioToDataSource updateScenarioToDataSource(
      
      @RequestBody ScenarioToDataSourceUpdate scenarioToDataSourceUpdate,
      @RequestAttribute SecurityContext securityContext) {

    String scenarioToDataSourceId = scenarioToDataSourceUpdate.getId();
    ScenarioToDataSource scenarioToDataSource =
        scenarioToDataSourceService.getByIdOrNull(
            scenarioToDataSourceId,
            ScenarioToDataSource.class,
            securityContext);
    if (scenarioToDataSource == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No ScenarioToDataSource with id " + scenarioToDataSourceId);
    }
    scenarioToDataSourceUpdate.setScenarioToDataSource(scenarioToDataSource);
    scenarioToDataSourceService.validate(scenarioToDataSourceUpdate, securityContext);
    return scenarioToDataSourceService.updateScenarioToDataSource(
        scenarioToDataSourceUpdate, securityContext);
  }

  @PostMapping("getAllScenarioToDataSources")
  @Operation(summary = "getAllScenarioToDataSources", description = "lists ScenarioToDataSources")
  public PaginationResponse<ScenarioToDataSource> getAllScenarioToDataSources(
      
      @RequestBody ScenarioToDataSourceFilter scenarioToDataSourceFilter,
      @RequestAttribute SecurityContext securityContext) {

    scenarioToDataSourceService.validate(scenarioToDataSourceFilter, securityContext);
    return scenarioToDataSourceService.getAllScenarioToDataSources(
        scenarioToDataSourceFilter, securityContext);
  }
}
