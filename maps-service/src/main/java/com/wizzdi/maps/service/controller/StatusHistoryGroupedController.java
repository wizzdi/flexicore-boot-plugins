package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.service.request.StatusHistoryGroupedRequest;
import com.wizzdi.maps.service.response.StatusHistoryGroupedEntry;
import com.wizzdi.maps.service.response.StatusHistoryGroupedResponse;
import com.wizzdi.maps.service.service.StatusHistoryGroupedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("StatusHistoryGrouped")
@Extension
@Tag(name = "StatusHistoryGrouped")
@OperationsInside
public class StatusHistoryGroupedController implements Plugin, Invoker {

  @Autowired private StatusHistoryGroupedService statusHistoryGroupedService;

  @PostMapping("listAllStatusHistoriesGrouped")
  @Operation(summary = "listAllStatusHistoriesGrouped", description = "lists StatusHistories grouped")
  public PaginationResponse<StatusHistoryGroupedEntry> listAllStatusHistoriesGrouped(
      
      @RequestBody StatusHistoryGroupedRequest statusHistoryGroupedRequest,
      @RequestAttribute SecurityContext securityContext) {

    statusHistoryGroupedService.validate(statusHistoryGroupedRequest, securityContext);
    StatusHistoryGroupedResponse statusHistoryGroupedResponse = statusHistoryGroupedService.listAllStatusHistoriesGrouped(statusHistoryGroupedRequest, securityContext);
    List<StatusHistoryGroupedEntry> statusHistoryGroupedEntries = statusHistoryGroupedResponse.getStatusHistoryGroupedEntries();
    return new PaginationResponse<>(statusHistoryGroupedEntries,statusHistoryGroupedEntries.size(),statusHistoryGroupedEntries.size());
  }


  @Override
  public Class<?> getHandlingClass() {
    return StatusHistoryGroupedEntry.class;
  }
}
