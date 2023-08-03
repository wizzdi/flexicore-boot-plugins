package com.flexicore.ui.rest;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.wizzdi.flexicore.security.response.PaginationResponse;

import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.model.*;
import com.flexicore.ui.request.*;
import com.flexicore.ui.response.PresetToRoleContainer;
import com.flexicore.ui.response.PresetToTenantContainer;
import com.flexicore.ui.response.PresetToUserContainer;
import com.flexicore.ui.service.UiFieldService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * Created by Asaf on 04/06/2017.
 */


@OperationsInside
@RestController
@RequestMapping("plugins/UiFields")

@Tag(name = "UiFields")
@Extension
@Component
public class UiFieldController implements Plugin {


    @Autowired
    private UiFieldService service;


    @Operation(summary = "deleteUIField", description = "delete UI Field")
    @DeleteMapping("{id}")
    public UiField deleteUIField(
            
            @PathVariable("id") String id,
            @RequestAttribute SecurityContextBase securityContext) {
        UiField uiField=id!=null?service.getByIdOrNull(id,UiField.class,UiField_.security,securityContext):null;
        if(uiField==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ui field with id "+id);
        }
        return service.deleteUIField(uiField, securityContext);

    }

    @Operation(summary = "listAllUiFields", description = "List all Ui Fields")
    @PostMapping("getAllUiFields")
    public PaginationResponse<UiField> getAllUiFields(
            
            @RequestBody UiFieldFiltering uiFieldFiltering,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(uiFieldFiltering, securityContext);
        return service.getAllUiFields(uiFieldFiltering, securityContext);

    }


    @Operation(summary = "linkPresetToUser", description = "Links preset to securityUser")
    @PostMapping("linkPresetToUser")
    public PresetToUser linkPresetToUser(
            
            @RequestBody PresetToUserCreate linkPresetToUser,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(linkPresetToUser, securityContext);
        return service.createPresetToUser(linkPresetToUser, securityContext);

    }


    @Operation(summary = "getAllPresetToRole", description = "getAllPresetToRole")
    @PostMapping("getAllPresetToRole")
    public PaginationResponse<PresetToRoleContainer> getAllPresetToRole(
            
            @RequestBody PresetToRoleFilter presetToRoleFilter,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(presetToRoleFilter, securityContext);
        return service.getAllPresetToRole(presetToRoleFilter, securityContext);

    }


    @Operation(summary = "getAllPresetToUser", description = "getAllPresetToUser")
    @PostMapping("getAllPresetToUser")
    public PaginationResponse<PresetToUserContainer> getAllPresetToUser(
             @RequestBody
            PresetToUserFilter presetToUserFilter,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(presetToUserFilter, securityContext);
        return service.getAllPresetToUser(presetToUserFilter, securityContext);

    }


    @Operation(summary = "getAllPresetToTenant", description = "getAllPresetToTenant")
    @PostMapping("getAllPresetToTenant")
    public PaginationResponse<PresetToTenantContainer> getAllPresetToTenant(
             @RequestBody
            PresetToTenantFilter presetToTenantFilter,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(presetToTenantFilter, securityContext);
        return service.getAllPresetToTenant(presetToTenantFilter,
                securityContext);

    }


    @Operation(summary = "getPreferredPresets", description = "returns preferred presets")
    @PostMapping("getPreferredPresets")
    public List<Preset> getPreferredPresets(
             @RequestBody
            PreferedPresetRequest linkPresetToRole,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(linkPresetToRole, securityContext);
        return service.getPreferredPresets(linkPresetToRole, securityContext);

    }


    @Operation(summary = "linkPresetToRole", description = "Links preset to Role")
    @PostMapping("linkPresetToRole")
    public PresetToRole linkPresetToRole(
             @RequestBody
            PresetToRoleCreate linkPresetToRole,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(linkPresetToRole, securityContext);
        return service.createPresetToRole(linkPresetToRole, securityContext);

    }


    @Operation(summary = "linkPresetToTenant", description = "Links preset to SecurityTenant")
    @PostMapping("linkPresetToTenant")
    public PresetToTenant linkPresetToTenant(
             @RequestBody
            PresetToTenantCreate linkPresetToRole,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(linkPresetToRole, securityContext);
        return service.createPresetToTenant(linkPresetToRole, securityContext);

    }


    @Operation(summary = "updatePresetToTenant", description = "updates preset to SecurityTenant")
    @PutMapping("updatePresetToTenant")
    public PresetToTenant updatePresetToTenant(
             @RequestBody
            PresetToTenantUpdate updateLinkPresetToTenant,
            @RequestAttribute SecurityContextBase securityContext) {
        PresetToTenant preset = updateLinkPresetToTenant.getLinkId() != null
                ? service.getByIdOrNull(updateLinkPresetToTenant.getLinkId(),
                PresetToTenant.class, PresetToTenant_.security, securityContext) : null;
        if (preset == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no link with id "
                    + updateLinkPresetToTenant.getLinkId());
        }
        updateLinkPresetToTenant.setPresetToTenant(preset);
        return service.updatePresetToTenant(updateLinkPresetToTenant,
                securityContext);

    }


    @Operation(summary = "updatePresetToUser", description = "updates preset to SecurityUser")
    @PutMapping("updatePresetToUser")
    public PresetToUser updatePresetToUser(
             @RequestBody
            PresetToUserUpdate updateLinkPresetToUser,
            @RequestAttribute SecurityContextBase securityContext) {
        PresetToUser preset = updateLinkPresetToUser.getLinkId() != null
                ? service.getByIdOrNull(updateLinkPresetToUser.getLinkId(),
                PresetToUser.class, PresetToUser_.security, securityContext) : null;
        if (preset == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no link with id "
                    + updateLinkPresetToUser.getLinkId());
        }
        updateLinkPresetToUser.setPresetToUser(preset);
        return service.updatePresetToUser(updateLinkPresetToUser,
                securityContext);

    }


    @Operation(summary = "updatePresetToRole", description = "updates preset to Role")
    @PutMapping("updatePresetToRole")
    public PresetToRole updatePresetToRole(
             @RequestBody
            PresetToRoleUpdate updateLinkPresetToTenant,
            @RequestAttribute SecurityContextBase securityContext) {
        PresetToRole preset = updateLinkPresetToTenant.getLinkId() != null
                ? service.getByIdOrNull(updateLinkPresetToTenant.getLinkId(),
                PresetToRole.class, PresetToRole_.security, securityContext) : null;
        if (preset == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no link with id "
                    + updateLinkPresetToTenant.getLinkId());
        }
        updateLinkPresetToTenant.setPresetToRole(preset);
        return service.updatePresetToRole(updateLinkPresetToTenant,
                securityContext);

    }

}
