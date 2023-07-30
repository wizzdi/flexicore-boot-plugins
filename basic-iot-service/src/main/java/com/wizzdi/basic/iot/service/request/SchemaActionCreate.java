package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.StateSchema;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class SchemaActionCreate extends BasicCreate {

    private String actionSchema;
    private String stateSchemaId;
    @JsonIgnore
    private StateSchema stateSchema;
    private String externalId;


    public String getActionSchema() {
        return actionSchema;
    }

    public <T extends SchemaActionCreate> T setActionSchema(String actionSchema) {
        this.actionSchema = actionSchema;
        return (T) this;
    }

    public String getStateSchemaId() {
        return stateSchemaId;
    }

    public <T extends SchemaActionCreate> T setStateSchemaId(String stateSchemaId) {
        this.stateSchemaId = stateSchemaId;
        return (T) this;
    }

    @JsonIgnore
    public StateSchema getStateSchema() {
        return stateSchema;
    }

    public <T extends SchemaActionCreate> T setStateSchema(StateSchema stateSchema) {
        this.stateSchema = stateSchema;
        return (T) this;
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends SchemaActionCreate> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }
}
