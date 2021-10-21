package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class DeviceTypeCreate extends BasicCreate {

    private String stateJsonSchema;
    private Integer version;

    public String getStateJsonSchema() {
        return stateJsonSchema;
    }

    public <T extends DeviceTypeCreate> T setStateJsonSchema(String stateJsonSchema) {
        this.stateJsonSchema = stateJsonSchema;
        return (T) this;
    }

    public Integer getVersion() {
        return version;
    }

    public <T extends DeviceTypeCreate> T setVersion(Integer version) {
        this.version = version;
        return (T) this;
    }
}
