package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.PaginationFilter;

public class StateHistoryAggRequest extends PaginationFilter {

    private StateHistoryFilter stateHistoryFilter;
    private String groupByFieldName;

    private boolean groupByRemote;
    private StateHistoryAggTimeUnit timeUnit;

    public StateHistoryFilter getStateHistoryFilter() {
        return stateHistoryFilter;
    }

    public <T extends StateHistoryAggRequest> T setStateHistoryFilter(StateHistoryFilter stateHistoryFilter) {
        this.stateHistoryFilter = stateHistoryFilter;
        return (T) this;
    }

    public String getGroupByFieldName() {
        return groupByFieldName;
    }

    public <T extends StateHistoryAggRequest> T setGroupByFieldName(String groupByFieldName) {
        this.groupByFieldName = groupByFieldName;
        return (T) this;
    }

    public StateHistoryAggTimeUnit getTimeUnit() {
        return timeUnit;
    }

    public <T extends StateHistoryAggRequest> T setTimeUnit(StateHistoryAggTimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return (T) this;
    }

    public boolean isGroupByRemote() {
        return groupByRemote;
    }

    public <T extends StateHistoryAggRequest> T setGroupByRemote(boolean groupByRemote) {
        this.groupByRemote = groupByRemote;
        return (T) this;
    }
}
