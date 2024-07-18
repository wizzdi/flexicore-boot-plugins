package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.SecurityUser;

public class MoveGatewaysRequest {

    private String targetTenantId;
    @JsonIgnore
    private SecurityTenant targetTenant;
    private String targetTenantAdminId;
    @JsonIgnore
    private SecurityUser targetTenantAdmin;
    private GatewayFilter gatewayFilter;

    public String getTargetTenantId() {
        return targetTenantId;
    }

    public <T extends MoveGatewaysRequest> T setTargetTenantId(String targetTenantId) {
        this.targetTenantId = targetTenantId;
        return (T) this;
    }

    public SecurityTenant getTargetTenant() {
        return targetTenant;
    }

    public <T extends MoveGatewaysRequest> T setTargetTenant(SecurityTenant targetTenant) {
        this.targetTenant = targetTenant;
        return (T) this;
    }

    public GatewayFilter getGatewayFilter() {
        return gatewayFilter;
    }

    public <T extends MoveGatewaysRequest> T setGatewayFilter(GatewayFilter gatewayFilter) {
        this.gatewayFilter = gatewayFilter;
        return (T) this;
    }

    public String getTargetTenantAdminId() {
        return targetTenantAdminId;
    }

    public <T extends MoveGatewaysRequest> T setTargetTenantAdminId(String targetTenantAdminId) {
        this.targetTenantAdminId = targetTenantAdminId;
        return (T) this;
    }

    @JsonIgnore
    public SecurityUser getTargetTenantAdmin() {
        return targetTenantAdmin;
    }

    public <T extends MoveGatewaysRequest> T setTargetTenantAdmin(SecurityUser targetTenantAdmin) {
        this.targetTenantAdmin = targetTenantAdmin;
        return (T) this;
    }
}
