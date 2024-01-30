package com.wizzdi.basic.iot.service.triggers.events;

import com.flexicore.rules.events.ScenarioEventBase;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.maps.model.MapIcon;

public class RemoteStatusChangedTrigger extends ScenarioEventBase {

    private final Remote remote;
    private final MapIcon newStatus;
    private final MapIcon currentStatus;

    public RemoteStatusChangedTrigger(Remote remote, MapIcon newStatus, MapIcon currentStatus, SecurityContextBase securityContextBase) {
        super(securityContextBase);
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
