package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.StateSchema;


public class StateSchemaUpdate extends StateSchemaCreate{

    private String id;
    @JsonIgnore
    private StateSchema stateSchema;


    public String getId() {
        return id;
    }

    public <T extends StateSchemaUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public StateSchema getStateSchema() {
        return stateSchema;
    }

    public <T extends StateSchemaUpdate> T setStateSchema(StateSchema stateSchema) {
        this.stateSchema = stateSchema;
        return (T) this;
    }
}
