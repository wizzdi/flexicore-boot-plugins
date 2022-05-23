package com.wizzdi.basic.iot.service.response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChangeStateResponse {

    private List<ChangeStateResponseEntry> devicesExecutedOn=new ArrayList<>();

    public ChangeStateResponse(List<ChangeStateResponseEntry> devicesExecutedOn) {
        this.devicesExecutedOn = devicesExecutedOn;
    }

    public ChangeStateResponse() {
    }

    public List<ChangeStateResponseEntry> getDevicesExecutedOn() {
        return devicesExecutedOn;
    }

    public <T extends ChangeStateResponse> T setDevicesExecutedOn(List<ChangeStateResponseEntry> devicesExecutedOn) {
        this.devicesExecutedOn = devicesExecutedOn;
        return (T) this;
    }
}
