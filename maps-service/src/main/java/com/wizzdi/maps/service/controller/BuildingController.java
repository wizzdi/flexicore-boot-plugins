package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.service.request.BuildingCreate;
import com.wizzdi.maps.service.request.BuildingFilter;
import com.wizzdi.maps.service.request.BuildingUpdate;
import com.wizzdi.maps.service.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("Building")
@Tag(name = "Building")
@OperationsInside
@Extension
public class BuildingController implements Plugin {

  @Autowired private BuildingService buildingService;

  @PutMapping("updateBuilding")
  @Operation(summary = "updateBuilding", description = "Updates Building")
  public Building updateBuilding(
      @Validated(Update.class) @RequestBody BuildingUpdate buildingUpdate,
      @RequestAttribute SecurityContextBase securityContext) {

    return buildingService.updateBuilding(buildingUpdate, securityContext);
  }

  @PostMapping("getAllBuildings")
  @Operation(summary = "getAllBuildings", description = "lists Buildings")
  public PaginationResponse<Building> getAllBuildings(
      @Valid @RequestBody BuildingFilter buildingFilter,
      @RequestAttribute SecurityContextBase securityContext) {

    return buildingService.getAllBuildings(buildingFilter, securityContext);
  }

  @PostMapping("createBuilding")
  @Operation(summary = "createBuilding", description = "Creates Building")
  public Building createBuilding(
      @Validated(Create.class) @RequestBody BuildingCreate buildingCreate,
      @RequestAttribute SecurityContextBase securityContext) {

    return buildingService.createBuilding(buildingCreate, securityContext);
  }
}
