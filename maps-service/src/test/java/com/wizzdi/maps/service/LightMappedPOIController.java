package com.wizzdi.maps.service;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.request.GeoHashRequest;
import com.wizzdi.maps.service.request.MapFilterComponentRequest;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.response.GeoHashResponse;
import com.wizzdi.maps.service.response.MapFilterComponent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("LightMappedPOIController")
@Extension
@Tag(name = "LightMappedPOIController")
@OperationsInside
public class LightMappedPOIController implements Plugin {
    @Autowired
    private LightMappedPOIService lightMappedPOIService;

    @Operation(summary = "getAllMapFilterComponents", description = "Gets All MapFilterComponents related")
    @PostMapping("getAllMapFilterComponents")
    public PaginationResponse<MapFilterComponent> getAllMapFilterComponents(
            @RequestHeader("authenticationKey") String authenticationKey,
            @RequestBody LightMapFilterComponentRequest mapFilterComponentRequest,
            @RequestAttribute SecurityContextBase securityContext) {
        lightMappedPOIService.validate(mapFilterComponentRequest, securityContext);
        return lightMappedPOIService.getAllMapFilterComponents(mapFilterComponentRequest, securityContext);
    }

    @Operation(summary = "getAllGeoHashAreas", description = "getAllGeoHashAreas")
    @PostMapping("getAllGeoHashAreas")
    public PaginationResponse<GeoHashResponse> getAllGeoHashAreas(
            @RequestHeader("authenticationKey") String authenticationKey,
            @RequestBody ExtendedGeoHashRequest geoHashRequest,
            @RequestAttribute SecurityContextBase securityContext) {
        lightMappedPOIService.validate(geoHashRequest, securityContext);
        return lightMappedPOIService.getAllGeoHashAreas(geoHashRequest, securityContext);
    }

    @Operation(summary = "getAllMappedPOIs", description = "Gets All MappedPOIs Filtered")
    @PostMapping("getAllMappedPOIs")
    public PaginationResponse<MappedPOI> getAllMappedPOIs(
            @RequestHeader("authenticationKey") String authenticationKey,
            @RequestBody ExtendedMappedPOIFilter mappedPOIFilter,
            @RequestAttribute SecurityContextBase securityContext) {
        lightMappedPOIService.validate(mappedPOIFilter, securityContext);
        return lightMappedPOIService.getAllMappedPOIs(mappedPOIFilter, securityContext);
    }
}
