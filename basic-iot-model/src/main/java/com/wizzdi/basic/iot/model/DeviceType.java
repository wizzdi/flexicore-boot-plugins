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

    /** JSON array of operational severity rules. */
    @jakarta.persistence.Column(columnDefinition = "text")
    private String severityDefinitions;

    /** JSON array mapping schema validation exceptions to severities. */
    @jakarta.persistence.Column(columnDefinition = "text")
    private String validationSeverityDefinitions;

    /** JSON array of tenant/device-type fleet aggregation rules. */
    @jakarta.persistence.Column(columnDefinition = "text")
    private String fleetHealthDefinitions;

    /** NONE, ALL_STATE_CHANGES, SEVERITY_CHANGES or MATCHED_RULES. */
    private String historyRecordingPolicy;

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

    public String getSeverityDefinitions() {
        return severityDefinitions;
    }

    public <T extends DeviceType> T setSeverityDefinitions(String severityDefinitions) {
        this.severityDefinitions = severityDefinitions;
        return (T) this;
    }

    public String getValidationSeverityDefinitions() {
        return validationSeverityDefinitions;
    }

    public <T extends DeviceType> T setValidationSeverityDefinitions(String validationSeverityDefinitions) {
        this.validationSeverityDefinitions = validationSeverityDefinitions;
        return (T) this;
    }

    public String getFleetHealthDefinitions() {
        return fleetHealthDefinitions;
    }

    public <T extends DeviceType> T setFleetHealthDefinitions(String fleetHealthDefinitions) {
        this.fleetHealthDefinitions = fleetHealthDefinitions;
        return (T) this;
    }

    public String getHistoryRecordingPolicy() {
        return historyRecordingPolicy;
    }

    public <T extends DeviceType> T setHistoryRecordingPolicy(String historyRecordingPolicy) {
        this.historyRecordingPolicy = historyRecordingPolicy;
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
