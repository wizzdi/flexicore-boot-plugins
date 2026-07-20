package com.wizzdi.tenant.data.administration.response;

import java.util.List;

public record TenantDataInventoryResponse(
        String tenantId,
        String tenantExternalId,
        List<String> requestedTenantIds,
        List<String> requestedTenantExternalIds,
        List<TenantTreeEntry> tenantTree,
        List<TenantTypeCount> recordsByType,
        int dynamicallyDiscoveredEntityRoots,
        long totalRecords) {
}
