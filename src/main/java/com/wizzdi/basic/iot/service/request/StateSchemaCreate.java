package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class StateSchemaCreate extends BasicCreate {

    private Integer version;
    private String stateSchemaJson;
    private String deviceTypeId;
    @JsonIgnore
    private DeviceType deviceType;

    public Integer getVersion() {
        return version;
    }

    public <T extends StateSchemaCreate> T setVersion(Integer version) {
        this.version = version;
        return (T) this;
    }

    public String getStateSchemaJson() {
        return stateSchemaJson;
    }

    public <T extends StateSchemaCreate> T setStateSchemaJson(String stateSchemaJson) {
        this.stateSchemaJson = stateSchemaJson;
        return (T) this;
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public <T extends StateSchemaCreate> T setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
        return (T) this;
    }

    @JsonIgnore
    public DeviceType getDeviceType() {
        return deviceType;
    }

    public <T extends StateSchemaCreate> T setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }
}
