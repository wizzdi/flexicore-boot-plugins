package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.Layer;
import com.wizzdi.maps.service.request.LayerCreate;
import com.wizzdi.maps.service.request.LayerFilter;
import com.wizzdi.maps.service.request.LayerUpdate;
import com.wizzdi.maps.service.service.LayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("Layer")
@Tag(name = "Layer")
@OperationsInside
@Extension
public class LayerController implements Plugin {

  @Autowired private LayerService layerService;

  @PutMapping("updateLayer")
  @Operation(summary = "updateLayer", description = "Updates Layer")
  public Layer updateLayer(
      @Validated(Update.class) @RequestBody LayerUpdate layerUpdate,
      @RequestAttribute SecurityContextBase securityContext) {

    return layerService.updateLayer(layerUpdate, securityContext);
  }

  @PostMapping("getAllLayers")
  @Operation(summary = "getAllLayers", description = "lists Layers")
  public PaginationResponse<Layer> getAllLayers(
      @Valid @RequestBody LayerFilter layerFilter,
      @RequestAttribute SecurityContextBase securityContext) {

    return layerService.getAllLayers(layerFilter, securityContext);
  }

  @PostMapping("createLayer")
  @Operation(summary = "createLayer", description = "Creates Layer")
  public Layer createLayer(
      @Validated(Create.class) @RequestBody LayerCreate layerCreate,
      @RequestAttribute SecurityContextBase securityContext) {

    return layerService.createLayer(layerCreate, securityContext);
  }
}
