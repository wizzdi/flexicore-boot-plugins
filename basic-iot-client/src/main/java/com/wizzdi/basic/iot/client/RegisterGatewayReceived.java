package com.wizzdi.basic.iot.client;

public class RegisterGatewayReceived extends IOTMessage {

    public static final String STATUS_REGISTERED = "registered";
    public static final String STATUS_ALREADY_PENDING = "alreadyPending";
    public static final String STATUS_ALREADY_REGISTERED = "alreadyRegistered";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_TENANT_NOT_FOUND = "tenantNotFound";
    public static final String STATUS_TENANT_MISMATCH = "tenantMismatch";
    public static final String STATUS_INVALID = "invalid";

    private String registerGatewayId;
    private String registrationStatus;
    private String registeredGatewayRemoteId;
    private String pendingGatewayId;
    private String registeredGatewayId;
    private String registrationMessage;
    private String tenantId;
    private String tenantExternalId;

    public String getRegisterGatewayId() {
        return registerGatewayId;
    }

    public <T extends RegisterGatewayReceived> T setRegisterGatewayId(String registerGatewayId) {
        this.registerGatewayId = registerGatewayId;
        return (T) this;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public <T extends RegisterGatewayReceived> T setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
        return (T) this;
    }

    public String getRegisteredGatewayRemoteId() {
        return registeredGatewayRemoteId;
    }

    public <T extends RegisterGatewayReceived> T setRegisteredGatewayRemoteId(String registeredGatewayRemoteId) {
        this.registeredGatewayRemoteId = registeredGatewayRemoteId;
        return (T) this;
    }

    public String getPendingGatewayId() {
        return pendingGatewayId;
    }

    public <T extends RegisterGatewayReceived> T setPendingGatewayId(String pendingGatewayId) {
        this.pendingGatewayId = pendingGatewayId;
        return (T) this;
    }

    public String getRegisteredGatewayId() {
        return registeredGatewayId;
    }

    public <T extends RegisterGatewayReceived> T setRegisteredGatewayId(String registeredGatewayId) {
        this.registeredGatewayId = registeredGatewayId;
        return (T) this;
    }

    public String getRegistrationMessage() {
        return registrationMessage;
    }

    public <T extends RegisterGatewayReceived> T setRegistrationMessage(String registrationMessage) {
        this.registrationMessage = registrationMessage;
        return (T) this;
    }


    public String getTenantId() {
        return tenantId;
    }

    public <T extends RegisterGatewayReceived> T setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return (T) this;
    }

    public String getTenantExternalId() {
        return tenantExternalId;
    }

    public <T extends RegisterGatewayReceived> T setTenantExternalId(String tenantExternalId) {
        this.tenantExternalId = tenantExternalId;
        return (T) this;
    }

    @Override
    public boolean isRetained() {
        return false;
    }
}
