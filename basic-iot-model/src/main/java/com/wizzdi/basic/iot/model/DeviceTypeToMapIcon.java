package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import com.wizzdi.maps.model.MapIcon;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "device_type_to_map_icon_idx", columnList = "deviceType_id,mapIcon_id,defaultIcon")
})
public class DeviceTypeToMapIcon extends Baseclass {

    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType deviceType;

    @ManyToOne(targetEntity = MapIcon.class)
    private MapIcon mapIcon;

    private String deviceTypeStates;

    private boolean defaultIcon;

    @ManyToOne(targetEntity = DeviceType.class)
    public DeviceType getDeviceType() {
        return deviceType;
    }

    public <T extends DeviceTypeToMapIcon> T setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }

    @ManyToOne(targetEntity = MapIcon.class)
    public MapIcon getMapIcon() {
        return mapIcon;
    }

    public <T extends DeviceTypeToMapIcon> T setMapIcon(MapIcon mapIcon) {
        this.mapIcon = mapIcon;
        return (T) this;
    }

    public String getDeviceTypeStates() {
        return deviceTypeStates;
    }

    public <T extends DeviceTypeToMapIcon> T setDeviceTypeStates(String deviceTypeStates) {
        this.deviceTypeStates = deviceTypeStates;
        return (T) this;
    }

    public boolean isDefaultIcon() {
        return defaultIcon;
    }

    public <T extends DeviceTypeToMapIcon> T setDefaultIcon(boolean defaultIcon) {
        this.defaultIcon = defaultIcon;
        return (T) this;
    }
}
