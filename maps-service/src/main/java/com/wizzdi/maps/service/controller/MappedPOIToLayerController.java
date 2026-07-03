package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.MappedPOIToLayer;
import com.wizzdi.maps.service.request.MappedPOIToLayerCreate;
import com.wizzdi.maps.service.request.MappedPOIToLayerFilter;
import com.wizzdi.maps.service.request.MappedPOIToLayerUpdate;
import com.wizzdi.maps.service.service.MappedPOIToLayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("MappedPOIToLayer")
@Tag(name = "MappedPOIToLayer")
@OperationsInside
@Extension
public class MappedPOIToLayerController implements Plugin {

    @Autowired
    private MappedPOIToLayerService mappedPOIToLayerService;

    @PostMapping("createMappedPOIToLayer")
    @Operation(summary = "createMappedPOIToLayer", description = "Creates MappedPOIToLayer")
    public MappedPOIToLayer createMappedPOIToLayer(
            @Validated(Create.class) @RequestBody MappedPOIToLayerCreate create,
            @RequestAttribute SecurityContext securityContext) {
        mappedPOIToLayerService.validate(create, securityContext);
        return mappedPOIToLayerService.createMappedPOIToLayer(create, securityContext);
    }

    @PutMapping("updateMappedPOIToLayer")
    @Operation(summary = "updateMappedPOIToLayer", description = "Updates MappedPOIToLayer")
    public MappedPOIToLayer updateMappedPOIToLayer(
            @Validated(Update.class) @RequestBody MappedPOIToLayerUpdate update,
            @RequestAttribute SecurityContext securityContext) {
        mappedPOIToLayerService.validate(update, securityContext);
        return mappedPOIToLayerService.updateMappedPOIToLayer(update, securityContext);
    }

    @PostMapping("getAllMappedPOIToLayers")
    @Operation(summary = "getAllMappedPOIToLayers", description = "Gets All MappedPOIToLayers Filtered")
    public PaginationResponse<MappedPOIToLayer> getAllMappedPOIToLayers(
            @Valid @RequestBody MappedPOIToLayerFilter filter,
            @RequestAttribute SecurityContext securityContext) {
        mappedPOIToLayerService.validate(filter, securityContext);
        return mappedPOIToLayerService.getAllMappedPOIToLayers(filter, securityContext);
    }
}
