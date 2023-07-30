package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.annotations.Invoker;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.service.response.MappedPOIRelated;
import com.wizzdi.maps.service.service.MappedPOIRelatedService;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("MappedPOIRelated")
@Extension
@Tag(name = "MappedPOIRelated")
@OperationsInside
public class MappedPOIRelatedController implements Plugin, Invoker {

    @Autowired
    private MappedPOIRelatedService mappedPOIService;


    @Operation(summary = "getAllMappedPOIRelated", description = "Gets All MappedPOIs related")
    @PostMapping("getAllMappedPOIRelated")
    public PaginationResponse<MappedPOIRelated> getAllMappedPOIRelated(
            @RequestHeader("authenticationKey") String authenticationKey,
            @RequestBody MappedPOIFilter mappedPOIFilter,
            @RequestAttribute SecurityContextBase securityContext) {
        mappedPOIService.validate(mappedPOIFilter, securityContext);
        return mappedPOIService.getAllMappedPOIRelated(mappedPOIFilter, securityContext);
    }

    @Override
    public Class<?> getHandlingClass() {
        return MappedPOIRelated.class;
    }
}
