package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.maps.model.MapIcon;

public class DeviceTypeCreate extends BasicCreate {

    private String externalId;
    private String defaultMapIconId;
    @JsonIgnore
    private MapIcon defaultMapIcon;

    private Boolean keepStateHistory;
    private String severityDefinitions;
    private String validationSeverityDefinitions;
    private String fleetHealthDefinitions;
    private String historyRecordingPolicy;

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

    public Boolean getKeepStateHistory() {
        return keepStateHistory;
    }

    public <T extends DeviceTypeCreate> T setKeepStateHistory(Boolean keepStateHistory) {
        this.keepStateHistory = keepStateHistory;
        return (T) this;
    }

    public String getSeverityDefinitions() {
        return severityDefinitions;
    }

    public <T extends DeviceTypeCreate> T setSeverityDefinitions(String severityDefinitions) {
        this.severityDefinitions = severityDefinitions;
        return (T) this;
    }

    public String getValidationSeverityDefinitions() {
        return validationSeverityDefinitions;
    }

    public <T extends DeviceTypeCreate> T setValidationSeverityDefinitions(String validationSeverityDefinitions) {
        this.validationSeverityDefinitions = validationSeverityDefinitions;
        return (T) this;
    }

    public String getFleetHealthDefinitions() {
        return fleetHealthDefinitions;
    }

    public <T extends DeviceTypeCreate> T setFleetHealthDefinitions(String fleetHealthDefinitions) {
        this.fleetHealthDefinitions = fleetHealthDefinitions;
        return (T) this;
    }

    public String getHistoryRecordingPolicy() {
        return historyRecordingPolicy;
    }

    public <T extends DeviceTypeCreate> T setHistoryRecordingPolicy(String historyRecordingPolicy) {
        this.historyRecordingPolicy = historyRecordingPolicy;
        return (T) this;
    }
}

