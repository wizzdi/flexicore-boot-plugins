package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.LayerType;
import com.wizzdi.maps.service.request.LayerTypeCreate;
import com.wizzdi.maps.service.request.LayerTypeFilter;
import com.wizzdi.maps.service.request.LayerTypeUpdate;
import com.wizzdi.maps.service.service.LayerTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("LayerType")
@Tag(name = "LayerType")
@OperationsInside
@Extension
public class LayerTypeController implements Plugin {

  @Autowired private LayerTypeService layerTypeService;

  @PutMapping("updateLayerType")
  @Operation(summary = "updateLayerType", description = "Updates LayerType")
  public LayerType updateLayerType(
      @Validated(Update.class) @RequestBody LayerTypeUpdate layerTypeUpdate,
      @RequestAttribute SecurityContext securityContext) {

    return layerTypeService.updateLayerType(layerTypeUpdate, securityContext);
  }

  @PostMapping("getAllLayerTypes")
  @Operation(summary = "getAllLayerTypes", description = "lists LayerTypes")
  public PaginationResponse<LayerType> getAllLayerTypes(
      @Valid @RequestBody LayerTypeFilter layerTypeFilter,
      @RequestAttribute SecurityContext securityContext) {

    return layerTypeService.getAllLayerTypes(layerTypeFilter, securityContext);
  }

  @PostMapping("createLayerType")
  @Operation(summary = "createLayerType", description = "Creates LayerType")
  public LayerType createLayerType(
      @Validated(Create.class) @RequestBody LayerTypeCreate layerTypeCreate,
      @RequestAttribute SecurityContext securityContext) {

    return layerTypeService.createLayerType(layerTypeCreate, securityContext);
  }
}
