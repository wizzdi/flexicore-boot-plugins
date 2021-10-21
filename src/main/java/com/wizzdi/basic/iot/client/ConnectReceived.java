package com.wizzdi.basic.iot.client;

public class ConnectReceived extends IOTMessage{
    private String connectId;
    private ConnectionState connectionState;


    public String getConnectId() {
        return connectId;
    }

    public <T extends ConnectReceived> T setConnectId(String connectId) {
        this.connectId = connectId;
        return (T) this;
    }
}
