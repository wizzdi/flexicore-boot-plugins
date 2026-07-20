package com.wizzdi.tenant.data.administration.response;

import java.util.List;

public record TenantDeletionTypeRecords(
        String clazz,
        String simpleName,
        String tableName,
        boolean securityData,
        long count,
        List<TenantDeletionRecord> records) {
}
