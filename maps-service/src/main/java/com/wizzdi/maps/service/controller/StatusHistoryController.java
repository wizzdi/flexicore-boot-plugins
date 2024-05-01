package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.request.StatusHistoryCreate;
import com.wizzdi.maps.service.request.StatusHistoryFilter;
import com.wizzdi.maps.service.request.StatusHistoryUpdate;
import com.wizzdi.maps.service.response.StatusHistoryContainer;
import com.wizzdi.maps.service.service.StatusHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("StatusHistory")
@Tag(name = "StatusHistory")
@OperationsInside
@Extension
public class StatusHistoryController implements Plugin {

  @Autowired private StatusHistoryService statusHistoryService;

  @PostMapping("getAllStatusHistories")
  @Operation(summary = "getAllStatusHistories", description = "lists StatusHistories")
  public PaginationResponse<StatusHistory> getAllStatusHistories(
      @Valid @RequestBody StatusHistoryFilter statusHistoryFilter,
      @RequestAttribute SecurityContextBase securityContext) {

    return statusHistoryService.getAllStatusHistories(statusHistoryFilter, securityContext);
  }


  @PostMapping("getAllStatusHistoryContainers")
  @Operation(summary = "getAllStatusHistoryContainers", description = "lists StatusHistoryContainers")
  public PaginationResponse<StatusHistoryContainer> getAllStatusHistoryContainers(
          @Valid @RequestBody StatusHistoryFilter statusHistoryFilter,
          @RequestAttribute SecurityContextBase securityContext) {

    return statusHistoryService.getAllStatusHistoryContainers(statusHistoryFilter, securityContext);
  }



  @PutMapping("updateStatusHistory")
  @Operation(summary = "updateStatusHistory", description = "Updates StatusHistory")
  public StatusHistory updateStatusHistory(
      @Validated(Update.class) @RequestBody StatusHistoryUpdate statusHistoryUpdate,
      @RequestAttribute SecurityContextBase securityContext) {

    return statusHistoryService.updateStatusHistory(statusHistoryUpdate, securityContext);
  }

  @PostMapping("createStatusHistory")
  @Operation(summary = "createStatusHistory", description = "Creates StatusHistory")
  public StatusHistory createStatusHistory(
      @Validated(Create.class) @RequestBody StatusHistoryCreate statusHistoryCreate,
      @RequestAttribute SecurityContextBase securityContext) {

    return statusHistoryService.createStatusHistory(statusHistoryCreate, securityContext);
  }
}
