package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MapIcon;

@com.wizzdi.flexicore.security.validation.IdValid.List({
        @IdValid(
                targetField = "deviceType",
                field = "deviceTypeId",
                fieldType = com.wizzdi.basic.iot.model.DeviceType.class,
                groups = {
                        com.wizzdi.flexicore.security.validation.Create.class,
                        com.wizzdi.flexicore.security.validation.Update.class
                }),
        @IdValid(
                targetField = "mapIcon",
                field = "mapIconId",
                fieldType = com.wizzdi.maps.model.MapIcon.class,
                groups = {
                        com.wizzdi.flexicore.security.validation.Create.class,
                        com.wizzdi.flexicore.security.validation.Update.class
                })
})
public class DeviceTypeToMapIconCreate extends BasicCreate {

    private String deviceTypeId;
    @JsonIgnore
    private DeviceType deviceType;
    private String mapIconId;
    @JsonIgnore
    private MapIcon mapIcon;
    private String deviceTypeStates;
    private Boolean defaultIcon;

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public <T extends DeviceTypeToMapIconCreate> T setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
        return (T) this;
    }

    @JsonIgnore
    public DeviceType getDeviceType() {
        return deviceType;
    }

    public <T extends DeviceTypeToMapIconCreate> T setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }

    public String getMapIconId() {
        return mapIconId;
    }

    public <T extends DeviceTypeToMapIconCreate> T setMapIconId(String mapIconId) {
        this.mapIconId = mapIconId;
        return (T) this;
    }

    @JsonIgnore
    public MapIcon getMapIcon() {
        return mapIcon;
    }

    public <T extends DeviceTypeToMapIconCreate> T setMapIcon(MapIcon mapIcon) {
        this.mapIcon = mapIcon;
        return (T) this;
    }

    public String getDeviceTypeStates() {
        return deviceTypeStates;
    }

    public <T extends DeviceTypeToMapIconCreate> T setDeviceTypeStates(String deviceTypeStates) {
        this.deviceTypeStates = deviceTypeStates;
        return (T) this;
    }

    public Boolean getDefaultIcon() {
        return defaultIcon;
    }

    public <T extends DeviceTypeToMapIconCreate> T setDefaultIcon(Boolean defaultIcon) {
        this.defaultIcon = defaultIcon;
        return (T) this;
    }
}
