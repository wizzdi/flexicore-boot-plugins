package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(indexes = @Index(name = "health_signal_external_id_idx", columnList = "externalId"))
public class HealthSignalDefinition extends Baseclass {

    private String externalId;

    @Enumerated(EnumType.STRING)
    private HealthSignalValueType valueType;

    private String unit;
    private boolean builtIn;
    private boolean aggregatable = true;

    public String getExternalId() {
        return externalId;
    }

    public <T extends HealthSignalDefinition> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    public HealthSignalValueType getValueType() {
        return valueType;
    }

    public <T extends HealthSignalDefinition> T setValueType(HealthSignalValueType valueType) {
        this.valueType = valueType;
        return (T) this;
    }

    public String getUnit() {
        return unit;
    }

    public <T extends HealthSignalDefinition> T setUnit(String unit) {
        this.unit = unit;
        return (T) this;
    }

    public boolean isBuiltIn() {
        return builtIn;
    }

    public <T extends HealthSignalDefinition> T setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
        return (T) this;
    }

    public boolean isAggregatable() {
        return aggregatable;
    }

    public <T extends HealthSignalDefinition> T setAggregatable(boolean aggregatable) {
        this.aggregatable = aggregatable;
        return (T) this;
    }
}
