package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthSignalMappingOperation;
import com.wizzdi.basic.iot.model.HealthSignalSourceType;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class HealthSignalMappingCreate extends BasicCreate {
    private String id;
    private String healthSignalId;
    private HealthSignalSourceType sourceType;
    private String statePropertyId;
    private HealthSignalMappingOperation operation;
    private Double multiplier;
    private Double offset;
    private String expectedStringValue;
    private Boolean negateBoolean;
    private Integer priority;

    public String getId() { return id; }
    public <T extends HealthSignalMappingCreate> T setId(String id) { this.id = id; return (T) this; }
    public String getHealthSignalId() { return healthSignalId; }
    public <T extends HealthSignalMappingCreate> T setHealthSignalId(String healthSignalId) { this.healthSignalId = healthSignalId; return (T) this; }
    public HealthSignalSourceType getSourceType() { return sourceType; }
    public <T extends HealthSignalMappingCreate> T setSourceType(HealthSignalSourceType sourceType) { this.sourceType = sourceType; return (T) this; }
    public String getStatePropertyId() { return statePropertyId; }
    public <T extends HealthSignalMappingCreate> T setStatePropertyId(String statePropertyId) { this.statePropertyId = statePropertyId; return (T) this; }
    public HealthSignalMappingOperation getOperation() { return operation; }
    public <T extends HealthSignalMappingCreate> T setOperation(HealthSignalMappingOperation operation) { this.operation = operation; return (T) this; }
    public Double getMultiplier() { return multiplier; }
    public <T extends HealthSignalMappingCreate> T setMultiplier(Double multiplier) { this.multiplier = multiplier; return (T) this; }
    public Double getOffset() { return offset; }
    public <T extends HealthSignalMappingCreate> T setOffset(Double offset) { this.offset = offset; return (T) this; }
    public String getExpectedStringValue() { return expectedStringValue; }
    public <T extends HealthSignalMappingCreate> T setExpectedStringValue(String expectedStringValue) { this.expectedStringValue = expectedStringValue; return (T) this; }
    public Boolean getNegateBoolean() { return negateBoolean; }
    public <T extends HealthSignalMappingCreate> T setNegateBoolean(Boolean negateBoolean) { this.negateBoolean = negateBoolean; return (T) this; }
    public Integer getPriority() { return priority; }
    public <T extends HealthSignalMappingCreate> T setPriority(Integer priority) { this.priority = priority; return (T) this; }
}
