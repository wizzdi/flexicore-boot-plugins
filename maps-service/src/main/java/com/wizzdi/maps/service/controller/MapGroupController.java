package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.MapGroup;
import com.wizzdi.maps.service.request.MapGroupCreate;
import com.wizzdi.maps.service.request.MapGroupFilter;
import com.wizzdi.maps.service.request.MapGroupUpdate;
import com.wizzdi.maps.service.service.MapGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("MapGroup")
@Tag(name = "MapGroup")
@OperationsInside
@Extension
public class MapGroupController implements Plugin {

  @Autowired private MapGroupService mapGroupService;

  @PutMapping("updateMapGroup")
  @Operation(summary = "updateMapGroup", description = "Updates MapGroup")
  public MapGroup updateMapGroup(
      @Validated(Update.class) @RequestBody MapGroupUpdate mapGroupUpdate,
      @RequestAttribute SecurityContext securityContext) {

    return mapGroupService.updateMapGroup(mapGroupUpdate, securityContext);
  }

  @PostMapping("getAllMapGroups")
  @Operation(summary = "getAllMapGroups", description = "lists MapGroups")
  public PaginationResponse<MapGroup> getAllMapGroups(
      @Valid @RequestBody MapGroupFilter mapGroupFilter,
      @RequestAttribute SecurityContext securityContext) {

    return mapGroupService.getAllMapGroups(mapGroupFilter, securityContext);
  }

  @PostMapping("createMapGroup")
  @Operation(summary = "createMapGroup", description = "Creates MapGroup")
  public MapGroup createMapGroup(
      @Validated(Create.class) @RequestBody MapGroupCreate mapGroupCreate,
      @RequestAttribute SecurityContext securityContext) {

    return mapGroupService.createMapGroup(mapGroupCreate, securityContext);
  }
}
