package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class PendingGatewayFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> gatewayIds =new HashSet<>();
    private Boolean registered;


    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends PendingGatewayFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getGatewayIds() {
        return gatewayIds;
    }

    public <T extends PendingGatewayFilter> T setGatewayIds(Set<String> gatewayIds) {
        this.gatewayIds = gatewayIds;
        return (T) this;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public <T extends PendingGatewayFilter> T setRegistered(Boolean registered) {
        this.registered = registered;
        return (T) this;
    }
}
