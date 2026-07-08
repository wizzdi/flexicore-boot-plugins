package com.wizzdi.building.studio.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.building.studio.model.*;
import com.wizzdi.building.studio.service.request.*;
import com.wizzdi.building.studio.service.response.BuildingStudioConversionResponse;
import com.wizzdi.building.studio.service.service.BuildingStudioService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("plugins/BuildingStudio")
@Tag(name = "BuildingStudio")
@OperationsInside
@Extension
public class BuildingStudioController implements Plugin {

    @Autowired
    private BuildingStudioService buildingStudioService;

    @PostMapping("getAllBuildingBundles")
    @Operation(summary = "getAllBuildingBundles", description = "Lists BuildingBundle records")
    public PaginationResponse<BuildingBundle> getAllBuildingBundles(@Valid @RequestBody BuildingBundleFilter filter,
                                                                    @RequestAttribute SecurityContext securityContext) {
        buildingStudioService.validate(filter, securityContext);
        return buildingStudioService.getAllBuildingBundles(filter, securityContext);
    }

    @PostMapping("createBuildingBundle")
    @Operation(summary = "createBuildingBundle", description = "Creates a BuildingBundle container for DWG/SVG/GLB/PNG resources")
    public BuildingBundle createBuildingBundle(@Validated(Create.class) @RequestBody BuildingBundleCreate request,
                                               @RequestAttribute SecurityContext securityContext) {
        return buildingStudioService.createBuildingBundle(request, securityContext);
    }

    @PutMapping("updateBuildingBundle")
    @Operation(summary = "updateBuildingBundle", description = "Updates a BuildingBundle")
    public BuildingBundle updateBuildingBundle(@Validated(Update.class) @RequestBody BuildingBundleUpdate request,
                                               @RequestAttribute SecurityContext securityContext) {
        return buildingStudioService.updateBuildingBundle(request, securityContext);
    }

    @PostMapping("registerDwg")
    @Operation(summary = "registerDwg", description = "Links an uploaded DWG/DXF FileResource into a BuildingBundle")
    public BuildingDwg registerDwg(@Validated(Create.class) @RequestBody BuildingDwgCreate request,
                                   @RequestAttribute SecurityContext securityContext) {
        return buildingStudioService.registerDwg(request, securityContext);
    }

    @PostMapping("registerSvg")
    @Operation(summary = "registerSvg", description = "Registers an SVG output FileResource under a BuildingBundle")
    public BuildingSvg registerSvg(@Validated(Create.class) @RequestBody BuildingSvgCreate request,
                                   @RequestAttribute SecurityContext securityContext) {
        return buildingStudioService.registerSvg(request, securityContext);
    }

    @PostMapping("registerGlb")
    @Operation(summary = "registerGlb", description = "Registers a GLB output FileResource under a BuildingBundle")
    public BuildingGlb registerGlb(@Validated(Create.class) @RequestBody BuildingGlbCreate request,
                                   @RequestAttribute SecurityContext securityContext) {
        return buildingStudioService.registerGlb(request, securityContext);
    }

    @PostMapping("runConversion")
    @Operation(summary = "runConversion", description = "Runs the configured DWG→DXF→SVG/GLB conversion process and stores output FileResources")
    public BuildingStudioConversionResponse runConversion(@Valid @RequestBody BuildingStudioConversionRequest request,
                                                          @RequestAttribute SecurityContext securityContext) {
        return buildingStudioService.runConversion(request, securityContext);
    }

    @PostMapping("linkFloorResource")
    @Operation(summary = "linkFloorResource", description = "Links a BuildingBundle file to a BuildingFloor for future walkthrough/floor resources")
    public BuildingFloorResource linkFloorResource(@Validated(Create.class) @RequestBody BuildingFloorResourceCreate request,
                                                   @RequestAttribute SecurityContext securityContext) {
        return buildingStudioService.linkFloorResource(request, securityContext);
    }
}
