package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.request.StatusHistoryForDateRequest;
import com.wizzdi.maps.service.service.StatusHistoryGroupedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("StatusHistoryGrouped")
@Extension
@Tag(name = "StatusHistoryGrouped")
@OperationsInside
public class StatusHistoryForDateController implements Plugin, Invoker {

  @Autowired private StatusHistoryGroupedService statusHistoryGroupedService;



  @PostMapping("getAllStatusHistoriesForDate")
  @Operation(summary = "getAllStatusHistoriesForDate", description = "lists StatusHistories for Date")
  public PaginationResponse<StatusHistory> getAllStatusHistoriesForDate(
          @Valid @RequestBody StatusHistoryForDateRequest statusHistoryFilter,
          @RequestAttribute SecurityContextBase securityContext) {

    return statusHistoryGroupedService.getAllStatusHistoriesForDate(statusHistoryFilter, securityContext);
  }

  @Override
  public Class<?> getHandlingClass() {
    return StatusHistory.class;
  }
}
