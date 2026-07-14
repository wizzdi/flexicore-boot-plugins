package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.model.HealthIncidentStatus;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.time.OffsetDateTime;
import java.util.Set;

public class HealthIncidentFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> healthIncidentIds;
    private Set<String> remoteIds;
    private Set<String> remoteGroupIds;
    private Set<HealthIncidentStatus> statuses;
    private Set<String> assignedUserIds;
    private Integer minimumSeverityValue;
    private Boolean actionRequired;
    private Boolean healthRecovered;
    private OffsetDateTime openedAfter;
    private OffsetDateTime openedBefore;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends HealthIncidentFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getHealthIncidentIds() { return healthIncidentIds; }
    public <T extends HealthIncidentFilter> T setHealthIncidentIds(Set<String> healthIncidentIds) { this.healthIncidentIds = healthIncidentIds; return (T) this; }
    public Set<String> getRemoteIds() { return remoteIds; }
    public <T extends HealthIncidentFilter> T setRemoteIds(Set<String> remoteIds) { this.remoteIds = remoteIds; return (T) this; }
    public Set<String> getRemoteGroupIds() { return remoteGroupIds; }
    public <T extends HealthIncidentFilter> T setRemoteGroupIds(Set<String> remoteGroupIds) { this.remoteGroupIds = remoteGroupIds; return (T) this; }
    public Set<HealthIncidentStatus> getStatuses() { return statuses; }
    public <T extends HealthIncidentFilter> T setStatuses(Set<HealthIncidentStatus> statuses) { this.statuses = statuses; return (T) this; }
    public Set<String> getAssignedUserIds() { return assignedUserIds; }
    public <T extends HealthIncidentFilter> T setAssignedUserIds(Set<String> assignedUserIds) { this.assignedUserIds = assignedUserIds; return (T) this; }
    public Integer getMinimumSeverityValue() { return minimumSeverityValue; }
    public <T extends HealthIncidentFilter> T setMinimumSeverityValue(Integer minimumSeverityValue) { this.minimumSeverityValue = minimumSeverityValue; return (T) this; }
    public Boolean getActionRequired() { return actionRequired; }
    public <T extends HealthIncidentFilter> T setActionRequired(Boolean actionRequired) { this.actionRequired = actionRequired; return (T) this; }
    public Boolean getHealthRecovered() { return healthRecovered; }
    public <T extends HealthIncidentFilter> T setHealthRecovered(Boolean healthRecovered) { this.healthRecovered = healthRecovered; return (T) this; }
    public OffsetDateTime getOpenedAfter() { return openedAfter; }
    public <T extends HealthIncidentFilter> T setOpenedAfter(OffsetDateTime openedAfter) { this.openedAfter = openedAfter; return (T) this; }
    public OffsetDateTime getOpenedBefore() { return openedBefore; }
    public <T extends HealthIncidentFilter> T setOpenedBefore(OffsetDateTime openedBefore) { this.openedBefore = openedBefore; return (T) this; }
}
