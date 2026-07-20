package com.wizzdi.tenant.data.administration.request;

import java.util.List;

public class TenantDataRequest {
    /**
     * Backward-compatible single-tenant input. When tenantIds is also supplied,
     * this value is merged into the same de-duplicated request set.
     */
    private String tenantId;
    private List<String> tenantIds;

    public String getTenantId() {
        return tenantId;
    }

    public <T extends TenantDataRequest> T setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return (T) this;
    }

    public List<String> getTenantIds() {
        return tenantIds;
    }

    public <T extends TenantDataRequest> T setTenantIds(List<String> tenantIds) {
        this.tenantIds = tenantIds;
        return (T) this;
    }
}
