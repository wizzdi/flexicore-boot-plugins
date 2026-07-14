package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public class RemoteHealthHistoryFilter extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> remoteHealthHistoryIds = new HashSet<>();
    private Set<String> remoteIds = new HashSet<>();
    private Set<String> remoteHealthProfileIds = new HashSet<>();
    private Set<String> matchedRuleIds = new HashSet<>();
    private Integer minimumSeverityValue;
    private Integer maximumSeverityValue;
    private Boolean humanInterventionRequired;
    private OffsetDateTime intervalStart;
    private OffsetDateTime intervalEnd;

    public BasicPropertiesFilter getBasicPropertiesFilter() { return basicPropertiesFilter; }
    public <T extends RemoteHealthHistoryFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) { this.basicPropertiesFilter = basicPropertiesFilter; return (T) this; }
    public Set<String> getRemoteHealthHistoryIds() { return remoteHealthHistoryIds; }
    public <T extends RemoteHealthHistoryFilter> T setRemoteHealthHistoryIds(Set<String> remoteHealthHistoryIds) { this.remoteHealthHistoryIds = remoteHealthHistoryIds; return (T) this; }
    public Set<String> getRemoteIds() { return remoteIds; }
    public <T extends RemoteHealthHistoryFilter> T setRemoteIds(Set<String> remoteIds) { this.remoteIds = remoteIds; return (T) this; }
    public Set<String> getRemoteHealthProfileIds() { return remoteHealthProfileIds; }
    public <T extends RemoteHealthHistoryFilter> T setRemoteHealthProfileIds(Set<String> remoteHealthProfileIds) { this.remoteHealthProfileIds = remoteHealthProfileIds; return (T) this; }
    public Set<String> getMatchedRuleIds() { return matchedRuleIds; }
    public <T extends RemoteHealthHistoryFilter> T setMatchedRuleIds(Set<String> matchedRuleIds) { this.matchedRuleIds = matchedRuleIds; return (T) this; }
    public Integer getMinimumSeverityValue() { return minimumSeverityValue; }
    public <T extends RemoteHealthHistoryFilter> T setMinimumSeverityValue(Integer minimumSeverityValue) { this.minimumSeverityValue = minimumSeverityValue; return (T) this; }
    public Integer getMaximumSeverityValue() { return maximumSeverityValue; }
    public <T extends RemoteHealthHistoryFilter> T setMaximumSeverityValue(Integer maximumSeverityValue) { this.maximumSeverityValue = maximumSeverityValue; return (T) this; }
    public Boolean getHumanInterventionRequired() { return humanInterventionRequired; }
    public <T extends RemoteHealthHistoryFilter> T setHumanInterventionRequired(Boolean humanInterventionRequired) { this.humanInterventionRequired = humanInterventionRequired; return (T) this; }
    public OffsetDateTime getIntervalStart() { return intervalStart; }
    public <T extends RemoteHealthHistoryFilter> T setIntervalStart(OffsetDateTime intervalStart) { this.intervalStart = intervalStart; return (T) this; }
    public OffsetDateTime getIntervalEnd() { return intervalEnd; }
    public <T extends RemoteHealthHistoryFilter> T setIntervalEnd(OffsetDateTime intervalEnd) { this.intervalEnd = intervalEnd; return (T) this; }
}
