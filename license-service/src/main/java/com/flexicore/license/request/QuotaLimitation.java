package com.flexicore.license.request;

public class QuotaLimitation {
    private final Class<?> clazz;
    private final int quota;

    public QuotaLimitation(Class<?> clazz, int quota) {
        this.clazz = clazz;
        this.quota = quota;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public int getQuota() {
        return quota;
    }
}
