package com.wizzdi.basic.iot.client;

import java.util.ArrayList;
import java.util.List;

public class UpdateStateSchema extends IOTMessage{

    private String deviceType;
    private String jsonSchema;
    private int version;

    private String deviceId;
    private List<SchemaAction> schemaActions=new ArrayList<>();

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

    public String getDeviceId() {
        return deviceId;
    }

    public <T extends UpdateStateSchema> T setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return (T) this;
    }

    public List<SchemaAction> getSchemaActions() {
        return schemaActions;
    }

    public <T extends UpdateStateSchema> T setSchemaActions(List<SchemaAction> schemaActions) {
        this.schemaActions = schemaActions;
        return (T) this;
    }
}
