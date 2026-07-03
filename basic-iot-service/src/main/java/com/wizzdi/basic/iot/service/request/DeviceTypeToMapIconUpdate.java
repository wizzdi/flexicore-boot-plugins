package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.DeviceTypeToMapIcon;
import com.wizzdi.flexicore.security.validation.IdValid;

@com.wizzdi.flexicore.security.validation.IdValid.List({
        @IdValid(
                targetField = "deviceTypeToMapIcon",
                field = "id",
                fieldType = com.wizzdi.basic.iot.model.DeviceTypeToMapIcon.class,
                groups = {com.wizzdi.flexicore.security.validation.Update.class})
})
public class DeviceTypeToMapIconUpdate extends DeviceTypeToMapIconCreate {

    private String id;
    @JsonIgnore
    private DeviceTypeToMapIcon deviceTypeToMapIcon;

    public String getId() {
        return id;
    }

    public <T extends DeviceTypeToMapIconUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public DeviceTypeToMapIcon getDeviceTypeToMapIcon() {
        return deviceTypeToMapIcon;
    }

    public <T extends DeviceTypeToMapIconUpdate> T setDeviceTypeToMapIcon(DeviceTypeToMapIcon deviceTypeToMapIcon) {
        this.deviceTypeToMapIcon = deviceTypeToMapIcon;
        return (T) this;
    }
}
