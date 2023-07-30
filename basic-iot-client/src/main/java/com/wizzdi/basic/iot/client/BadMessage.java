package com.wizzdi.basic.iot.client;

public class BadMessage extends IOTMessage{

    private String originalMessage;
    private String error;


    public String getOriginalMessage() {
        return originalMessage;
    }

    public <T extends BadMessage> T setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
        return (T) this;
    }

    public String getError() {
        return error;
    }

    public <T extends BadMessage> T setError(String error) {
        this.error = error;
        return (T) this;
    }

    @Override
    public boolean isRequireAuthentication() {
        return false;
    }
}
