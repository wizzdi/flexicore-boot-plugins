package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;
import com.wizzdi.maps.model.MapIcon;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "device_type_idx",columnList = "name")
})
public class DeviceType extends SecuredBasic {

    @ManyToOne(targetEntity = MapIcon.class)
    private MapIcon defaultMapIcon;

    @ManyToOne(targetEntity = MapIcon.class)
    public MapIcon getDefaultMapIcon() {
        return defaultMapIcon;
    }

    public <T extends DeviceType> T setDefaultMapIcon(MapIcon defaultMapIcon) {
        this.defaultMapIcon = defaultMapIcon;
        return (T) this;
    }
}
