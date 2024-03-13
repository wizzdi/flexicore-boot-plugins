package com.wizzdi.basic.iot.service.triggers.events;

import com.flexicore.model.SecurityTenant;
import com.flexicore.rules.events.ScenarioEventBase;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.maps.model.MapIcon;

import java.util.List;

public class RemoteStatusChangedTrigger extends ScenarioEventBase {

    private final Remote remote;
    private final MapIcon newStatus;
    private final MapIcon currentStatus;

    public RemoteStatusChangedTrigger(Remote remote, MapIcon newStatus, MapIcon currentStatus, List<SecurityTenant> tenants) {
        super(tenants);
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

    @Override
    public String toString() {
        return "RemoteStatusChangedTrigger{" +
                "remote=" + remote +
                ", newStatus=" + newStatus +
                ", currentStatus=" + currentStatus +
                '}';
    }
}
