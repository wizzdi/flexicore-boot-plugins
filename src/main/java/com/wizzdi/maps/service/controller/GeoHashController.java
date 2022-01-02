package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.service.request.GeoHashRequest;
import com.wizzdi.maps.service.response.GeoHashResponse;
import com.wizzdi.maps.service.service.GeoHashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("GeoHash")
@Extension
@Tag(name = "GeoHash")
@OperationsInside
public class GeoHashController implements Plugin {

    @Autowired
    private GeoHashService geoHashService;


    @Operation(summary = "getAllGeoHashAreas", description = "getAllGeoHashAreas")
    @PostMapping("getAllGeoHashAreas")
    public PaginationResponse<GeoHashResponse> getAllGeoHashAreas(
            @RequestHeader("authenticationKey") String authenticationKey,
            @RequestBody GeoHashRequest geoHashRequest,
            @RequestAttribute SecurityContextBase securityContext) {
        geoHashService.validate(geoHashRequest, securityContext);
        return geoHashService.getAllGeoHashAreas(geoHashRequest, securityContext);
    }
}
