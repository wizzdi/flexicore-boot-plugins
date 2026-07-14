package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = @Index(name = "state_property_schema_path_idx", columnList = "stateSchema_id,propertyPath"))
public class StatePropertyDefinition extends Baseclass {

    @ManyToOne(targetEntity = StateSchema.class)
    private StateSchema stateSchema;

    private String externalId;
    private String propertyPath;

    @Enumerated(EnumType.STRING)
    private HealthSignalValueType valueType;

    private String unit;

    public StateSchema getStateSchema() {
        return stateSchema;
    }

    public <T extends StatePropertyDefinition> T setStateSchema(StateSchema stateSchema) {
        this.stateSchema = stateSchema;
        return (T) this;
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends StatePropertyDefinition> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public <T extends StatePropertyDefinition> T setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
        return (T) this;
    }

    public HealthSignalValueType getValueType() {
        return valueType;
    }

    public <T extends StatePropertyDefinition> T setValueType(HealthSignalValueType valueType) {
        this.valueType = valueType;
        return (T) this;
    }

    public String getUnit() {
        return unit;
    }

    public <T extends StatePropertyDefinition> T setUnit(String unit) {
        this.unit = unit;
        return (T) this;
    }
}
