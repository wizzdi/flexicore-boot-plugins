package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.HealthSignalValueType;
import com.wizzdi.basic.iot.model.StateSchema;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class StatePropertyDefinitionCreate extends BasicCreate {
    private String stateSchemaId;
    @JsonIgnore
    private StateSchema stateSchema;
    private String externalId;
    private String propertyPath;
    private HealthSignalValueType valueType;
    private String unit;

    public String getStateSchemaId() { return stateSchemaId; }
    public <T extends StatePropertyDefinitionCreate> T setStateSchemaId(String stateSchemaId) { this.stateSchemaId = stateSchemaId; return (T) this; }
    public StateSchema getStateSchema() { return stateSchema; }
    public <T extends StatePropertyDefinitionCreate> T setStateSchema(StateSchema stateSchema) { this.stateSchema = stateSchema; return (T) this; }
    public String getExternalId() { return externalId; }
    public <T extends StatePropertyDefinitionCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }
    public String getPropertyPath() { return propertyPath; }
    public <T extends StatePropertyDefinitionCreate> T setPropertyPath(String propertyPath) { this.propertyPath = propertyPath; return (T) this; }
    public HealthSignalValueType getValueType() { return valueType; }
    public <T extends StatePropertyDefinitionCreate> T setValueType(HealthSignalValueType valueType) { this.valueType = valueType; return (T) this; }
    public String getUnit() { return unit; }
    public <T extends StatePropertyDefinitionCreate> T setUnit(String unit) { this.unit = unit; return (T) this; }
}
