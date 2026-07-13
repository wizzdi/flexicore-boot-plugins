package com.flexicore.ui.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.ui.model.GridPresetStyle;
import com.flexicore.ui.request.GridPresetStyleCreate;
import com.flexicore.ui.request.GridPresetStyleFiltering;
import com.flexicore.ui.request.GridPresetStyleUpdate;
import com.flexicore.ui.service.GridPresetStyleService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@OperationsInside
@RestController
@RequestMapping("plugins/GridPresetStyles")
@Tag(name = "GridPresetStyles", description = "Named reusable title and table styles for GridPreset")
@Tag(name = "Presets")
@Extension
@Component
public class GridPresetStyleController implements Plugin {

    @Autowired
    private GridPresetStyleService service;

    @Operation(summary = "getAllGridPresetStyles", description = "Returns accessible named GridPreset styles")
    @PostMapping("getAllGridPresetStyles")
    public PaginationResponse<GridPresetStyle> getAllGridPresetStyles(
            @RequestBody GridPresetStyleFiltering filtering,
            @RequestAttribute SecurityContext securityContext) {
        service.validateFiltering(filtering, securityContext);
        return service.getAllGridPresetStyles(filtering, securityContext);
    }

    @Operation(summary = "createGridPresetStyle", description = "Creates a named reusable GridPreset style")
    @PostMapping("createGridPresetStyle")
    public GridPresetStyle createGridPresetStyle(
            @RequestBody GridPresetStyleCreate create,
            @RequestAttribute SecurityContext securityContext) {
        service.validate(create, securityContext);
        return service.createGridPresetStyle(create, securityContext);
    }

    @Operation(summary = "updateGridPresetStyle", description = "Updates a named reusable GridPreset style")
    @PutMapping("updateGridPresetStyle")
    public GridPresetStyle updateGridPresetStyle(
            @RequestBody GridPresetStyleUpdate update,
            @RequestAttribute SecurityContext securityContext) {
        GridPresetStyle style = update.getId() != null
                ? service.getByIdOrNull(update.getId(), GridPresetStyle.class, securityContext)
                : null;
        if (style == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No GridPresetStyle with id " + update.getId());
        }
        update.setGridPresetStyle(style);
        service.validate(update, securityContext);
        return service.updateGridPresetStyle(update, securityContext);
    }
}
