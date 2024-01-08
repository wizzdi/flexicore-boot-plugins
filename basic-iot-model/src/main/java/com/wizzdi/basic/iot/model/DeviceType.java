package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;
import com.wizzdi.maps.model.MapIcon;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "device_type_idx",columnList = "name")
})
public class DeviceType extends SecuredBasic {

    @ManyToOne(targetEntity = MapIcon.class)
    private MapIcon defaultMapIcon;
    private Boolean keepStateHistory;

    @ManyToOne(targetEntity = MapIcon.class)
    public MapIcon getDefaultMapIcon() {
        return defaultMapIcon;
    }

    public <T extends DeviceType> T setDefaultMapIcon(MapIcon defaultMapIcon) {
        this.defaultMapIcon = defaultMapIcon;
        return (T) this;
    }

    public Boolean getKeepStateHistory() {
        return keepStateHistory;
    }

    public DeviceType setKeepStateHistory(Boolean keepStateHistory) {
        this.keepStateHistory = keepStateHistory;
        return this;
    }
}
