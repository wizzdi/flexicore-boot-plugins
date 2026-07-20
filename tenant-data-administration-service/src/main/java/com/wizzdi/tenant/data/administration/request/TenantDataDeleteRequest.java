package com.wizzdi.tenant.data.administration.request;

public class TenantDataDeleteRequest extends TenantDataRequest {
    private boolean deleteSecurityObjects;
    private boolean dry;

    public boolean isDeleteSecurityObjects() {
        return deleteSecurityObjects;
    }

    public TenantDataDeleteRequest setDeleteSecurityObjects(boolean deleteSecurityObjects) {
        this.deleteSecurityObjects = deleteSecurityObjects;
        return this;
    }

    public boolean isDry() {
        return dry;
    }

    public TenantDataDeleteRequest setDry(boolean dry) {
        this.dry = dry;
        return this;
    }
}
