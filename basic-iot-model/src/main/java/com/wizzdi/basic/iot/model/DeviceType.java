package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.wizzdi.maps.model.MapIcon;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "device_type_idx",columnList = "name"),
        @Index(name = "device_type_external_id_idx",columnList = "externalId")
})
public class DeviceType extends Baseclass {

    private String externalId;

    @ManyToOne(targetEntity = MapIcon.class)
    private MapIcon defaultMapIcon;
    private boolean keepStateHistory;

    @JsonIgnore
    @OneToMany(targetEntity = DeviceTypeToMapIcon.class, mappedBy = "deviceType")
    private List<DeviceTypeToMapIcon> deviceTypeToMapIcons = new ArrayList<>();

    public String getExternalId() {
        return externalId;
    }

    public <T extends DeviceType> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    @ManyToOne(targetEntity = MapIcon.class)
    public MapIcon getDefaultMapIcon() {
        return defaultMapIcon;
    }

    public <T extends DeviceType> T setDefaultMapIcon(MapIcon defaultMapIcon) {
        this.defaultMapIcon = defaultMapIcon;
        return (T) this;
    }

    public boolean isKeepStateHistory() {
        return keepStateHistory;
    }

    public <T extends DeviceType> T setKeepStateHistory(boolean keepStateHistory) {
        this.keepStateHistory = keepStateHistory;
        return (T) this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = DeviceTypeToMapIcon.class, mappedBy = "deviceType")
    public List<DeviceTypeToMapIcon> getDeviceTypeToMapIcons() {
        return deviceTypeToMapIcons;
    }

    public <T extends DeviceType> T setDeviceTypeToMapIcons(List<DeviceTypeToMapIcon> deviceTypeToMapIcons) {
        this.deviceTypeToMapIcons = deviceTypeToMapIcons;
        return (T) this;
    }
}
