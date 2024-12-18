package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.LocationHistory;
import com.wizzdi.maps.service.request.LocationHistoryCreate;
import com.wizzdi.maps.service.request.LocationHistoryFilter;
import com.wizzdi.maps.service.request.LocationHistoryUpdate;
import com.wizzdi.maps.service.service.LocationHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("LocationHistory")
@Tag(name = "LocationHistory")
@OperationsInside
@Extension
public class LocationHistoryController implements Plugin {

  @Autowired private LocationHistoryService locationHistoryService;

  @PutMapping("updateLocationHistory")
  @Operation(summary = "updateLocationHistory", description = "Updates LocationHistory")
  public LocationHistory updateLocationHistory(
      @Validated(Update.class) @RequestBody LocationHistoryUpdate locationHistoryUpdate,
      @RequestAttribute SecurityContext securityContext) {

    return locationHistoryService.updateLocationHistory(locationHistoryUpdate, securityContext);
  }

  @PostMapping("getAllLocationHistories")
  @Operation(summary = "getAllLocationHistories", description = "lists LocationHistories")
  public PaginationResponse<LocationHistory> getAllLocationHistories(
      @Valid @RequestBody LocationHistoryFilter locationHistoryFilter,
      @RequestAttribute SecurityContext securityContext) {

    return locationHistoryService.getAllLocationHistories(locationHistoryFilter, securityContext);
  }

  @PostMapping("createLocationHistory")
  @Operation(summary = "createLocationHistory", description = "Creates LocationHistory")
  public LocationHistory createLocationHistory(
      @Validated(Create.class) @RequestBody LocationHistoryCreate locationHistoryCreate,
      @RequestAttribute SecurityContext securityContext) {

    return locationHistoryService.createLocationHistory(locationHistoryCreate, securityContext);
  }
}
