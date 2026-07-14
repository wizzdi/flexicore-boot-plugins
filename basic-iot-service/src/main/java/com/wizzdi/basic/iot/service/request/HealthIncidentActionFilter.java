package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthIncidentActionType;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.Set;

public class HealthIncidentActionFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> healthIncidentIds;
    private Set<HealthIncidentActionType> actionTypes;
    private Set<String> performedByUserIds;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends HealthIncidentActionFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getHealthIncidentIds() { return healthIncidentIds; }
    public <T extends HealthIncidentActionFilter> T setHealthIncidentIds(Set<String> healthIncidentIds) { this.healthIncidentIds = healthIncidentIds; return (T) this; }
    public Set<HealthIncidentActionType> getActionTypes() { return actionTypes; }
    public <T extends HealthIncidentActionFilter> T setActionTypes(Set<HealthIncidentActionType> actionTypes) { this.actionTypes = actionTypes; return (T) this; }
    public Set<String> getPerformedByUserIds() { return performedByUserIds; }
    public <T extends HealthIncidentActionFilter> T setPerformedByUserIds(Set<String> performedByUserIds) { this.performedByUserIds = performedByUserIds; return (T) this; }
}
