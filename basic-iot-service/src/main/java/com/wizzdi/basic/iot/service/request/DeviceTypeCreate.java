package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.RemoteHealthProfile;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.maps.model.MapIcon;

public class DeviceTypeCreate extends BasicCreate {

    private String externalId;
    private String defaultMapIconId;
    @JsonIgnore
    private MapIcon defaultMapIcon;

    private Boolean keepStateHistory;
    private String defaultHealthProfileId;
    @JsonIgnore
    private RemoteHealthProfile defaultHealthProfile;

    public String getExternalId() { return externalId; }

    public <T extends DeviceTypeCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }

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

    public String getDefaultHealthProfileId() {
        return defaultHealthProfileId;
    }

    public <T extends DeviceTypeCreate> T setDefaultHealthProfileId(String defaultHealthProfileId) {
        this.defaultHealthProfileId = defaultHealthProfileId;
        return (T) this;
    }

    @JsonIgnore
    public RemoteHealthProfile getDefaultHealthProfile() {
        return defaultHealthProfile;
    }

    public <T extends DeviceTypeCreate> T setDefaultHealthProfile(RemoteHealthProfile defaultHealthProfile) {
        this.defaultHealthProfile = defaultHealthProfile;
        return (T) this;
    }

    public Boolean getKeepStateHistory() {
        return keepStateHistory;
    }

    public <T extends DeviceTypeCreate> T setKeepStateHistory(Boolean keepStateHistory) {
        this.keepStateHistory = keepStateHistory;
        return (T) this;
    }

}

