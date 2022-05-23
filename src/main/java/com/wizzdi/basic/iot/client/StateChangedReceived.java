package com.wizzdi.basic.iot.client;

public class StateChangedReceived extends IOTMessage{
    private String stateChangedId;


    public String getStateChangedId() {
        return stateChangedId;
    }

    public <T extends StateChangedReceived> T setStateChangedId(String stateChangedId) {
        this.stateChangedId = stateChangedId;
        return (T) this;
    }

    @Override
    public String toString() {
        return "StateChangedReceived{" +
                "stateChangedId='" + stateChangedId + '\'' +
                "} " + super.toString();
    }
}
