package com.wizzdi.basic.iot.tester.request;

public class IotTestStartRequest {
    private String tenantExternalId;
    private String gatewayExternalId;
    private Double lat;
    private Double lon;
    private Integer deviceCount = 3;
    private Long stepTimeoutMs = 30_000L;
    private Long pollIntervalMs = 250L;
    private Integer keepAliveCount = 3;
    private Long keepAliveIntervalMs = 1_000L;
    private Boolean accelerateDisconnect = true;
    private Long disconnectGraceMs = 1_000L;
    private String emailDestination;
    private String whatsAppDestination;
    private Boolean requireExternalDelivery = false;

    public String getTenantExternalId() { return tenantExternalId; }
    public <T extends IotTestStartRequest> T setTenantExternalId(String tenantExternalId) { this.tenantExternalId = tenantExternalId; return (T) this; }
    public String getGatewayExternalId() { return gatewayExternalId; }
    public <T extends IotTestStartRequest> T setGatewayExternalId(String gatewayExternalId) { this.gatewayExternalId = gatewayExternalId; return (T) this; }
    public Double getLat() { return lat; }
    public <T extends IotTestStartRequest> T setLat(Double lat) { this.lat = lat; return (T) this; }
    public Double getLon() { return lon; }
    public <T extends IotTestStartRequest> T setLon(Double lon) { this.lon = lon; return (T) this; }
    public Integer getDeviceCount() { return deviceCount; }
    public <T extends IotTestStartRequest> T setDeviceCount(Integer deviceCount) { this.deviceCount = deviceCount; return (T) this; }
    public Long getStepTimeoutMs() { return stepTimeoutMs; }
    public <T extends IotTestStartRequest> T setStepTimeoutMs(Long stepTimeoutMs) { this.stepTimeoutMs = stepTimeoutMs; return (T) this; }
    public Long getPollIntervalMs() { return pollIntervalMs; }
    public <T extends IotTestStartRequest> T setPollIntervalMs(Long pollIntervalMs) { this.pollIntervalMs = pollIntervalMs; return (T) this; }
    public Integer getKeepAliveCount() { return keepAliveCount; }
    public <T extends IotTestStartRequest> T setKeepAliveCount(Integer keepAliveCount) { this.keepAliveCount = keepAliveCount; return (T) this; }
    public Long getKeepAliveIntervalMs() { return keepAliveIntervalMs; }
    public <T extends IotTestStartRequest> T setKeepAliveIntervalMs(Long keepAliveIntervalMs) { this.keepAliveIntervalMs = keepAliveIntervalMs; return (T) this; }
    public Boolean getAccelerateDisconnect() { return accelerateDisconnect; }
    public <T extends IotTestStartRequest> T setAccelerateDisconnect(Boolean accelerateDisconnect) { this.accelerateDisconnect = accelerateDisconnect; return (T) this; }
    public Long getDisconnectGraceMs() { return disconnectGraceMs; }
    public <T extends IotTestStartRequest> T setDisconnectGraceMs(Long disconnectGraceMs) { this.disconnectGraceMs = disconnectGraceMs; return (T) this; }
    public String getEmailDestination() { return emailDestination; }
    public <T extends IotTestStartRequest> T setEmailDestination(String emailDestination) { this.emailDestination = emailDestination; return (T) this; }
    public String getWhatsAppDestination() { return whatsAppDestination; }
    public <T extends IotTestStartRequest> T setWhatsAppDestination(String whatsAppDestination) { this.whatsAppDestination = whatsAppDestination; return (T) this; }
    public Boolean getRequireExternalDelivery() { return requireExternalDelivery; }
    public <T extends IotTestStartRequest> T setRequireExternalDelivery(Boolean requireExternalDelivery) { this.requireExternalDelivery = requireExternalDelivery; return (T) this; }
}
