package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class FleetHealthPolicyFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> fleetHealthPolicyIds = new HashSet<>();
    private Set<String> externalIds = new HashSet<>();
    private Boolean enabled;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends FleetHealthPolicyFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getFleetHealthPolicyIds() { return fleetHealthPolicyIds; }
    public <T extends FleetHealthPolicyFilter> T setFleetHealthPolicyIds(Set<String> fleetHealthPolicyIds) { this.fleetHealthPolicyIds = fleetHealthPolicyIds; return (T) this; }
    public Set<String> getExternalIds() { return externalIds; }
    public <T extends FleetHealthPolicyFilter> T setExternalIds(Set<String> externalIds) { this.externalIds = externalIds; return (T) this; }
    public Boolean getEnabled() { return enabled; }
    public <T extends FleetHealthPolicyFilter> T setEnabled(Boolean enabled) { this.enabled = enabled; return (T) this; }
}
