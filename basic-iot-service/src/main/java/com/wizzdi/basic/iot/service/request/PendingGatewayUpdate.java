package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.PendingGateway;


public class PendingGatewayUpdate extends PendingGatewayCreate{

    private String id;
    @JsonIgnore
    private PendingGateway pendingGateway;


    public String getId() {
        return id;
    }

    public <T extends PendingGatewayUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public PendingGateway getPendingGateway() {
        return pendingGateway;
    }

    public <T extends PendingGatewayUpdate> T setPendingGateway(PendingGateway pendingGateway) {
        this.pendingGateway = pendingGateway;
        return (T) this;
    }
}
