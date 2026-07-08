package com.wizzdi.basic.iot.client;

public class RegisterGatewayConfirmationReceived extends IOTMessage {

    private String registerGatewayReceivedId;
    private String registrationStatus;
    private Boolean confirmed;
    private String pendingGatewayId;
    private String registeredGatewayId;
    private String registrationMessage;

    public String getRegisterGatewayReceivedId() {
        return registerGatewayReceivedId;
    }

    public <T extends RegisterGatewayConfirmationReceived> T setRegisterGatewayReceivedId(String registerGatewayReceivedId) {
        this.registerGatewayReceivedId = registerGatewayReceivedId;
        return (T) this;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public <T extends RegisterGatewayConfirmationReceived> T setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
        return (T) this;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public <T extends RegisterGatewayConfirmationReceived> T setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
        return (T) this;
    }

    public String getPendingGatewayId() {
        return pendingGatewayId;
    }

    public <T extends RegisterGatewayConfirmationReceived> T setPendingGatewayId(String pendingGatewayId) {
        this.pendingGatewayId = pendingGatewayId;
        return (T) this;
    }

    public String getRegisteredGatewayId() {
        return registeredGatewayId;
    }

    public <T extends RegisterGatewayConfirmationReceived> T setRegisteredGatewayId(String registeredGatewayId) {
        this.registeredGatewayId = registeredGatewayId;
        return (T) this;
    }

    public String getRegistrationMessage() {
        return registrationMessage;
    }

    public <T extends RegisterGatewayConfirmationReceived> T setRegistrationMessage(String registrationMessage) {
        this.registrationMessage = registrationMessage;
        return (T) this;
    }

    @Override
    public boolean isRetained() {
        return false;
    }
}
