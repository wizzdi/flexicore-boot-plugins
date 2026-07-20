package com.wizzdi.tenant.data.administration.response;

import java.util.List;

public record TenantDataDeletionResponse(
        String tenantId,
        String tenantExternalId,
        List<String> requestedTenantIds,
        List<String> requestedTenantExternalIds,
        boolean dry,
        boolean deletionPerformed,
        boolean deleteSecurityObjects,
        boolean requestingUserSelectedForDeletion,
        boolean requestingUserDeleted,
        List<String> selectedTenantIds,
        List<String> deletedTenantIds,
        List<String> retainedTenantIds,
        List<String> tenantDeletionOrder,
        List<String> deletionOrder,
        List<TenantDeletionTypeRecords> recordsByType,
        long totalSelectedRecords,
        long totalDeletedRecords) {
}
