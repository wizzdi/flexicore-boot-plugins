package com.wizzdi.basic.iot.service.triggers.events;

import com.flexicore.rules.events.ScenarioEventBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.maps.model.MapIcon;

public class RemoteStatusChangedTrigger extends ScenarioEventBase {

    private final Remote remote;
    private final MapIcon newStatus;
    private final MapIcon currentStatus;

    public RemoteStatusChangedTrigger(Remote remote, MapIcon newStatus, MapIcon currentStatus) {
        this.remote = remote;
        this.newStatus = newStatus;
        this.currentStatus = currentStatus;
    }

    public Remote getRemote() {
        return remote;
    }

    public MapIcon getNewStatus() {
        return newStatus;
    }

    public MapIcon getCurrentStatus() {
        return currentStatus;
    }
}
