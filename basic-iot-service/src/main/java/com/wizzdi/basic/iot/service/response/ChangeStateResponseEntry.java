package com.wizzdi.basic.iot.service.response;

public class ChangeStateResponseEntry {

    private String remoteId;
    private int onTry;
    private boolean success;


    public String getRemoteId() {
        return remoteId;
    }

    public <T extends ChangeStateResponseEntry> T setRemoteId(String remoteId) {
        this.remoteId = remoteId;
        return (T) this;
    }

    public int getOnTry() {
        return onTry;
    }

    public <T extends ChangeStateResponseEntry> T setOnTry(int onTry) {
        this.onTry = onTry;
        return (T) this;
    }

    public boolean isSuccess() {
        return success;
    }

    public <T extends ChangeStateResponseEntry> T setSuccess(boolean success) {
        this.success = success;
        return (T) this;
    }
}
