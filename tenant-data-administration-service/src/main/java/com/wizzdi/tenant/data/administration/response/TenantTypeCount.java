package com.wizzdi.tenant.data.administration.response;

public record TenantTypeCount(
        String clazz,
        String simpleName,
        String tableName,
        long count,
        boolean securityData) {
}
