package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.RemoteGroupPopulationType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class RemoteGroupFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> remoteGroupIds = new HashSet<>();
    private Set<String> externalIds = new HashSet<>();
    private Set<String> fleetHealthPolicyIds = new HashSet<>();
    private Boolean healthEnabled;
    private Set<RemoteGroupPopulationType> populationTypes = new HashSet<>();
    private Set<String> sourceDeviceTypeIds = new HashSet<>();
    private Boolean systemManaged;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends RemoteGroupFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getRemoteGroupIds() { return remoteGroupIds; }
    public <T extends RemoteGroupFilter> T setRemoteGroupIds(Set<String> remoteGroupIds) { this.remoteGroupIds = remoteGroupIds; return (T) this; }
    public Set<String> getExternalIds() { return externalIds; }
    public <T extends RemoteGroupFilter> T setExternalIds(Set<String> externalIds) { this.externalIds = externalIds; return (T) this; }
    public Set<String> getFleetHealthPolicyIds() { return fleetHealthPolicyIds; }
    public <T extends RemoteGroupFilter> T setFleetHealthPolicyIds(Set<String> fleetHealthPolicyIds) { this.fleetHealthPolicyIds = fleetHealthPolicyIds; return (T) this; }
    public Boolean getHealthEnabled() { return healthEnabled; }
    public <T extends RemoteGroupFilter> T setHealthEnabled(Boolean healthEnabled) { this.healthEnabled = healthEnabled; return (T) this; }
    public Set<RemoteGroupPopulationType> getPopulationTypes() { return populationTypes; }
    public <T extends RemoteGroupFilter> T setPopulationTypes(Set<RemoteGroupPopulationType> populationTypes) { this.populationTypes = populationTypes; return (T) this; }
    public Set<String> getSourceDeviceTypeIds() { return sourceDeviceTypeIds; }
    public <T extends RemoteGroupFilter> T setSourceDeviceTypeIds(Set<String> sourceDeviceTypeIds) { this.sourceDeviceTypeIds = sourceDeviceTypeIds; return (T) this; }
    public Boolean getSystemManaged() { return systemManaged; }
    public <T extends RemoteGroupFilter> T setSystemManaged(Boolean systemManaged) { this.systemManaged = systemManaged; return (T) this; }
}
