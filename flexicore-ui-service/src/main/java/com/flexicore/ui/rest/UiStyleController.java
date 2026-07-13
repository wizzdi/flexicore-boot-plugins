package com.flexicore.ui.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.ui.model.UiStyle;
import com.flexicore.ui.request.UiStyleCreate;
import com.flexicore.ui.request.UiStyleFiltering;
import com.flexicore.ui.request.UiStylePropertyDefinitionFilter;
import com.flexicore.ui.request.UiStyleUpdate;
import com.flexicore.ui.response.UiStylePropertyDefinition;
import com.flexicore.ui.service.UiStyleDefinitionService;
import com.flexicore.ui.service.UiStyleService;
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

import java.util.List;

@OperationsInside
@RestController
@RequestMapping("plugins/UIStyles")
@Tag(name = "UIStyles", description = "Reusable named UI styles for presets and other visual components")
@Extension
@Component
public class UiStyleController implements Plugin {

    @Autowired
    private UiStyleService uiStyleService;
    @Autowired
    private UiStyleDefinitionService uiStyleDefinitionService;

    @Operation(summary = "getAllUIStyles", description = "Returns accessible named UI styles and their property lines")
    @PostMapping("getAllUIStyles")
    public PaginationResponse<UiStyle> getAllUiStyles(
            @RequestBody UiStyleFiltering filtering,
            @RequestAttribute SecurityContext securityContext) {
        return uiStyleService.getAllUiStyles(filtering, securityContext);
    }

    @Operation(summary = "getUIStylePropertyDefinitions",
            description = "Returns the registered style property catalog used to build non-technical style editors")
    @PostMapping("getUIStylePropertyDefinitions")
    public List<UiStylePropertyDefinition> getUiStylePropertyDefinitions(
            @RequestBody(required = false) UiStylePropertyDefinitionFilter filter) {
        return uiStyleDefinitionService.getDefinitions(filter);
    }

    @Operation(summary = "createUIStyle", description = "Creates a reusable named UI style")
    @PostMapping("createUIStyle")
    public UiStyle createUiStyle(
            @RequestBody UiStyleCreate create,
            @RequestAttribute SecurityContext securityContext) {
        return uiStyleService.createUiStyle(create, securityContext);
    }

    @Operation(summary = "updateUIStyle", description = "Updates a reusable named UI style")
    @PutMapping("updateUIStyle")
    public UiStyle updateUiStyle(
            @RequestBody UiStyleUpdate update,
            @RequestAttribute SecurityContext securityContext) {
        UiStyle style = update.getId() != null
                ? uiStyleService.getByIdOrNull(update.getId(), UiStyle.class, securityContext)
                : null;
        if (style == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No accessible UI style with id " + update.getId());
        }
        update.setUiStyle(style);
        return uiStyleService.updateUiStyle(update, securityContext);
    }
}
