package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.MappedPOI_;
import com.wizzdi.maps.service.request.MappedPOICreate;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.request.MappedPOIUpdate;
import com.wizzdi.maps.service.response.MappedPoiDTO;
import com.wizzdi.maps.service.service.MappedPOIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("MappedPOI")
@Extension
@Tag(name = "MappedPOI")
@OperationsInside
public class MappedPOIController implements Plugin {

    @Autowired
    private MappedPOIService mappedPOIService;

    @PostMapping("createMappedPOI")
    @Operation(summary = "createMappedPOI", description = "Creates MappedPOI")
    public MappedPOI createMappedPOI(
            @RequestHeader("authenticationKey") String authenticationKey,
            @RequestBody MappedPOICreate mappedPOICreate,
            @RequestAttribute SecurityContextBase securityContext) {
        mappedPOIService.validate(mappedPOICreate, securityContext);
        return mappedPOIService.createMappedPOI(mappedPOICreate, securityContext);
    }

    @Operation(summary = "updateMappedPOI", description = "Updates MappedPOI")
    @PutMapping("updateMappedPOI")
    public MappedPOI updateMappedPOI(
            @RequestHeader("authenticationKey") String authenticationKey,
            @RequestBody MappedPOIUpdate mappedPOIUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        String mappedPOIId = mappedPOIUpdate.getId();
        MappedPOI mappedPOI =
                mappedPOIService.getByIdOrNull(
                        mappedPOIId, MappedPOI.class, MappedPOI_.security, securityContext);
        if (mappedPOI == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No MappedPOI with id " + mappedPOIId);
        }
        mappedPOIUpdate.setMappedPOI(mappedPOI);
        mappedPOIService.validate(mappedPOIUpdate, securityContext);
        return mappedPOIService.updateMappedPOI(mappedPOIUpdate, securityContext);
    }

    @Operation(summary = "getAllMappedPOIs", description = "Gets All MappedPOIs Filtered")
    @PostMapping("getAllMappedPOIs")
    public PaginationResponse<MappedPOI> getAllMappedPOIs(
            @RequestHeader("authenticationKey") String authenticationKey,
            @RequestBody MappedPOIFilter mappedPOIFilter,
            @RequestAttribute SecurityContextBase securityContext) {
        mappedPOIService.validate(mappedPOIFilter, securityContext);
        return mappedPOIService.getAllMappedPOIs(mappedPOIFilter, securityContext);
    }

    @Operation(summary = "getAllMappedPOIDTOs", description = "Gets All MappedPOIDTOs Filtered")
    @PostMapping("getAllMappedPOIDTOs")
    public PaginationResponse<MappedPoiDTO> getAllMappedPOIDTOs(
            @RequestHeader("authenticationKey") String authenticationKey,
            @RequestBody MappedPOIFilter mappedPOIFilter,
            @RequestAttribute SecurityContextBase securityContext) {
        mappedPOIService.validate(mappedPOIFilter, securityContext);
        return mappedPOIService.getAllMappedPOIDTOs(mappedPOIFilter, securityContext);
    }

}
