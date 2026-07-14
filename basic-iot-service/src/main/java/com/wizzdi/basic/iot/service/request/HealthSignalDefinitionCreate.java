package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthSignalValueType;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class HealthSignalDefinitionCreate extends BasicCreate {
    private String externalId;
    private HealthSignalValueType valueType;
    private String unit;
    private Boolean builtIn;
    private Boolean aggregatable;

    public String getExternalId() { return externalId; }
    public <T extends HealthSignalDefinitionCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }
    public HealthSignalValueType getValueType() { return valueType; }
    public <T extends HealthSignalDefinitionCreate> T setValueType(HealthSignalValueType valueType) { this.valueType = valueType; return (T) this; }
    public String getUnit() { return unit; }
    public <T extends HealthSignalDefinitionCreate> T setUnit(String unit) { this.unit = unit; return (T) this; }
    public Boolean getBuiltIn() { return builtIn; }
    public <T extends HealthSignalDefinitionCreate> T setBuiltIn(Boolean builtIn) { this.builtIn = builtIn; return (T) this; }
    public Boolean getAggregatable() { return aggregatable; }
    public <T extends HealthSignalDefinitionCreate> T setAggregatable(Boolean aggregatable) { this.aggregatable = aggregatable; return (T) this; }
}
