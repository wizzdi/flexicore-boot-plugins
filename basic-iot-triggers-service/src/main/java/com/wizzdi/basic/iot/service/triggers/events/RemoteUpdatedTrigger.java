package com.wizzdi.basic.iot.service.triggers.events;

import com.flexicore.model.SecurityTenant;
import com.flexicore.rules.events.ScenarioEventBase;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.request.RemoteCreate;

import java.util.List;

public class RemoteUpdatedTrigger extends ScenarioEventBase {

    private final Remote remote;
    private final RemoteCreate previousState;

    public RemoteUpdatedTrigger(Remote remote, RemoteCreate previousState, List<SecurityTenant> tenants) {
        super(tenants);
        this.remote = remote;
        this.previousState = previousState;
    }

    public Remote getRemote() {
        return remote;
    }

    public RemoteCreate getPreviousState() {
        return previousState;
    }

    @Override
    public String toString() {
        return "RemoteUpdatedTrigger{" +
                "remote=" + remote +
                ", previousState=" + previousState +
                '}';
    }
}
