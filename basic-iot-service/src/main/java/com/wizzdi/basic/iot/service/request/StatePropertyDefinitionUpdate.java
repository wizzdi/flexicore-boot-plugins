package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.StatePropertyDefinition;

public class StatePropertyDefinitionUpdate extends StatePropertyDefinitionCreate {
    private String id;
    @JsonIgnore
    private StatePropertyDefinition statePropertyDefinition;

    public String getId() { return id; }
    public <T extends StatePropertyDefinitionUpdate> T setId(String id) { this.id = id; return (T) this; }
    public StatePropertyDefinition getStatePropertyDefinition() { return statePropertyDefinition; }
    public <T extends StatePropertyDefinitionUpdate> T setStatePropertyDefinition(StatePropertyDefinition statePropertyDefinition) { this.statePropertyDefinition = statePropertyDefinition; return (T) this; }
}
