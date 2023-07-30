package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.ConnectivityChange;


public class ConnectivityChangeUpdate extends ConnectivityChangeCreate{

    private String id;
    @JsonIgnore
    private ConnectivityChange connectivityChange;


    public String getId() {
        return id;
    }

    public <T extends ConnectivityChangeUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public ConnectivityChange getConnectivityChange() {
        return connectivityChange;
    }

    public <T extends ConnectivityChangeUpdate> T setConnectivityChange(ConnectivityChange connectivityChange) {
        this.connectivityChange = connectivityChange;
        return (T) this;
    }
}
