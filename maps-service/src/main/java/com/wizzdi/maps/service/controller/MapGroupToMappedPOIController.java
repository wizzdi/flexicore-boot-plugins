package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.MapGroupToMappedPOI;
import com.wizzdi.maps.service.request.MapGroupToMappedPOICreate;
import com.wizzdi.maps.service.request.MapGroupToMappedPOIFilter;
import com.wizzdi.maps.service.request.MapGroupToMappedPOIUpdate;
import com.wizzdi.maps.service.service.MapGroupToMappedPOIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("MapGroupToMappedPOI")
@Tag(name = "MapGroupToMappedPOI")
@OperationsInside
@Extension
public class MapGroupToMappedPOIController implements Plugin {

  @Autowired private MapGroupToMappedPOIService mapGroupToMappedPOIService;

  @PostMapping("getAllMapGroupToMappedPOIs")
  @Operation(summary = "getAllMapGroupToMappedPOIs", description = "lists MapGroupToMappedPOIs")
  public PaginationResponse<MapGroupToMappedPOI> getAllMapGroupToMappedPOIs(
      @Valid @RequestBody MapGroupToMappedPOIFilter mapGroupToMappedPOIFilter,
      @RequestAttribute SecurityContextBase securityContext) {

    return mapGroupToMappedPOIService.getAllMapGroupToMappedPOIs(
        mapGroupToMappedPOIFilter, securityContext);
  }

  @PutMapping("updateMapGroupToMappedPOI")
  @Operation(summary = "updateMapGroupToMappedPOI", description = "Updates MapGroupToMappedPOI")
  public MapGroupToMappedPOI updateMapGroupToMappedPOI(
      @Validated(Update.class) @RequestBody MapGroupToMappedPOIUpdate mapGroupToMappedPOIUpdate,
      @RequestAttribute SecurityContextBase securityContext) {

    return mapGroupToMappedPOIService.updateMapGroupToMappedPOI(
        mapGroupToMappedPOIUpdate, securityContext);
  }

  @PostMapping("createMapGroupToMappedPOI")
  @Operation(summary = "createMapGroupToMappedPOI", description = "Creates MapGroupToMappedPOI")
  public MapGroupToMappedPOI createMapGroupToMappedPOI(
      @Validated(Create.class) @RequestBody MapGroupToMappedPOICreate mapGroupToMappedPOICreate,
      @RequestAttribute SecurityContextBase securityContext) {

    return mapGroupToMappedPOIService.createMapGroupToMappedPOI(
        mapGroupToMappedPOICreate, securityContext);
  }
}
