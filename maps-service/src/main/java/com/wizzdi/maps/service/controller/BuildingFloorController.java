package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.BuildingFloor;
import com.wizzdi.maps.service.request.BuildingFloorCreate;
import com.wizzdi.maps.service.request.BuildingFloorFilter;
import com.wizzdi.maps.service.request.BuildingFloorUpdate;
import com.wizzdi.maps.service.service.BuildingFloorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("BuildingFloor")
@Tag(name = "BuildingFloor")
@OperationsInside
@Extension
public class BuildingFloorController implements Plugin {

  @Autowired private BuildingFloorService buildingFloorService;

  @PutMapping("updateBuildingFloor")
  @Operation(summary = "updateBuildingFloor", description = "Updates BuildingFloor")
  public BuildingFloor updateBuildingFloor(
      @Validated(Update.class) @RequestBody BuildingFloorUpdate buildingFloorUpdate,
      @RequestAttribute SecurityContextBase securityContext) {

    return buildingFloorService.updateBuildingFloor(buildingFloorUpdate, securityContext);
  }

  @PostMapping("getAllBuildingFloors")
  @Operation(summary = "getAllBuildingFloors", description = "lists BuildingFloors")
  public PaginationResponse<BuildingFloor> getAllBuildingFloors(
      @Valid @RequestBody BuildingFloorFilter buildingFloorFilter,
      @RequestAttribute SecurityContextBase securityContext) {

    return buildingFloorService.getAllBuildingFloors(buildingFloorFilter, securityContext);
  }

  @PostMapping("createBuildingFloor")
  @Operation(summary = "createBuildingFloor", description = "Creates BuildingFloor")
  public BuildingFloor createBuildingFloor(
      @Validated(Create.class) @RequestBody BuildingFloorCreate buildingFloorCreate,
      @RequestAttribute SecurityContextBase securityContext) {

    return buildingFloorService.createBuildingFloor(buildingFloorCreate, securityContext);
  }
}
