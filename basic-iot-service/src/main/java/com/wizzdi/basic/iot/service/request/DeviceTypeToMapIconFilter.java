package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MapIcon;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@com.wizzdi.flexicore.security.validation.IdValid.List({
        @IdValid(
                targetField = "deviceTypes",
                field = "deviceTypeIds",
                fieldType = com.wizzdi.basic.iot.model.DeviceType.class),
        @IdValid(
                targetField = "mapIcons",
                field = "mapIconIds",
                fieldType = com.wizzdi.maps.model.MapIcon.class)
})
public class DeviceTypeToMapIconFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> deviceTypeIds = new HashSet<>();
    @JsonIgnore
    @TypeRetention(DeviceType.class)
    private List<DeviceType> deviceTypes;
    private Set<String> mapIconIds = new HashSet<>();
    @JsonIgnore
    @TypeRetention(MapIcon.class)
    private List<MapIcon> mapIcons;
    private Boolean defaultIcon;
    private String deviceTypeState;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends DeviceTypeToMapIconFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getDeviceTypeIds() {
        return deviceTypeIds;
    }

    public <T extends DeviceTypeToMapIconFilter> T setDeviceTypeIds(Set<String> deviceTypeIds) {
        this.deviceTypeIds = deviceTypeIds;
        return (T) this;
    }

    @JsonIgnore
    public List<DeviceType> getDeviceTypes() {
        return deviceTypes;
    }

    public <T extends DeviceTypeToMapIconFilter> T setDeviceTypes(List<DeviceType> deviceTypes) {
        this.deviceTypes = deviceTypes;
        return (T) this;
    }

    public Set<String> getMapIconIds() {
        return mapIconIds;
    }

    public <T extends DeviceTypeToMapIconFilter> T setMapIconIds(Set<String> mapIconIds) {
        this.mapIconIds = mapIconIds;
        return (T) this;
    }

    @JsonIgnore
    public List<MapIcon> getMapIcons() {
        return mapIcons;
    }

    public <T extends DeviceTypeToMapIconFilter> T setMapIcons(List<MapIcon> mapIcons) {
        this.mapIcons = mapIcons;
        return (T) this;
    }

    public Boolean getDefaultIcon() {
        return defaultIcon;
    }

    public <T extends DeviceTypeToMapIconFilter> T setDefaultIcon(Boolean defaultIcon) {
        this.defaultIcon = defaultIcon;
        return (T) this;
    }

    public String getDeviceTypeState() {
        return deviceTypeState;
    }

    public <T extends DeviceTypeToMapIconFilter> T setDeviceTypeState(String deviceTypeState) {
        this.deviceTypeState = deviceTypeState;
        return (T) this;
    }
}
