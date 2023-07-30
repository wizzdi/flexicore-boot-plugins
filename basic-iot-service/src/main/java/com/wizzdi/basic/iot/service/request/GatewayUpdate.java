package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.Gateway;


public class GatewayUpdate extends GatewayCreate{

    private String id;
    @JsonIgnore
    private Gateway gateway;


    public String getId() {
        return id;
    }

    public <T extends GatewayUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public Gateway getGateway() {
        return gateway;
    }

    public <T extends GatewayUpdate> T setGateway(Gateway gateway) {
        this.gateway = gateway;
        return (T) this;
    }
}
