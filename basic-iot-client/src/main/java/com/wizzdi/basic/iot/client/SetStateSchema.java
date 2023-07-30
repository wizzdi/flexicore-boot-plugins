package com.wizzdi.basic.iot.client;

public class SetStateSchema extends IOTMessage{

    private String deviceType;
    private int version;

    private String deviceId;

    public String getDeviceType() {
        return deviceType;
    }

    public <T extends SetStateSchema> T setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }


    public int getVersion() {
        return version;
    }

    public <T extends SetStateSchema> T setVersion(int version) {
        this.version = version;
        return (T) this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public <T extends SetStateSchema> T setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return (T) this;
    }
}
