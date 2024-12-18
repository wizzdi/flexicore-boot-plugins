package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import com.wizzdi.maps.model.MapIcon;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "device_type_idx",columnList = "name")
})
public class DeviceType extends Baseclass {

    @ManyToOne(targetEntity = MapIcon.class)
    private MapIcon defaultMapIcon;
    private boolean keepStateHistory;

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
}
