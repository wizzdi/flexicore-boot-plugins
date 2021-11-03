package com.wizzdi.basic.iot.service.response;

import java.util.HashSet;
import java.util.Set;

public class ChangeStateResponse {

    private Set<String> devicesExecutedOn=new HashSet<>();

    public ChangeStateResponse(Set<String> devicesExecutedOn) {
        this.devicesExecutedOn = devicesExecutedOn;
    }

    public ChangeStateResponse() {
    }

    public Set<String> getDevicesExecutedOn() {
        return devicesExecutedOn;
    }

    public <T extends ChangeStateResponse> T setDevicesExecutedOn(Set<String> devicesExecutedOn) {
        this.devicesExecutedOn = devicesExecutedOn;
        return (T) this;
    }
}
