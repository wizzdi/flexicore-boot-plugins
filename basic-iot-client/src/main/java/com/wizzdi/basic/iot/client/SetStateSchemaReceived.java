package com.wizzdi.basic.iot.client;

public class SetStateSchemaReceived extends IOTMessage {

    private String setStateSchemaId;
    private boolean found;

    public String getSetStateSchemaId() {
        return setStateSchemaId;
    }

    public <T extends SetStateSchemaReceived> T setSetStateSchemaId(String setStateSchemaId) {
        this.setStateSchemaId = setStateSchemaId;
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
