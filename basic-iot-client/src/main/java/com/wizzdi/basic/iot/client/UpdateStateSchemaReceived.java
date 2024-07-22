package com.wizzdi.basic.iot.client;

public class UpdateStateSchemaReceived extends IOTMessage {

    private String updateStateSchemaId;

    public String getUpdateStateSchemaId() {
        return updateStateSchemaId;
    }

    public <T extends UpdateStateSchemaReceived> T setUpdateStateSchemaId(String updateStateSchemaId) {
        this.updateStateSchemaId = updateStateSchemaId;
        return (T) this;
    }

    @Override
    public boolean isRetained() {
        return false;
    }
}
