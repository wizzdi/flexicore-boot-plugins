package com.wizzdi.basic.iot.service.request;

import com.wizzdi.dynamic.properties.converter.postgresql.DynamicFilterItem;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.time.OffsetDateTime;
import java.util.Map;

public class StateHistoryFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;

    private RemoteFilter remoteFilter;

    private OffsetDateTime timeAtStateFrom;
    private OffsetDateTime timeAtStateTo;

    private Map<String,DynamicFilterItem> devicePropertiesFilter;
    private Map<String,DynamicFilterItem> userAddedPropertiesFilter;




    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends StateHistoryFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }


    public OffsetDateTime getTimeAtStateTo() {
        return timeAtStateTo;
    }

    public <T extends StateHistoryFilter> T setTimeAtStateTo(OffsetDateTime timeAtStateTo) {
        this.timeAtStateTo = timeAtStateTo;
        return (T) this;
    }

    public Map<String, DynamicFilterItem> getDevicePropertiesFilter() {
        return devicePropertiesFilter;
    }

    public <T extends StateHistoryFilter> T setDevicePropertiesFilter(Map<String, DynamicFilterItem> devicePropertiesFilter) {
        this.devicePropertiesFilter = devicePropertiesFilter;
        return (T) this;
    }

    public Map<String, DynamicFilterItem> getUserAddedPropertiesFilter() {
        return userAddedPropertiesFilter;
    }

    public <T extends StateHistoryFilter> T setUserAddedPropertiesFilter(Map<String, DynamicFilterItem> userAddedPropertiesFilter) {
        this.userAddedPropertiesFilter = userAddedPropertiesFilter;
        return (T) this;
    }


    public OffsetDateTime getTimeAtStateFrom() {
        return timeAtStateFrom;
    }

    public <T extends StateHistoryFilter> T setTimeAtStateFrom(OffsetDateTime timeAtStateFrom) {
        this.timeAtStateFrom = timeAtStateFrom;
        return (T) this;
    }

    public RemoteFilter getRemoteFilter() {
        return remoteFilter;
    }

    public <T extends StateHistoryFilter> T setRemoteFilter(RemoteFilter remoteFilter) {
        this.remoteFilter = remoteFilter;
        return (T) this;
    }
}
