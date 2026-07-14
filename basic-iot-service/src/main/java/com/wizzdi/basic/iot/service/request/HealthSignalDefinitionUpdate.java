package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.HealthSignalDefinition;

public class HealthSignalDefinitionUpdate extends HealthSignalDefinitionCreate {
    private String id;
    @JsonIgnore
    private HealthSignalDefinition healthSignalDefinition;

    public String getId() { return id; }
    public <T extends HealthSignalDefinitionUpdate> T setId(String id) { this.id = id; return (T) this; }
    public HealthSignalDefinition getHealthSignalDefinition() { return healthSignalDefinition; }
    public <T extends HealthSignalDefinitionUpdate> T setHealthSignalDefinition(HealthSignalDefinition healthSignalDefinition) { this.healthSignalDefinition = healthSignalDefinition; return (T) this; }
}
