package com.wizzdi.flexicore.building.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.building.model.Building;
import com.wizzdi.flexicore.building.model.Building_;
import com.wizzdi.flexicore.building.request.BuildingCreate;
import com.wizzdi.flexicore.building.request.BuildingFilter;
import com.wizzdi.flexicore.building.request.BuildingUpdate;
import com.wizzdi.flexicore.building.service.BuildingService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("Building")
@Extension
@Tag(name = "Building")
@OperationsInside
public class BuildingController implements Plugin {

  @Autowired private BuildingService buildingService;

  @PostMapping("createBuilding")
  @Operation(summary = "createBuilding", description = "Creates Building")
  public Building createBuilding(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody BuildingCreate buildingCreate,
      @RequestAttribute SecurityContextBase securityContext) {
    buildingService.validate(buildingCreate, securityContext);
    return buildingService.createBuilding(buildingCreate, securityContext);
  }

  @Operation(summary = "updateBuilding", description = "Updates Building")
  @PutMapping("updateBuilding")
  public Building updateBuilding(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody BuildingUpdate buildingUpdate,
      @RequestAttribute SecurityContextBase securityContext) {
    String buildingId = buildingUpdate.getId();
    Building building =
        buildingService.getByIdOrNull(
            buildingId, Building.class, Building_.security, securityContext);
    if (building == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No Building with id " + buildingId);
    }
    buildingUpdate.setBuilding(building);
    buildingService.validate(buildingUpdate, securityContext);
    return buildingService.updateBuilding(buildingUpdate, securityContext);
  }

  @Operation(summary = "getAllBuildings", description = "Gets All Buildings Filtered")
  @PostMapping("getAllBuildings")
  public PaginationResponse<Building> getAllBuildings(
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody BuildingFilter buildingFilter,
      @RequestAttribute SecurityContextBase securityContext) {
    buildingService.validate(buildingFilter, securityContext);
    return buildingService.getAllBuildings(buildingFilter, securityContext);
  }
}
