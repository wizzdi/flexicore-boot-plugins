package com.wizzdi.basic.iot.client;

public class UpdateStateSchema extends IOTMessage{

    private String deviceType;
    private String jsonSchema;
    private int version;

    public String getDeviceType() {
        return deviceType;
    }

    public <T extends UpdateStateSchema> T setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public <T extends UpdateStateSchema> T setJsonSchema(String jsonSchema) {
        this.jsonSchema = jsonSchema;
        return (T) this;
    }

    public int getVersion() {
        return version;
    }

    public <T extends UpdateStateSchema> T setVersion(int version) {
        this.version = version;
        return (T) this;
    }
}
