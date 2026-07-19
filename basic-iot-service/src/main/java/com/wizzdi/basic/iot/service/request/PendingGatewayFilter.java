package com.wizzdi.basic.iot.service.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.Set;

public class PendingGatewayFilter extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;
    private Set<String> externalIds =new HashSet<>();
    private Set<String> gatewayIds =new HashSet<>();
    private Boolean registered;
    private Boolean gatewayConfirmationReceived;


    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends PendingGatewayFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public Set<String> getExternalIds() {
        return externalIds;
    }

    public <T extends PendingGatewayFilter> T setExternalIds(Set<String> externalIds) {
        this.externalIds = externalIds;
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

    public Boolean getGatewayConfirmationReceived() {
        return gatewayConfirmationReceived;
    }

    public <T extends PendingGatewayFilter> T setGatewayConfirmationReceived(Boolean gatewayConfirmationReceived) {
        this.gatewayConfirmationReceived = gatewayConfirmationReceived;
        return (T) this;
    }
}
