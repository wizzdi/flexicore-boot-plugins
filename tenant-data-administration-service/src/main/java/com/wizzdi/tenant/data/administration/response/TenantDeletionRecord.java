package com.wizzdi.tenant.data.administration.response;

public record TenantDeletionRecord(
        String id,
        String tenantId,
        String name,
        String externalId) {
}
