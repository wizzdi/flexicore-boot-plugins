package com.wizzdi.maps.service.ws.messages;

public class MappedPOINotification {

    private String id;
    private String mappedPOIId;
    private String tenantId;

    public String getId() {
        return id;
    }

    public <T extends MappedPOINotification> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public String getMappedPOIId() {
        return mappedPOIId;
    }

    public <T extends MappedPOINotification> T setMappedPOIId(String mappedPOIId) {
        this.mappedPOIId = mappedPOIId;
        return (T) this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public <T extends MappedPOINotification> T setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return (T) this;
    }
}
