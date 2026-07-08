package com.wizzdi.building.studio.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.Set;

public class BuildingBundleFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> buildingBundleIds;
    private Set<String> externalIds;
    private Set<String> statuses;
    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends BuildingBundleFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getBuildingBundleIds() { return buildingBundleIds; }
    public <T extends BuildingBundleFilter> T setBuildingBundleIds(Set<String> buildingBundleIds) { this.buildingBundleIds = buildingBundleIds; return (T) this; }
    public Set<String> getExternalIds() { return externalIds; }
    public <T extends BuildingBundleFilter> T setExternalIds(Set<String> externalIds) { this.externalIds = externalIds; return (T) this; }
    public Set<String> getStatuses() { return statuses; }
    public <T extends BuildingBundleFilter> T setStatuses(Set<String> statuses) { this.statuses = statuses; return (T) this; }
}
