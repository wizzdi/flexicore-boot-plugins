package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.maps.model.MapIcon;

public class DeviceTypeCreate extends BasicCreate {

    private String defaultMapIconId;
    @JsonIgnore
    private MapIcon defaultMapIcon;

    public String getDefaultMapIconId() {
        return defaultMapIconId;
    }

    public <T extends DeviceTypeCreate> T setDefaultMapIconId(String defaultMapIconId) {
        this.defaultMapIconId = defaultMapIconId;
        return (T) this;
    }

    @JsonIgnore
    public MapIcon getDefaultMapIcon() {
        return defaultMapIcon;
    }

    public <T extends DeviceTypeCreate> T setDefaultMapIcon(MapIcon defaultMapIcon) {
        this.defaultMapIcon = defaultMapIcon;
        return (T) this;
    }
}
