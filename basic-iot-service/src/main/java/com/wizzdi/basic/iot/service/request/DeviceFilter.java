package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.Gateway;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeviceFilter extends RemoteFilter {


    private Set<String> gatewayIds=new HashSet<>();
    @JsonIgnore
    @TypeRetention(Gateway.class)
    private List<Gateway> gateways;
    private Set<String> deviceTypeIds=new HashSet<>();
    @JsonIgnore
    @TypeRetention(DeviceType.class)
    private List<DeviceType> deviceTypes;
    private DeviceTypeFilter deviceTypeFilter;
    @JsonIgnore
    private boolean withoutDefaultIcon;
    private Boolean verified;


    public Set<String> getGatewayIds() {
        return gatewayIds;
    }

    public <T extends DeviceFilter> T setGatewayIds(Set<String> gatewayIds) {
        this.gatewayIds = gatewayIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Gateway> getGateways() {
        return gateways;
    }

    public <T extends DeviceFilter> T setGateways(List<Gateway> gateways) {
        this.gateways = gateways;
        return (T) this;
    }

    public Set<String> getDeviceTypeIds() {
        return deviceTypeIds;
    }

    public <T extends DeviceFilter> T setDeviceTypeIds(Set<String> deviceTypeIds) {
        this.deviceTypeIds = deviceTypeIds;
        return (T) this;
    }

    @JsonIgnore
    public List<DeviceType> getDeviceTypes() {
        return deviceTypes;
    }

    public <T extends DeviceFilter> T setDeviceTypes(List<DeviceType> deviceTypes) {
        this.deviceTypes = deviceTypes;
        return (T) this;
    }

    public DeviceTypeFilter getDeviceTypeFilter() {
        return deviceTypeFilter;
    }

    public <T extends DeviceFilter> T setDeviceTypeFilter(DeviceTypeFilter deviceTypeFilter) {
        this.deviceTypeFilter = deviceTypeFilter;
        return (T) this;
    }

    @JsonIgnore
    public boolean isWithoutDefaultIcon() {
        return withoutDefaultIcon;
    }

    public <T extends DeviceFilter> T setWithoutDefaultIcon(boolean withoutDefaultIcon) {
        this.withoutDefaultIcon = withoutDefaultIcon;
        return (T) this;
    }

    public Boolean getVerified() {
        return verified;
    }

    public <T extends DeviceFilter> T setVerified(Boolean verified) {
        this.verified = verified;
        return (T) this;
    }
}
