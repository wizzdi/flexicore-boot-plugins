package com.wizzdi.basic.iot.service.request;

public class ApproveGatewaysRequest {

    private PendingGatewayFilter pendingGatewayFilter;

    public PendingGatewayFilter getPendingGatewayFilter() {
        return pendingGatewayFilter;
    }

    public <T extends ApproveGatewaysRequest> T setPendingGatewayFilter(PendingGatewayFilter pendingGatewayFilter) {
        this.pendingGatewayFilter = pendingGatewayFilter;
        return (T) this;
    }


}
