package com.wizzdi.tenant.data.administration.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.tenant.data.administration.request.TenantDataDeleteRequest;
import com.wizzdi.tenant.data.administration.request.TenantDataRequest;
import com.wizzdi.tenant.data.administration.response.TenantDataDeletionResponse;
import com.wizzdi.tenant.data.administration.response.TenantDataInventoryResponse;
import com.wizzdi.tenant.data.administration.service.TenantDataAdministrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@OperationsInside
@RequestMapping("/plugins/TenantDataAdministration")
@Tag(name = "Tenant Data Administration")
@Extension
@RestController
public class TenantDataAdministrationController implements Plugin {

    @Autowired
    private TenantDataAdministrationService service;

    @PostMapping("/getTenantData")
    @Operation(
            summary = "Returns every dynamically discovered entity type and record count in tenant trees",
            description = "Accepts tenantIds, merges overlapping or nested tenant trees, and includes every tenant owned by any requested tenant")
    @IOperation(
            Name = "getTenantData",
            Description = "Returns entity counts for requested tenants and their merged owned-tenant trees")
    public TenantDataInventoryResponse getTenantData(
            @RequestBody TenantDataRequest request,
            @RequestAttribute SecurityContext securityContext) {
        return service.getTenantData(request, securityContext);
    }

    @PostMapping("/deleteTenantData")
    @Operation(
            summary = "Hard-deletes tenant data in dependency order",
            description = "Accepts tenantIds, merges duplicate and nested tenant trees, and processes tenant records deepest-first by persisted ownership. Set dry=true to return the exact grouped record list without deleting. Security users, roles, permissions and tenant records are retained unless deleteSecurityObjects is true")
    @IOperation(
            Name = "deleteTenantData",
            Description = "Hard-deletes tenant data and optionally its security objects")
    public TenantDataDeletionResponse deleteTenantData(
            @RequestBody TenantDataDeleteRequest request,
            @RequestAttribute SecurityContext securityContext) {
        TenantDataDeletionResponse response = service.deleteTenantData(request, securityContext);
        if (request != null && !request.isDry()) {
            // Audit persistence runs after the controller returns. Detach that audit entry from the
            // deleted tenant so the deletion request itself cannot recreate tenant-scoped data.
            securityContext.setTenantToCreateIn(null);
        }
        return response;
    }
}
