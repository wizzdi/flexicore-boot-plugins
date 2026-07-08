package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecurityTenant;
import com.wizzdi.basic.iot.model.PendingGateway;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApproveGatewaysRequest {

    private Set<String> pendingGatewayIds = new HashSet<>();
    private String tenantId;
    @JsonIgnore
    private List<PendingGateway> pendingGateways = new ArrayList<>();
    @JsonIgnore
    private SecurityTenant targetTenant;

    public Set<String> getPendingGatewayIds() {
        return pendingGatewayIds;
    }

    public <T extends ApproveGatewaysRequest> T setPendingGatewayIds(Set<String> pendingGatewayIds) {
        this.pendingGatewayIds = pendingGatewayIds;
        return (T) this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public <T extends ApproveGatewaysRequest> T setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return (T) this;
    }

    @JsonIgnore
    public List<PendingGateway> getPendingGateways() {
        return pendingGateways;
    }

    public <T extends ApproveGatewaysRequest> T setPendingGateways(List<PendingGateway> pendingGateways) {
        this.pendingGateways = pendingGateways;
        return (T) this;
    }

    @JsonIgnore
    public SecurityTenant getTargetTenant() {
        return targetTenant;
    }

    public <T extends ApproveGatewaysRequest> T setTargetTenant(SecurityTenant targetTenant) {
        this.targetTenant = targetTenant;
        return (T) this;
    }
}
