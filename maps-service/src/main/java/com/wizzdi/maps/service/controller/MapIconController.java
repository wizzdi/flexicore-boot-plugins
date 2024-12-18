package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.service.request.MapIconCreate;
import com.wizzdi.maps.service.request.MapIconFilter;
import com.wizzdi.maps.service.request.MapIconUpdate;
import com.wizzdi.maps.service.service.MapIconService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("MapIcon")
@Tag(name = "MapIcon")
@OperationsInside
@Extension
public class MapIconController implements Plugin {

  @Autowired private MapIconService mapIconService;

  @PutMapping("updateMapIcon")
  @Operation(summary = "updateMapIcon", description = "Updates MapIcon")
  public MapIcon updateMapIcon(
      @Validated(Update.class) @RequestBody MapIconUpdate mapIconUpdate,
      @RequestAttribute SecurityContext securityContext) {

    return mapIconService.updateMapIcon(mapIconUpdate, securityContext);
  }

  @PostMapping("getAllMapIcons")
  @Operation(summary = "getAllMapIcons", description = "lists MapIcons")
  public PaginationResponse<MapIcon> getAllMapIcons(
      @Valid @RequestBody MapIconFilter mapIconFilter,
      @RequestAttribute SecurityContext securityContext) {

    return mapIconService.getAllMapIcons(mapIconFilter, securityContext);
  }

  @PostMapping("createMapIcon")
  @Operation(summary = "createMapIcon", description = "Creates MapIcon")
  public MapIcon createMapIcon(
      @Validated(Create.class) @RequestBody MapIconCreate mapIconCreate,
      @RequestAttribute SecurityContext securityContext) {

    return mapIconService.createMapIcon(mapIconCreate, securityContext);
  }
}
