package com.wizzdi.tenant.data.administration.response;

public record TenantTreeEntry(
        String tenantId,
        String externalId,
        String name,
        String ownerTenantId,
        int depth) {
}
