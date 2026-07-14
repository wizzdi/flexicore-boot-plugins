package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public class RemoteGroupHealthHistoryFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> remoteGroupHealthHistoryIds = new HashSet<>();
    private Set<String> remoteGroupIds = new HashSet<>();
    private Set<String> fleetHealthPolicyIds = new HashSet<>();
    private Set<String> matchedRuleIds = new HashSet<>();
    private Integer minimumSeverityValue;
    private Integer maximumSeverityValue;
    private Boolean humanInterventionRequired;
    private OffsetDateTime intervalStart;
    private OffsetDateTime intervalEnd;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends RemoteGroupHealthHistoryFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getRemoteGroupHealthHistoryIds() { return remoteGroupHealthHistoryIds; }
    public <T extends RemoteGroupHealthHistoryFilter> T setRemoteGroupHealthHistoryIds(Set<String> remoteGroupHealthHistoryIds) { this.remoteGroupHealthHistoryIds = remoteGroupHealthHistoryIds; return (T) this; }
    public Set<String> getRemoteGroupIds() { return remoteGroupIds; }
    public <T extends RemoteGroupHealthHistoryFilter> T setRemoteGroupIds(Set<String> remoteGroupIds) { this.remoteGroupIds = remoteGroupIds; return (T) this; }
    public Set<String> getFleetHealthPolicyIds() { return fleetHealthPolicyIds; }
    public <T extends RemoteGroupHealthHistoryFilter> T setFleetHealthPolicyIds(Set<String> fleetHealthPolicyIds) { this.fleetHealthPolicyIds = fleetHealthPolicyIds; return (T) this; }
    public Set<String> getMatchedRuleIds() { return matchedRuleIds; }
    public <T extends RemoteGroupHealthHistoryFilter> T setMatchedRuleIds(Set<String> matchedRuleIds) { this.matchedRuleIds = matchedRuleIds; return (T) this; }
    public Integer getMinimumSeverityValue() { return minimumSeverityValue; }
    public <T extends RemoteGroupHealthHistoryFilter> T setMinimumSeverityValue(Integer minimumSeverityValue) { this.minimumSeverityValue = minimumSeverityValue; return (T) this; }
    public Integer getMaximumSeverityValue() { return maximumSeverityValue; }
    public <T extends RemoteGroupHealthHistoryFilter> T setMaximumSeverityValue(Integer maximumSeverityValue) { this.maximumSeverityValue = maximumSeverityValue; return (T) this; }
    public Boolean getHumanInterventionRequired() { return humanInterventionRequired; }
    public <T extends RemoteGroupHealthHistoryFilter> T setHumanInterventionRequired(Boolean humanInterventionRequired) { this.humanInterventionRequired = humanInterventionRequired; return (T) this; }
    public OffsetDateTime getIntervalStart() { return intervalStart; }
    public <T extends RemoteGroupHealthHistoryFilter> T setIntervalStart(OffsetDateTime intervalStart) { this.intervalStart = intervalStart; return (T) this; }
    public OffsetDateTime getIntervalEnd() { return intervalEnd; }
    public <T extends RemoteGroupHealthHistoryFilter> T setIntervalEnd(OffsetDateTime intervalEnd) { this.intervalEnd = intervalEnd; return (T) this; }
}
