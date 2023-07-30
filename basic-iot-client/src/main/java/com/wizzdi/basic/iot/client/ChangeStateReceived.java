package com.wizzdi.basic.iot.client;

public class ChangeStateReceived extends IOTMessage{
    private String changeStateId;


    public String getChangeStateId() {
        return changeStateId;
    }

    public <T extends ChangeStateReceived> T setChangeStateId(String changeStateId) {
        this.changeStateId = changeStateId;
        return (T) this;
    }

    @Override
    public String toString() {
        return "ChangeStateReceived{" +
                "changeStateId='" + changeStateId + '\'' +
                "} " + super.toString();
    }
}
