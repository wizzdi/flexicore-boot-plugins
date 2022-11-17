package com.wizzdi.basic.iot.client;

public class SetStateSchemaReceived extends IOTMessage {

    private String updateStateSchemaId;
    private boolean found;

    public String getUpdateStateSchemaId() {
        return updateStateSchemaId;
    }

    public <T extends SetStateSchemaReceived> T setUpdateStateSchemaId(String updateStateSchemaId) {
        this.updateStateSchemaId = updateStateSchemaId;
        return (T) this;
    }

    public boolean isFound() {
        return found;
    }

    public <T extends SetStateSchemaReceived> T setFound(boolean found) {
        this.found = found;
        return (T) this;
    }
}
