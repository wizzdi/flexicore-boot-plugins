package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.service.request.FilterComponentType;
import com.wizzdi.maps.service.request.MapFilterComponentRequest;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.response.MapFilterComponent;
import com.wizzdi.maps.service.service.MapFilterComponentService;
import com.wizzdi.maps.service.service.MappedPOIRelatedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("MapFilterComponent")
@Extension
@Tag(name = "MapFilterComponent")
@OperationsInside
public class MapFilterComponentController implements Plugin {

    @Autowired
    private MapFilterComponentService mappedPOIService;


    @Operation(summary = "getAllMapFilterComponents", description = "Gets All MapFilterComponents related")
    @PostMapping("getAllMapFilterComponents")
    public PaginationResponse<MapFilterComponent> getAllMapFilterComponents(
            @RequestHeader("authenticationKey") String authenticationKey,
            @RequestBody MapFilterComponentRequest mapFilterComponentRequest,
            @RequestAttribute SecurityContextBase securityContext) {
        mappedPOIService.validate(mapFilterComponentRequest, securityContext);
        return mappedPOIService.getAllMapFilterComponents(mapFilterComponentRequest, securityContext);
    }
    @Operation(summary = "get all possible values for , used with the getAllMapFilterComponents API")
    @GetMapping("getAllMapFilterComponentsTypes")
    public List<String> getAllMapFilterComponentsTypes(
            @RequestHeader("authenticationKey") String authenticationKey,

            @RequestAttribute SecurityContextBase securityContext) {

       return Arrays.stream(FilterComponentType.values()).map(f->f.toString()).collect(Collectors.toList());
    }
}
